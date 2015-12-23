package org.yiwan.webcore.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.yiwan.webcore.selenium.Driver;

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

	private String suiteName;

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	protected String lastDownloadFileName;

	/**
	 * get last download file name with relative path
	 * 
	 * @return download file name
	 */
	public String getLastDownloadFileName() {
		return lastDownloadFileName;
	}

	private String j_winname = "";

	public void setJ_winname(String j_winname) {
		this.j_winname = j_winname;
	}

	public String getJ_winname() {
		return j_winname;
	}

	protected final static String DISCRIMINATOR_KEY = "testcase";

	private final static BrowserMobProxy proxy = new BrowserMobProxyServer();

	public static BrowserMobProxy getProxy() {
		return proxy;
	}

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

	public String getLogFolder() {
		return "target/" + suiteName + "/logs/";
	}

	public String getTargetDataFolder() {
		return "target/" + suiteName + "/data/";
	}

}