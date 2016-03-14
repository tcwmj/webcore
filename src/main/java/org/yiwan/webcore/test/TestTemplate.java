package org.yiwan.webcore.test;

import org.openqa.selenium.WebDriver;
import org.yiwan.webcore.perf.*;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.util.ProxyWrapper;
import org.yiwan.webcore.web.WebDriverFactory;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class TestTemplate implements ITestTemplate {

    private Subject subject;
    private ProxyWrapper proxyWrapper;
    private WebDriver driver;
    private boolean prepareToDownload = false;
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

    public TestTemplate() {
        driver = new WebDriverFactory().createWebDriver();
        proxyWrapper = new ProxyWrapper();
        subject = new TransactionSubject(this);
        if (PropHelper.ENABLE_DOWNLOAD) {
            subject.attach(new FileDownloadObserver(this));
        }
        if (PropHelper.ENABLE_HAR) {
            subject.attach(new HttpArchiveObserver(proxyWrapper));
        }
        subject.attach(new TimestampObserver(proxyWrapper));
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

}
