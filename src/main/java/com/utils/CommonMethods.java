package com.utils;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


public class CommonMethods {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CommonMethods(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }
    public static String getClasspathFileAsTempPath(String relativePath) {
        String normalized = relativePath.replace("\\", "/");
        try (InputStream stream = CommonMethods.class.getClassLoader()
                .getResourceAsStream("testFiles/" + normalized)) {
            if (stream == null) {
                throw new IllegalArgumentException("File not found in testFiles resources: " + normalized);
            }
            String fileName = Paths.get(normalized).getFileName().toString();
            Path tempFile = Files.createTempFile("testfile-", "-" + fileName);
            tempFile.toFile().deleteOnExit();
            Files.copy(stream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract file: " + relativePath, e);
        }
    }

    public static String createAndGetTestFilesSubdirectory(String relativeSubfolder) {
        Path testFilesDir = Paths.get(
                System.getProperty("user.dir"),
                "src", "test", "resources", "testFiles"
        ).toAbsolutePath().normalize();
        Path downloadDir = testFilesDir.resolve(relativeSubfolder).normalize();

        if (!downloadDir.startsWith(testFilesDir)) {
            throw new IllegalArgumentException("Download subfolder must stay under testFiles: " + relativeSubfolder);
        }

        try {
            Files.createDirectories(downloadDir);
            return downloadDir.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create download directory: " + downloadDir, e);
        }
    }

    public void isElementDisplayed(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

}
