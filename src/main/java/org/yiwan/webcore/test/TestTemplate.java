package org.yiwan.webcore.test;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.yiwan.webcore.perf.*;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.util.ProxyWrapper;
import org.yiwan.webcore.web.WebDriverFactory;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public abstract class TestTemplate implements ITestTemplate {

    private Subject subject;
    private ProxyWrapper proxyWrapper;
    private WebDriver driver;
    private boolean prepareToDownload = false;
    private boolean recordTransactionTimestamp = false;
    private boolean recordHttpArchive = false;
    private ITestData testData;
    private String scenarioId;
    private String featureId;

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

    protected void createWebDriver() {
        driver = new WebDriverFactory().createWebDriver();
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

    public abstract void embedScreenshot();

    public abstract void embedLog();

    public abstract void embedHtml(String html);

    public abstract void embedXml(Object object);

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
}
