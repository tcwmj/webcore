package org.yiwan.webcore.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.yiwan.webcore.selenium.Driver;
import org.yiwan.webcore.util.Helper;
import org.yiwan.webcore.util.Property;

/**
 * @author Kenny Wang
 * 
 */
public class TestCaseTemplate {

	protected Logger logger = Helper.getTestCaseLogger(this.getClass());
	protected Driver driver;
	protected String currentUrl = "";

	public String getCurrentUrl() {
		return currentUrl;
	}

	public void setCurrentUrl(String currentUrl) {
		this.currentUrl = currentUrl;
	}

	/**
	 * @return the driver
	 */
	public Driver getDriver() {
		return driver;
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
	 * get test data file by specified test case id
	 * 
	 * @param testCaseId
	 * @param isSource
	 *            true indicates source file, false indicates target file
	 * @return
	 */
	protected File getTestData(String testCaseId, Boolean isSource) {
		if (testCaseId == null)
			throw new IllegalArgumentException("parameter testCaseId was null!");
		if (isSource) {
			return new File("data/" + testCaseId + ".xml");
		} else {
			return new File("target/data/" + testCaseId + ".xml");
		}
	}

	@BeforeClass
	@Parameters({ "os", "os_version", "browser", "browser_version",
			"resolution", "url" })
	protected void setUp(@Optional String os, @Optional String os_version,
			@Optional String browser, @Optional String browser_version,
			@Optional String resolution, @Optional String url) {
		logger.info("setup");

		driver = new Driver(this, os, os_version, browser, browser_version,
				resolution);

		if (new File("data/" + getTestCaseId() + ".xml").exists())
			report(Helper.getTestReportStyle("../../../data/" + getTestCaseId()
					+ ".xml", "open source test data"));
		report(Helper.getTestReportStyle("../../../target/data/"
				+ getTestCaseId() + ".xml", "open target test data"));

		if (url == null) {
			String serverUrl = System.getProperty("server.url");
			if (serverUrl == null) {
				report(Helper.getTestReportStyle(Property.BASE_URL,
						"open test server url by property config"));
				driver.navigateTo(Property.BASE_URL);
			} else {
				report(Helper.getTestReportStyle(serverUrl,
						"open test server url by system property server.url"));
				driver.navigateTo(serverUrl);
			}
		} else {
			report(Helper.getTestReportStyle(url,
					"open test server url by testng config"));
			driver.navigateTo(url);
		}
	}

	/**
	 * log the content into the report
	 * 
	 * @param s
	 */
	public void report(String s) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String now = df.format(new Date());
		logger.info(s);
		Reporter.log(now + " " + this.getClass().getName() + " " + s + "<br>");
	}

	@AfterClass
	protected void tearDown() {
		logger.info("teardown");
		driver.quit();
	}

	/**
	 * get test case id of current instance
	 * 
	 * @return
	 */
	public String getTestCaseId() {
		return this.getClass().getSimpleName();
	}

}