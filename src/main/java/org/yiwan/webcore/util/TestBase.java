package org.yiwan.webcore.util;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.yiwan.webcore.web.PageFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Kenny Wang
 */
public class TestBase {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected WebDriver driver;

    public WebDriver getDriver() {
        return driver;
    }

    protected PageFactory pageFactory = new PageFactory(this);

    public PageFactory getPageFactory() {
        return pageFactory;
    }

    protected final HashMap<String, String> testMap = new HashMap<String, String>();

    public HashMap<String, String> getTestMap() {
        return testMap;
    }

    private String currentUrl;

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String intialUrl) {
        this.baseUrl = intialUrl;
    }

    /**
     * testng test suite name
     */
    private String suiteName;

    /**
     * get testng test suite name
     *
     * @return testng suite name
     */
    public String getSuiteName() {
        return suiteName;
    }

    /**
     * set testng test suite name
     *
     * @param suiteName
     */
    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    /**
     * testng test name
     */
    private String testName;

    /**
     * get testng test name
     *
     * @return testng test name
     */
    public String getTestName() {
        return testName;
    }

    /**
     * set testng test name
     *
     * @param suiteName
     */
    public void setTestName(String suiteName) {
        this.testName = suiteName;
    }

    private String defaultDownloadFileName;

    /**
     * get default name of download file
     *
     * @return
     */
    public String getDefaultDownloadFileName() {
        return defaultDownloadFileName;
    }

    public void setDefaultDownloadFileName(String defaultDownloadFileName) {
        this.defaultDownloadFileName = defaultDownloadFileName;
    }

    private String downloadFile;

    /**
     * get last download file name with relative path
     *
     * @return download file name
     */
    public String getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(String downloadFile) {
        this.downloadFile = downloadFile;
    }

    /**
     * enable http request archive, default value should be false, true for
     * performance use
     */
    private boolean enableHAR = PropHelper.ENABLE_HAR;

    /**
     * is enable http request archive
     *
     * @return enabled or disabled
     */
    public boolean isEnableHAR() {
        return enableHAR;
    }

    /**
     * enable or disable http request archive
     *
     * @param enableHAR
     */
    public void setEnableHAR(boolean enableHAR) {
        this.enableHAR = enableHAR;
    }

    protected final static String DISCRIMINATOR_KEY = "testcase";

    /**
     * whether to skip next execution of left test methods
     */
    protected Boolean skipTest = false;

    /**
     * @return the skipTest
     */
    public Boolean getSkipTest() {
        return skipTest;
    }

    /**
     * @param skipTest the skipTest to set
     */
    public void setSkipTest(Boolean skipTest) {
        this.skipTest = skipTest;
    }

    /**
     * log the content into the report
     *
     * @param s
     */
    public void report(String s) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String now = df.format(new Date());
        Reporter.log(now + " " + this.getClass().getName() + " " + s + "<br>");
    }

    /**
     * get test case id of current instance
     *
     * @return test case id string
     */
    public String getTestCaseId() {
        return this.getClass().getSimpleName();
    }

    /**
     * get test result folder against suite name and test name
     *
     * @return relative result folder string
     */
    private String getTestResultFolder() {
        return ("target/result/" + (suiteName == null ? "" : suiteName) + "/" + (testName == null ? "" : testName)
                + "/").replace("//", "/");
    }

    /**
     * get log folder
     *
     * @return log folder string
     */
    public String getLogFolder() {
        return getTestResultFolder() + "log/";
    }

    /**
     * get target data folder
     *
     * @return target data folder string
     */
    public String getTargetDataFolder() {
        return getTestResultFolder() + "data/";
    }

    /**
     * get screenshot folder
     *
     * @return screenshot folder string
     */
    public String getScreenshotFolder() {
        return getTestResultFolder() + "screenshot/";
    }

    /**
     * get HAR folder
     *
     * @return HAR folder string
     */
    public String getHARFolder() {
        return getTestResultFolder() + "har/" + getTestCaseId() + "/";
    }

    public JavascriptExecutor getJavascriptExecutor() {
        return (JavascriptExecutor) driver;
    }

    public Wait<WebDriver> getWebDriverWait() {
        return new WebDriverWait(driver, PropHelper.TIMEOUT_INTERVAL, PropHelper.TIMEOUT_POLLING_INTERVAL)
                .ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class)
                .ignoring(UnreachableBrowserException.class);
    }

    /**
     * capture screenshot for local or remote testing
     *
     * @param result
     */
    private void captureScreenShot(ITestResult result) {
        TakesScreenshot ts = null;
        if (PropHelper.REMOTE)
            // RemoteWebDriver does not implement the TakesScreenshot class if
            // the driver does have the Capabilities to take a screenshot then
            // Augmenter will add the TakesScreenshot methods to the instance
            ts = (TakesScreenshot) (new Augmenter().augment(driver));
        else
            ts = (TakesScreenshot) driver;
        String saveTo = getScreenshotFolder() + result.getTestClass().getName() + "." + result.getName() + ".png";
        saveTo = saveTo.replaceAll("\\\\", "/");
        try {
            File screenshot = ts.getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File(saveTo));
            // Reporter.setCurrentTestResult(result);
            report(Helper.getTestReportStyle("../../../" + saveTo,
                    "<img src=\"../../../" + saveTo + "\" width=\"400\" height=\"300\"/>"));
        } catch (Exception e) {
            logger.error("capture screentshot failed due to " + e.getMessage(), e);
        }
    }

    /**
     * Invoked each time before a test will be invoked. The
     * <code>ITestResult</code> is only partially filled with the references to
     * class, method, start millis and status.
     *
     * @param result the partially filled <code>ITestResult</code>
     * @see ITestResult#STARTED
     */
    public void onTestStart(ITestResult result) {
        logger.info(result.getTestClass().getName() + "." + result.getName() + " started");
    }

    ;

    /**
     * Invoked each time a test succeeds.
     *
     * @param result <code>ITestResult</code> containing information about the run
     *               test
     * @see ITestResult#SUCCESS
     */
    public void onTestSuccess(ITestResult result) {
        logger.info(result.getTestClass().getName() + "." + result.getName() + " passed");
    }

    ;

    /**
     * Invoked each time a test fails.
     *
     * @param result <code>ITestResult</code> containing information about the run
     *               test
     * @see ITestResult#FAILURE
     */
    public void onTestFailure(ITestResult result) {
        logger.info(result.getTestClass().getName() + "." + result.getName() + " failed");
        captureScreenShot(result);
    }

    ;

    /**
     * Invoked each time a test is skipped.
     *
     * @param result <code>ITestResult</code> containing information about the run
     *               test
     * @see ITestResult#SKIP
     */
    public void onTestSkipped(ITestResult result) {
        logger.info(result.getTestClass().getName() + "." + result.getName() + " skipped");
    }

    ;

    private boolean prepareToDownload = false;

    public boolean isPrepareToDownload() {
        return prepareToDownload;
    }

    /**
     * set it to true if you want to download something from the web
     *
     * @param prepareToDownload
     */
    public void setPrepareToDownload(boolean prepareToDownload) {
        this.prepareToDownload = prepareToDownload;
    }

}