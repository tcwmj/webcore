package org.yiwan.webcore.test;

import org.openqa.selenium.WebDriver;
import org.yiwan.webcore.perf.Subject;
import org.yiwan.webcore.util.ProxyWrapper;

/**
 * Created by Kenny Wang on 3/2/2016.
 */
public interface ITestTemplate {
    WebDriver getWebDriver();

    ProxyWrapper getProxyWrapper();

    boolean isPrepareToDownload();

    void setPrepareToDownload(boolean prepareToDownload);

    String getDownloadFile();

    void setDownloadFile(String downloadFile);

    String getDefaultDownloadFileName();

    void setDefaultDownloadFileName(String defaultDownloadFileName);

    String getInitialPageRef();

    void setInitialPageRef(String initialPageRef);

    Subject getSubject();

}
