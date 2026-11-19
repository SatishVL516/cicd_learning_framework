package com.pages.jsonComparison;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.utils.CommonMethods;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class JsonCompareCodePage {
    // Location values closer than this can contribute partial evidence when pairing similar array items.
    private static final double LOCATION_MATCH_TOLERANCE = 0.1;
    // Reject fuzzy container pairs below this score to avoid pairing items based on weak similarity alone.
    // This is a tuning threshold; changing it should be validated against representative comparison reports.
    private static final double MIN_CONTAINER_SIMILARITY = 0.35;
    // Treat scores within this tiny delta as equal to avoid floating-point rounding noise affecting tie detection.
    private static final double SIMILARITY_SCORE_EPSILON = 1e-9;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Set<String> IGNORED_JSON_KEYS = Set.of(
            "update_timestamps", "Communication Points", "regenerate_persistence_relation_info");
    private static final Set<String> UUID_ID_KEYS = Set.of(
            "station_uuid", "connected_station_uuid", "Station Uuid", "id", "new_id",
            "rfid_tag", "lastDrawnRfidTag", "start_uuid", "end_uuid", "tag_id",
            "minimum_station_distance_uuid", "maximum_station_distance_uuid", "central_line_uuid");
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", Pattern.CASE_INSENSITIVE);

    // Main Method
    public static void main(String[] args) throws IOException {
        String stationName = "HARDA-QA";
        String oldJsonFilePath = "/home/satish/Desktop/Automation Folders/Kavach-Automation/src/test/resources/testFiles/jsonComparisonFiles/previousJsonFiles/HARDA-QA_MERGED_OLD.json";
        String newJsonFilePath = "/home/satish/Desktop/Automation Folders/Kavach-Automation/src/test/resources/testFiles/jsonComparisonFiles/latestJsonFiles/HARDA-QA_MERGED.json";
        new JsonCompareCodePage().comparePreviousAndLatestMergedJsonFiles(stationName, oldJsonFilePath, newJsonFilePath);
    }

    public void comparePreviousAndLatestMergedJsonFiles(String stationName) throws IOException {
        String oldJsonFilePath = CommonMethods.getClasspathFileAsTempPath("jsonComparisonFiles/previousJsonFiles/" + stationName + "_MERGED_OLD.json");
        String newJsonFilePath = Paths.get(CommonMethods.createAndGetTestFilesSubdirectory("jsonComparisonFiles/latestJsonFiles"), stationName + "_MERGED.json").toString();
        new JsonCompareCodePage().comparePreviousAndLatestMergedJsonFiles(stationName, oldJsonFilePath, newJsonFilePath);
    }

    // Execution
    public void comparePreviousAndLatestMergedJsonFiles(String stationName, String oldJsonFilePath, String newJsonFilePath) throws IOException {
        Path resultsDirectory = createResultsDirectory(stationName);
        Path sortedOldJsonPath = resultsDirectory.resolve(stationName + "_old_sorted.json");
        Path sortedNewJsonPath = resultsDirectory.resolve(stationName + "_new_sorted.json");
        Path reportFile = resultsDirectory.resolve(stationName + "_comparison.txt");

        // Sort old and new JSON files
        createSortedInputJsonFileCopy(oldJsonFilePath, sortedOldJsonPath);
        createSortedInputJsonFileCopy(newJsonFilePath, sortedNewJsonPath);

        // Read and normalize JSON files
        JsonNode oldJson = normalizeJson(readJsonFile(sortedOldJsonPath));
        JsonNode newJson = normalizeJson(readJsonFile(sortedNewJsonPath));

        // Compare JSON files
        List<String> differences = compareJsons(oldJson, newJson);

        // Generate comparison report
        String report = buildReport(stationName, differences);
        Files.writeString(reportFile, report, StandardCharsets.UTF_8);
//        System.out.print(report);
    }

    private static JsonNode readJsonFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("JSON file not found: " + filePath);
        }

        JsonNode json;
        try {
            json = mapper.readTree(filePath.toFile());
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IOException("JSON file contains invalid JSON: " + filePath, e);
        } catch (IOException e) {
            throw new IOException("Failed to read JSON file: " + filePath, e);
        }

        if (json == null) {
            throw new IOException("JSON file is empty: " + filePath);
        }
        if (json.isNull()) {
            throw new IOException("JSON file contains root null instead of a JSON document: " + filePath);
        }

        return json;
    }

    // Helper Methods
    // HM1
    private Path createResultsDirectory(String stationName) throws IOException {
        Path resultsDirectory = Paths.get(CommonMethods.createAndGetTestFilesSubdirectory("jsonComparisonFiles/results")).resolve(stationName);
        Files.createDirectories(resultsDirectory);
        return resultsDirectory;
    }

    // HM2
    private static void createSortedInputJsonFileCopy(String inputFilePath, Path outputFilePath) throws IOException {
        JsonNode json = sortMainKeys(readJsonFile(Path.of(inputFilePath)));
        mapper.writer(new ArrayObjectPrettyPrinter()).writeValue(outputFilePath.toFile(), json);
    }

    // HM3
    private JsonNode normalizeJson(JsonNode node) {
        if (node.isObject()) {
            ObjectNode normalized = mapper.createObjectNode();
            node.fields().forEachRemaining(field -> {
                if (!IGNORED_JSON_KEYS.contains(field.getKey())) {
                    normalized.set(field.getKey(), normalizeJson(field.getValue()));
                }
            });
            return normalized;
        }
        if (node.isArray()) {
            com.fasterxml.jackson.databind.node.ArrayNode normalized = mapper.createArrayNode();
            node.forEach(value -> normalized.add(normalizeJson(value)));
            return normalized;
        }
        if (node.isTextual()) {
            String text = node.textValue().trim();
            if ((text.startsWith("{") && text.endsWith("}")) || (text.startsWith("[") && text.endsWith("]"))) {
                try {
                    return normalizeJson(mapper.readTree(text));
                } catch (IOException ignored) {
                    // Keep non-JSON text unchanged.
                }
            }
        }
        return node;
    }

    // HM4
    private List<String> compareJsons(JsonNode oldJson, JsonNode newJson) {
        List<String> differences = new ArrayList<>();
        compareNodes(oldJson, newJson, "", differences);
        return differences;
    }

    // HM5
    private String buildReport(String stationName, List<String> differences) {
        long modified = differences.stream().filter(value -> value.startsWith("[MODIFIED]")).count();
        long added = differences.stream().filter(value -> value.startsWith("[ADDED]")).count();
        long deleted = differences.stream().filter(value -> value.startsWith("[DELETED]")).count();

        StringBuilder report = new StringBuilder("=== DIFFERENCE TOTALS ===").append("\n")
                .append("STATION: ").append(stationName).append("\n")
                .append("SAME KEYS BUT VALUE DIFFERS: ").append(modified).append("\n")
                .append("ADDED IN NEW FILE: ").append(added).append("\n")
                .append("REMOVED IN NEW FILE: ").append(deleted).append("\n").append("\n");
        for (String difference : differences) report.append(difference).append("\n").append("\n");
        return report.toString();
    }

    // Sub helpers
    private static JsonNode sortMainKeys(JsonNode json) {
        if (!json.isObject()) {
            return json;
        }

        Map<String, JsonNode> sortedFields = new TreeMap<>();
        json.fields().forEachRemaining(field -> sortedFields.put(field.getKey(), field.getValue()));

        ObjectNode sortedJson = mapper.createObjectNode();
        sortedFields.forEach(sortedJson::set);
        return sortedJson;
    }

    private void compareNodes(JsonNode oldNode, JsonNode newNode, String path, List<String> differences) {
        if (canonicalJson(oldNode).equals(canonicalJson(newNode))) return;

        if (oldNode.isObject() && newNode.isObject()) {
            Set<String> keys = new LinkedHashSet<>();
            oldNode.fieldNames().forEachRemaining(keys::add);
            newNode.fieldNames().forEachRemaining(keys::add);
            for (String key : keys) {
                JsonNode oldValue = oldNode.get(key);
                JsonNode newValue = newNode.get(key);
                String escapedKey = escapePathKey(key);
                String childPath = path.isEmpty() ? escapedKey : path + "/" + escapedKey;
                if (oldValue == null) {
                    addDifference("ADDED", childPath, null, newValue, differences);
                } else if (newValue == null) {
                    addDifference("DELETED", childPath, oldValue, null, differences);
                } else if (oldValue.equals(newValue) || !isUuidValueEquivalent(key, oldValue, newValue)) {
                    compareNodes(oldValue, newValue, childPath, differences);
                }
            }
            return;
        }

        if (oldNode.isArray() && newNode.isArray()) {
            compareArrays(oldNode, newNode, path, differences);
            return;
        }

        addDifference("MODIFIED", path.isEmpty() ? "$" : path, oldNode, newNode, differences);
    }

    private void compareArrays(JsonNode oldArray, JsonNode newArray, String path, List<String> differences) {
        int[] oldToNew = new int[oldArray.size()];
        int[] newToOld = new int[newArray.size()];
        Arrays.fill(oldToNew, -1);
        Arrays.fill(newToOld, -1);

        Map<String, Deque<Integer>> newByContent = new HashMap<>();
        for (int newIndex = 0; newIndex < newArray.size(); newIndex++) {
            newByContent.computeIfAbsent(arrayMatchKey(newArray.get(newIndex)), ignored -> new ArrayDeque<>())
                    .addLast(newIndex);
        }
        for (int oldIndex = 0; oldIndex < oldArray.size(); oldIndex++) {
            Deque<Integer> matches = newByContent.get(arrayMatchKey(oldArray.get(oldIndex)));
            if (matches != null && !matches.isEmpty()) {
                int newIndex = matches.removeFirst();
                oldToNew[oldIndex] = newIndex;
                newToOld[newIndex] = oldIndex;
            }
        }

        matchSimilarContainers(oldArray, newArray, oldToNew, newToOld);

        // Compare old items in old-index order, labeling matched pairs with both indexes.
        for (int oldIndex = 0; oldIndex < oldArray.size(); oldIndex++) {
            int newIndex = oldToNew[oldIndex];
            String itemPath = newIndex < 0
                    ? path + "[old:" + oldIndex + "]"
                    : path + "[old:" + oldIndex + " → new:" + newIndex + "]";
            if (newIndex < 0) {
                addDifference("DELETED", itemPath, oldArray.get(oldIndex), null, differences);
            } else if (!canonicalJson(oldArray.get(oldIndex)).equals(canonicalJson(newArray.get(newIndex)))) {
                compareNodes(oldArray.get(oldIndex), newArray.get(newIndex), itemPath, differences);
            }
        }

        // List wholly new items last, using indexes from the new JSON.
        for (int newIndex = 0; newIndex < newArray.size(); newIndex++) {
            if (newToOld[newIndex] < 0) {
                addDifference("ADDED", path + "[new:" + newIndex + "]", null, newArray.get(newIndex), differences);
            }
        }
    }

    /**
     * Builds an exact-match key for array-item pairing. RFID identity fields are
     * omitted only for matching; the original nodes remain intact for reporting.
     */
    private String arrayMatchKey(JsonNode node) {
        return canonicalJson(withoutRfidIdentityFields(node));
    }

    private JsonNode withoutRfidIdentityFields(JsonNode node) {
        if (node.isObject()) {
            ObjectNode filtered = mapper.createObjectNode();
            Set<String> keys = new TreeSet<>();
            node.fieldNames().forEachRemaining(keys::add);
            for (String key : keys) {
                if (!isRfidIdentityKey(key)) {
                    filtered.set(key, withoutRfidIdentityFields(node.get(key)));
                }
            }
            return filtered;
        }
        if (node.isArray()) {
            com.fasterxml.jackson.databind.node.ArrayNode filtered = mapper.createArrayNode();
            node.forEach(value -> filtered.add(withoutRfidIdentityFields(value)));
            return filtered;
        }
        return node;
    }

    private void matchSimilarContainers(JsonNode oldArray, JsonNode newArray, int[] oldToNew, int[] newToOld) {
        List<Set<String>> oldTokens = new ArrayList<>(Collections.nCopies(oldArray.size(), null));
        List<Set<String>> newTokens = new ArrayList<>(Collections.nCopies(newArray.size(), null));
        for (int i = 0; i < oldArray.size(); i++) {
            if (oldToNew[i] < 0 && oldArray.get(i).isContainerNode()) oldTokens.set(i, contentTokens(oldArray.get(i)));
        }
        for (int i = 0; i < newArray.size(); i++) {
            if (newToOld[i] < 0 && newArray.get(i).isContainerNode()) newTokens.set(i, contentTokens(newArray.get(i)));
        }

        Map<String, Integer> oldTokenFrequency = tokenFrequencies(oldTokens);
        Map<String, Integer> newTokenFrequency = tokenFrequencies(newTokens);
        int itemCount = oldArray.size() + newArray.size();

        boolean matched;
        do {
            matched = false;
            for (int oldIndex = 0; oldIndex < oldArray.size(); oldIndex++) {
                if (oldToNew[oldIndex] >= 0 || oldTokens.get(oldIndex) == null) continue;
                int bestNew = -1;
                double bestScore = 0;
                boolean tied = false;
                for (int newIndex = 0; newIndex < newArray.size(); newIndex++) {
                    if (newToOld[newIndex] >= 0 || newTokens.get(newIndex) == null
                            || differentContainerType(oldArray.get(oldIndex), newArray.get(newIndex))) continue;
                    double score = similarity(oldTokens.get(oldIndex), newTokens.get(newIndex),
                            oldTokenFrequency, newTokenFrequency, itemCount);
                    if (score > bestScore + SIMILARITY_SCORE_EPSILON) {
                        bestNew = newIndex;
                        bestScore = score;
                        tied = false;
                    } else if (score > 0 && Math.abs(score - bestScore) <= SIMILARITY_SCORE_EPSILON) {
                        tied = true;
                    }
                }
                if (bestNew < 0 || tied || bestScore < MIN_CONTAINER_SIMILARITY) continue;

                int bestOld = -1;
                double reverseScore = 0;
                boolean reverseTied = false;
                for (int candidateOld = 0; candidateOld < oldArray.size(); candidateOld++) {
                    if (oldToNew[candidateOld] >= 0 || oldTokens.get(candidateOld) == null
                            || differentContainerType(oldArray.get(candidateOld), newArray.get(bestNew))) continue;
                    double score = similarity(oldTokens.get(candidateOld), newTokens.get(bestNew),
                            oldTokenFrequency, newTokenFrequency, itemCount);
                    if (score > reverseScore + SIMILARITY_SCORE_EPSILON) {
                        bestOld = candidateOld;
                        reverseScore = score;
                        reverseTied = false;
                    } else if (score > 0 && Math.abs(score - reverseScore) <= SIMILARITY_SCORE_EPSILON) {
                        reverseTied = true;
                    }
                }
                if (!reverseTied && bestOld == oldIndex && reverseScore >= MIN_CONTAINER_SIMILARITY) {
                    oldToNew[oldIndex] = bestNew;
                    newToOld[bestNew] = oldIndex;
                    matched = true;
                }
            }
        } while (matched);
    }

    private boolean differentContainerType(JsonNode left, JsonNode right) {
        return (!left.isObject() || !right.isObject()) && (!left.isArray() || !right.isArray());
    }

    private Set<String> contentTokens(JsonNode node) {
        Set<String> tokens = new java.util.HashSet<>();
        collectContentTokens(node, "", tokens);
        return tokens;
    }

    private void collectContentTokens(JsonNode node, String path, Set<String> tokens) {
        if (node.isObject()) {
            Set<String> keys = new TreeSet<>();
            node.fieldNames().forEachRemaining(keys::add);
            for (String key : keys) {
                if (isRfidIdentityKey(key)) continue;
                collectContentTokens(node.get(key), path + "/" + escapePathKey(key), tokens);
            }
        } else if (node.isArray()) {
            for (JsonNode value : node) collectContentTokens(value, path + "/*", tokens);
        } else {
            String key = path.substring(path.lastIndexOf('/') + 1);
            String value = isUuidKeyedValue(key, node)
                    ? normalizeUuidValue(node.textValue()) : canonicalJson(node);
            tokens.add(path + "=" + value);
        }
    }

    private Map<String, Integer> tokenFrequencies(List<Set<String>> tokensByItem) {
        Map<String, Integer> frequencies = new HashMap<>();
        for (Set<String> tokens : tokensByItem) {
            if (tokens != null) for (String token : tokens) frequencies.merge(token, 1, Integer::sum);
        }
        return frequencies;
    }

    private double similarity(Set<String> left, Set<String> right,
                              Map<String, Integer> oldFrequencies,
                              Map<String, Integer> newFrequencies, int itemCount) {
        if (left.isEmpty() || right.isEmpty()) return 0;
        Set<String> shared = new HashSet<>(left);
        shared.retainAll(right);
        double leftWeight = tokenWeight(left, oldFrequencies, newFrequencies, itemCount);
        double rightWeight = tokenWeight(right, oldFrequencies, newFrequencies, itemCount);
        double sharedWeight = tokenWeight(shared, oldFrequencies, newFrequencies, itemCount)
                + (nearLocationWeight(left, right, oldFrequencies, newFrequencies, itemCount)
                + nearLocationWeight(right, left, newFrequencies, oldFrequencies, itemCount)) / 2.0;
        return 2.0 * sharedWeight / (leftWeight + rightWeight);
    }

    private double nearLocationWeight(Set<String> source, Set<String> candidates,
                                      Map<String, Integer> sourceFrequencies,
                                      Map<String, Integer> candidateFrequencies, int itemCount) {
        double matchedWeight = 0;
        for (String sourceToken : source) {
            if (!isLocationToken(sourceToken)) continue;
            String sourcePath = tokenPath(sourceToken);
            Double sourceValue = numericTokenValue(sourceToken);
            if (sourceValue == null) continue;

            double bestAffinity = 0;
            for (String candidateToken : candidates) {
                if (sourceToken.equals(candidateToken) || !sourcePath.equals(tokenPath(candidateToken))) continue;
                Double candidateValue = numericTokenValue(candidateToken);
                if (candidateValue == null) continue;
                double distance = Math.abs(sourceValue - candidateValue);
                if (distance <= LOCATION_MATCH_TOLERANCE) {
                    bestAffinity = Math.max(bestAffinity, 1.0 - distance / LOCATION_MATCH_TOLERANCE);
                }
            }
            if (bestAffinity > 0) {
                matchedWeight += tokenWeight(Set.of(sourceToken), sourceFrequencies, candidateFrequencies, itemCount)
                        * bestAffinity;
            }
        }
        return matchedWeight;
    }

    private String tokenPath(String token) {
        int valueSeparator = token.indexOf('=');
        return valueSeparator >= 0 ? token.substring(0, valueSeparator) : token;
    }

    private Double numericTokenValue(String token) {
        int valueSeparator = token.indexOf('=');
        if (valueSeparator < 0) return null;
        try {
            return Double.parseDouble(token.substring(valueSeparator + 1));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private double tokenWeight(Set<String> tokens, Map<String, Integer> oldFrequencies,
                               Map<String, Integer> newFrequencies, int itemCount) {
        double weight = 0;
        for (String token : tokens) {
            int frequency = oldFrequencies.getOrDefault(token, 0) + newFrequencies.getOrDefault(token, 0);
            double rarityWeight = Math.log(1.0 + itemCount / (double) Math.max(1, frequency));
            weight += isLocationToken(token) ? rarityWeight * 2.0 : rarityWeight;
        }
        return weight;
    }

    private boolean isRfidIdentityKey(String key) {
        return "rfid_tag".equalsIgnoreCase(key)
                || "lastDrawnRfidTag".equalsIgnoreCase(key)
                || key.toLowerCase().endsWith("_associated_rfid_tag");
    }

    private boolean isLocationToken(String token) {
        int valueSeparator = token.indexOf('=');
        String tokenPath = valueSeparator >= 0 ? token.substring(0, valueSeparator) : token;
        String key = tokenPath.substring(tokenPath.lastIndexOf('/') + 1);
        return key.toLowerCase().endsWith("location") && !"offset_location".equalsIgnoreCase(key);
    }

    private String escapePathKey(String key) {
        return key.replace("~", "~0").replace("/", "~1");
    }

    private void addDifference(String status, String path, JsonNode oldValue, JsonNode newValue, List<String> differences) {
        StringBuilder entry = new StringBuilder("[").append(status).append("] ").append(path);
        if ("MODIFIED".equals(status)) {
            entry.append("\n").append("  Old: ").append(formatValue(oldValue));
            entry.append("\n").append("  New: ").append(formatValue(newValue));
        } else {
            JsonNode value = "ADDED".equals(status) ? newValue : oldValue;
            entry.append("\n").append("  Value: ").append(formatValue(value));
        }
        differences.add(entry.toString());
    }

    private String formatValue(JsonNode value) {
        if (value == null || value.isNull()) return "null";
        if (value.isTextual()) {
            try {
                return mapper.writeValueAsString(value.textValue());
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Unable to format JSON report string", e);
            }
        }
        if (value.isContainerNode()) {
            try {
                return mapper.writer(new CompactColonPrettyPrinter()).writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Unable to format JSON report value", e);
            }
        }
        return value.toString();
    }

    private static final class CompactColonPrettyPrinter extends DefaultPrettyPrinter {
        private CompactColonPrettyPrinter() {
            indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        }

        @Override
        public void writeObjectFieldValueSeparator(JsonGenerator generator) throws IOException {
            generator.writeRaw(": ");
        }

        @Override
        public void writeEndObject(JsonGenerator generator, int entryCount) throws IOException {
            if (entryCount > 0) {
                super.writeEndObject(generator, entryCount);
                return;
            }
            if (!_objectIndenter.isInline()) _nesting--;
            generator.writeRaw('}');
        }

        @Override
        public void writeEndArray(JsonGenerator generator, int entryCount) throws IOException {
            if (entryCount > 0) {
                super.writeEndArray(generator, entryCount);
                return;
            }
            if (!_arrayIndenter.isInline()) _nesting--;
            generator.writeRaw(']');
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new CompactColonPrettyPrinter();
        }
    }

    private String canonicalJson(JsonNode node) {
        try {
            return mapper.writeValueAsString(canonicalize(node));
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to canonicalize JSON", e);
        }
    }

    private JsonNode canonicalize(JsonNode node) {
        if (node.isObject()) {
            ObjectNode sorted = mapper.createObjectNode();
            Set<String> keys = new TreeSet<>();
            node.fieldNames().forEachRemaining(keys::add);
            for (String key : keys) {
                JsonNode value = node.get(key);
                if (isUuidKeyedValue(key, value)) {
                    sorted.put(key, normalizeUuidValue(value.textValue()));
                } else {
                    sorted.set(key, canonicalize(value));
                }
            }
            return sorted;
        }
        if (node.isArray()) {
            com.fasterxml.jackson.databind.node.ArrayNode sorted = mapper.createArrayNode();
            node.forEach(value -> sorted.add(canonicalize(value)));
            return sorted;
        }
        return node;
    }

    private boolean isUuidValueEquivalent(String key, JsonNode oldValue, JsonNode newValue) {
        if (!isUuidKeyedValue(key, oldValue) || !isUuidKeyedValue(key, newValue)) return false;
        String oldText = oldValue.textValue();
        String newText = newValue.textValue();
        return normalizeUuidValue(oldText).equals(normalizeUuidValue(newText));
    }

    private boolean isUuidKeyedValue(String key, JsonNode value) {
        return UUID_ID_KEYS.contains(key)
                && value.isTextual()
                && UUID_PATTERN.matcher(value.textValue()).find();
    }

    private String normalizeUuidValue(String value) {
        return UUID_PATTERN.matcher(value).replaceAll("<UUID>");
    }

    private static final class ArrayObjectPrettyPrinter extends DefaultPrettyPrinter {
        private ArrayObjectPrettyPrinter() {
            super();
            indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        }

        private ArrayObjectPrettyPrinter(ArrayObjectPrettyPrinter base) {
            super(base);
        }

        @Override
        public void writeEndObject(JsonGenerator generator, int entryCount) throws IOException {
            if (entryCount > 0) {
                super.writeEndObject(generator, entryCount);
                return;
            }
            if (!_objectIndenter.isInline()) _nesting--;
            generator.writeRaw('}');
        }

        @Override
        public void writeEndArray(JsonGenerator generator, int entryCount) throws IOException {
            if (entryCount > 0) {
                super.writeEndArray(generator, entryCount);
                return;
            }
            if (!_arrayIndenter.isInline()) _nesting--;
            generator.writeRaw(']');
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new ArrayObjectPrettyPrinter(this);
        }
    }


}
