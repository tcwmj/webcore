package org.yiwan.webcore.test;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.yiwan.webcore.proxy.*;
import org.yiwan.webcore.util.Helper;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IPageManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.WebDriverWrapperFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class TestBase {
    public final HashMap<String, String> testMap = new HashMap<String, String>();
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected boolean skipTest = false;//whether to skip next execution of left test methods
    private Subject subject;
    private IWebDriverWrapper webDriverWrapper;
    private ITestDataManager testDataManager;
    private IPageManager pageManager;
    private ProxyWrapper proxyWrapper;
    private TestCapability testCapability = new TestCapability();
    private boolean prepareToDownload = false;
    private String scenarioId;
    private String featureId;
    private TestEnvironment testEnvironment;
    private boolean recycleTestEnvironment = false;
    private String downloadFile;//last download file name by relative path
    private String defaultDownloadFileName;//default name of download file
    private String transactionName;//unique http archive file name
    private String suiteName;//testng test suite name
    private String testName;//testng test name

    public TestCapability getTestCapability() {
        return testCapability;
    }

    public IWebDriverWrapper getWebDriverWrapper() {
        return webDriverWrapper;
    }

    public IPageManager getPageManager() {
        return pageManager;
    }

    public void setPageManager(IPageManager pageManager) {
        this.pageManager = pageManager;
    }

    public String getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public HashMap<String, String> getTestMap() {
        return testMap;
    }

    public TestEnvironment getTestEnvironment() {
        return testEnvironment;
    }

    public void setTestEnvironment(TestEnvironment testEnvironment) {
        this.testEnvironment = testEnvironment;
    }

    public boolean getSkipTest() {
        return skipTest;
    }

    public void setSkipTest(boolean skipTest) {
        this.skipTest = skipTest;
    }

    public void createWebDriverWrapper() throws MalformedURLException {
        webDriverWrapper = new WebDriverWrapperFactory(testCapability).create();
        proxyWrapper = new ProxyWrapper();
        subject = new TransactionSubject(this);
        if (PropHelper.ENABLE_RECORD_TRANSACTION_TIMESTAMP) {
            subject.attach(new TimestampObserver(proxyWrapper));
        }
        if (PropHelper.ENABLE_CAPTURE_TRANSACTION_SCREENSHOT) {
            subject.attach(new ScreenshotObserver(proxyWrapper));
        }
        if (PropHelper.ENABLE_HAR) {
            subject.attach(new HttpArchiveObserver(proxyWrapper));
        }
        if (PropHelper.ENABLE_DOWNLOAD) {
            subject.attach(new FileDownloadObserver(this));
        }
    }

    public ProxyWrapper getProxyWrapper() {
        return proxyWrapper;
    }

    public boolean isPrepareToDownload() {
        return prepareToDownload;
    }

    public void setPrepareToDownload(boolean prepareToDownload) {
        this.prepareToDownload = prepareToDownload;
    }

    public String getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(String downloadFile) {
        this.downloadFile = downloadFile;
    }

    public String getDefaultDownloadFileName() {
        return defaultDownloadFileName;
    }

    public void setDefaultDownloadFileName(String defaultDownloadFileName) {
        this.defaultDownloadFileName = defaultDownloadFileName;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public ITestDataManager getTestDataManager() {
        return testDataManager;
    }

    public void setTestDataManager(ITestDataManager testDataManager) {
        this.testDataManager = testDataManager;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
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

    /**
     * Invoked each time a test fails.
     *
     * @param result <code>ITestResult</code> containing information about the run
     *               test
     * @see ITestResult#FAILURE
     */
    public void onTestFailure(ITestResult result) throws Exception {
        logger.info(result.getTestClass().getName() + "." + result.getName() + " failed");
        embedScreenshot();
    }

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

    public void embedScreenshot() throws Exception {
        String saveTo = PropHelper.SCREENSHOT_FOLDER + Helper.randomize() + ".png";
        File screenshot = webDriverWrapper.getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File(saveTo));
        // Reporter.setCurrentTestResult(result);
        report(Helper.getTestReportStyle("../../../" + saveTo,
                "<img src=\"../../../" + saveTo + "\" width=\"400\" height=\"300\"/>"));
    }

    public void embedLog() throws Exception {
    }

    public void embedTestData(Object o) throws Exception {
    }

    public boolean isRecycleTestEnvironment() {
        return recycleTestEnvironment;
    }

    public void setRecycleTestEnvironment(boolean recycleTestEnvironment) {
        this.recycleTestEnvironment = recycleTestEnvironment;
    }

    public void startTransaction(String transactionName) {
        this.transactionName = transactionName;
        subject.nodifyObserversStart();
    }

    public void stopTransaction() {
        subject.nodifyObserversStop();
    }
}
