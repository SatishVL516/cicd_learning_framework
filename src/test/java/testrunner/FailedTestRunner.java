package testrunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * FailedTestRunner — Automatically reruns only the scenarios that failed in TestRunner.
 *
 * Flow:
 * 1. TestRunner runs all scenarios → failed ones are written to target/failed_scenarios.txt
 * 2. FailedTestRunner reads that file and reruns only those failed scenarios
 * 3. Allure results are updated with rerun outcomes
 * 4. Final Allure report shows consolidated pass/fail status
 */
@CucumberOptions(
        features = {"@target/failed_scenarios.txt"},   // reads failed scenarios from TestRunner
        glue = {"stepdefinitions", "apphooks"},
        plugin = {
                "pretty",
                "timeline:test-output/",
                "html:target/rerun-report.html",       // separate HTML for rerun
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm", // appends to same allure-results
                "rerun:target/failed_scenarios2.txt"   // captures any still-failing scenarios

        },
        monochrome = true
)
public class FailedTestRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = false)  // rerun sequentially for stability
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
