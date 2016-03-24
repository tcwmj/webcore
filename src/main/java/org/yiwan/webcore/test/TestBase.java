package org.yiwan.webcore.test;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.yiwan.webcore.perf.*;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.util.ProxyWrapper;
import org.yiwan.webcore.web.WebDriverFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public abstract class TestBase implements ITestBase {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Subject subject;
    private ProxyWrapper proxyWrapper;
    private WebDriver driver;
    private boolean prepareToDownload = false;
    private boolean recordTransactionTimestamp = false;
    private boolean recordHttpArchive = false;
    private ITestData testData;
    private String scenarioId;
    private String featureId;

    public final HashMap<String, String> testMap = new HashMap<String, String>();
    private String baseUrl = PropHelper.getProperty("server.url");

    private String os;
    private String osVersion;
    private String browser;
    private String browserVersion;
    private String resolution;

    /**
     * last download file name by relative path
     */
    private String downloadFile;
    /**
     * default name of download file
     */
    private String defaultDownloadFileName;
    /**
     * unique http archive file name
     */
    private String initialPageRef;

    /**
     * testng test suite name
     */
    private String suiteName;
    /**
     * testng test name
     */
    private String testName;
    /**
     * whether to skip next execution of left test methods
     */
    protected Boolean skipTest = false;

    public String getSuiteName() {
        return suiteName;
    }

    public String getTestName() {
        return testName;
    }

    @Override
    public HashMap<String, String> getTestMap() {
        return testMap;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getResolution() {
        return resolution;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public String getBrowser() {
        return browser;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getOs() {
        return os;
    }

    public Boolean getSkipTest() {
        return skipTest;
    }

    public void setSkipTest(Boolean skipTest) {
        this.skipTest = skipTest;
    }

    protected void createWebDriver() {
        driver = new WebDriverFactory(this).createWebDriver();
        proxyWrapper = new ProxyWrapper();
        subject = new TransactionSubject(this);
        if (PropHelper.ENABLE_RECORD_TRANSACTION_TIMESTAMP) {
            subject.attach(new TimestampObserver(proxyWrapper));
        }
        if (PropHelper.ENABLE_HAR) {
            subject.attach(new HttpArchiveObserver(proxyWrapper));
        }
        if (PropHelper.ENABLE_DOWNLOAD) {
            subject.attach(new FileDownloadObserver(this));
        }
    }

    @Override
    public WebDriver getWebDriver() {
        return driver;
    }

    @Override
    public ProxyWrapper getProxyWrapper() {
        return proxyWrapper;
    }

    @Override
    public boolean isPrepareToDownload() {
        return prepareToDownload;
    }

    @Override
    public void setPrepareToDownload(boolean prepareToDownload) {
        this.prepareToDownload = prepareToDownload;
    }

    @Override
    public boolean isRecordHttpArchive() {
        return recordHttpArchive;
    }

    @Override
    public void setRecordHttpArchive(boolean recordHttpArchive) {
        this.recordHttpArchive = recordHttpArchive;
    }

    @Override
    public boolean isRecordTransactionTimestamp() {
        return recordTransactionTimestamp;
    }

    @Override
    public void setRecordTransactionTimestamp(boolean recordTransactionTimestamp) {
        this.recordTransactionTimestamp = recordTransactionTimestamp;
    }

    @Override
    public Subject getSubject() {
        return subject;
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

    public String getInitialPageRef() {
        return initialPageRef;
    }

    public void setInitialPageRef(String initialPageRef) {
        this.initialPageRef = initialPageRef;
    }

    public ITestData getTestData() {
        return testData;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setTestData(ITestData testData) {
        this.testData = testData;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    /**
     * capture screenshot for local or remote testing
     *
     * @return screenshot TakesScreenshot
     */
    protected TakesScreenshot getTakesScreenshot() {
        TakesScreenshot ts = null;
        if (PropHelper.REMOTE)
            // RemoteWebDriver does not implement the TakesScreenshot class if
            // the driver does have the Capabilities to take a screenshot then
            // Augmenter will add the TakesScreenshot methods to the instance
            ts = (TakesScreenshot) (new Augmenter().augment(getWebDriver()));
        else
            ts = (TakesScreenshot) getWebDriver();
        return ts;
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
    public void onTestFailure(ITestResult result) {
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

    public abstract void embedScreenshot();

    public abstract void embedLog();

    public abstract void embedHtml(String html);

    public abstract void embedXml(Object object);

    @BeforeClass
    @Parameters({"os", "os_version", "browser", "browser_version", "resolution"})
    protected void beforeClass(ITestContext testContext, @Optional String os, @Optional String os_version, @Optional String browser, @Optional String browser_version, @Optional String resolution) {
        this.suiteName = testContext.getCurrentXmlTest().getSuite().getName();
        this.testName = testContext.getCurrentXmlTest().getName();
        this.os = os;
        this.osVersion = os_version;
        this.browser = browser;
        this.browserVersion = browser_version;
        this.resolution = resolution;
        setFeatureId(this.getClass().getSimpleName().toLowerCase());
    }
}
