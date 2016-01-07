package org.yiwan.webcore.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.yiwan.webcore.selenium.Driver;
import org.yiwan.webcore.util.ProxyHelper;

/**
 * @author Kenny Wang
 * 
 */
public class TestCaseBase {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Driver driver;

	/**
	 * get wrapper driver
	 * 
	 * @return the driver
	 */
	public Driver getDriver() {
		return driver;
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

	private String j_winname = "";

	public void setJ_winname(String j_winname) {
		this.j_winname = j_winname;
	}

	public String getJ_winname() {
		return j_winname;
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
	 * @param skipTest
	 *            the skipTest to set
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
	 * @return
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

	@BeforeSuite
	protected void beforeSuite() {
		ProxyHelper.startProxy();
		ProxyHelper.supportFileDownload(this);
	}

	@AfterSuite
	protected void afterSuite() {
		ProxyHelper.stopProxy();
	}

}