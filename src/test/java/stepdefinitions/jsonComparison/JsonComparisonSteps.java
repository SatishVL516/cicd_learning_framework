package stepdefinitions.jsonComparison;

import com.pages.jsonComparison.JsonCompareCodePage;
import io.cucumber.java.en.Then;

import java.io.IOException;

public class JsonComparisonSteps {
    JsonCompareCodePage jsonCompareCodePage = new JsonCompareCodePage();

    @Then("the user compares the previous merged json with the latest merged json {string}")
    public void theUserComparesThePreviousMergedJsonWithTheLatestMergedJson(String stationName) throws IOException {
        jsonCompareCodePage.comparePreviousAndLatestMergedJsonFiles(stationName);
    }
}
