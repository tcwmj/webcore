package org.yiwan.webcore.test;

import org.openqa.selenium.WebDriver;
import org.yiwan.webcore.perf.Subject;
import org.yiwan.webcore.util.ProxyWrapper;

import java.util.HashMap;

/**
 * Created by Kenny Wang on 3/2/2016.
 */
public interface ITestBase {

    WebDriver getWebDriver();

    ProxyWrapper getProxyWrapper();

    boolean isPrepareToDownload();

    void setPrepareToDownload(boolean prepareToDownload);

    boolean isRecordTransactionTimestamp();

    void setRecordTransactionTimestamp(boolean recordTransactionTimestamp);

    boolean isRecordHttpArchive();

    void setRecordHttpArchive(boolean recordHttpArchive);

    String getDownloadFile();

    void setDownloadFile(String downloadFile);

    String getDefaultDownloadFileName();

    void setDefaultDownloadFileName(String defaultDownloadFileName);

    String getInitialPageRef();

    void setInitialPageRef(String initialPageRef);

    Subject getSubject();

    HashMap<String, String> getTestMap();

    TestEnvironment getTestEnvironment();

    void setTestEnvironment(TestEnvironment testEnvironment);

    boolean isRecycleTestEnvironment();

    void setRecycleTestEnvironment(boolean recycleTestEnvironment);

    ITestDataManager getTestDataManager();

    void setTestDataManager(ITestDataManager testData);
}
