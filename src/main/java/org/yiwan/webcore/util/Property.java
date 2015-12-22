package org.yiwan.webcore.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kenny Wang
 * 
 */
public class Property {

	private final static Logger logger = LoggerFactory
			.getLogger(Property.class);

	private static Properties props = null;

	private static final String PROPERTY_FILE = "config/test.properties";
	public static final int TIMEOUT_INTERVAL = Integer
			.parseInt(getProperty("timeout.interval"));
	public static final int NAVIGATION_INTERVAL = Integer
			.parseInt(getProperty("timeout.navigation.interval"));
	public static final int POLLING_INTERVAL = Integer
			.parseInt(getProperty("timeout.polling.interval"));
	public static final int DIALOG_APPEAR_INTERVAL = Integer
			.parseInt(getProperty("timeout.dialog.appear"));
	public static final int DIALOG_DISAPPEAR_INTERVAL = Integer
			.parseInt(getProperty("timeout.dialog.disappear"));
	public static final int FILE_ACCESSABLE = Integer
			.parseInt(getProperty("timeout.file.accessable"));
	public static final int TIMEOUT_DOCUMENT_COMPLETE = Integer
			.parseInt(getProperty("timeout.document.complete"));

	public static final Integer RETRY_COUNT = Integer
			.valueOf(getProperty("retryCount"));

	public static final String BASE_URL = getProperty("baseUrl");
	public static final String LANG = getProperty("lang");
	public static final String FIRST_TEST_METHOD = getProperty("firstTestMethod");

	public static final String WEB_DRIVER_CHROME = getProperty("path.webdriver.chrome");
	public static final String WEB_DRIVER_IE_X86 = getProperty("path.webdriver.ie.x86");
	public static final String WEB_DRIVER_IE_X64 = getProperty("path.webdriver.ie.x64");
	public static final String DOWNLOAD_FILE_EXE = getProperty("path.downloadfile");
	public static final String SCREENSHOT_DIR = getProperty("path.screenshot");
	public static final String DATA_SCHEMA = getProperty("path.dataschema");
	public static final String SYSTEM_DATA = getProperty("path.systemdata");
	public static final String TEST_DATA_FOLDER = getProperty("path.testdata.folder");
	public static final String MAPS_FOLDER = getProperty("path.maps.folder");
	
	public static final String WINDOWS = getProperty("system.windows");
	public static final String WINDOWS_XP = getProperty("system.windows.xp");
	public static final String WINDOWS_7 = getProperty("system.windows.7");
	public static final String WINDOWS_8 = getProperty("system.windows.8");
	public static final String WINDOWS_8_1 = getProperty("system.windows.8.1");
	public static final String OSX = getProperty("system.osx");
	public static final String CHROME = getProperty("broswer.chrome");
	public static final String FIREFOX = getProperty("broswer.firefox");
	public static final String INTERNET_EXPLORER = getProperty("broswer.ie");
	public static final String INTERNET_EXPLORER_PREFERRED = getProperty("broswer.ie.preferred");
	public static final String DEFAULT_BROSWER = getProperty("broswer.default");
	public static final Boolean MAXIMIZE_BROSWER = Boolean
			.parseBoolean(getProperty("browser.maximize"));

	public static final Boolean REMOTE = Boolean
			.parseBoolean(getProperty("remote"));
	public static final String REMOTE_ADDRESS = getProperty("remote.address");
	public static final String FIREFOX_DIR = getProperty("path.firefox");
	public static final String DOWNLOAD_WINXP_DIR = getProperty("path.download.xp");
	public static final String DOWNLOAD_WINNT_DIR = getProperty("path.download.nt");
	public static final String DOWNLOAD_OSX_DIR = getProperty("path.download.osx");

	public static final Boolean BROWSERSTACK = Boolean
			.parseBoolean(getProperty("browserstack"));
	public static final String BROWSERSTACK_URL = getProperty("browserstackurl");
	public static final String PROJECT = getProperty("project");
	public static final String BUILD = getProperty("build");
	public static final String BROWSERSTACK_LOCAL = getProperty("browserstacklocal");
	public static final String BROWSERSTACK_LOCAL_IDENTIFIER = getProperty("browserstacklocalIdentifier");
	public static final String BROWSERSTACK_DEBUG = getProperty("browserstackdebug");

	public static final String SOURCE_CODE_PATH = getProperty("sourceCode.path");
	public static final String SOURCE_CODE_ENCODING = getProperty("sourceCode.encoding");

	// public static final String testResultExcelPath = new
	// File("").getAbsolutePath()
	// + "\\target\\TestResult.xls";

	public static void getProperties() {
		logger.info("try to get test properties from external file "
				+ PROPERTY_FILE);
		props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					PROPERTY_FILE));
			props.load(in);
			in.close();
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		if (props == null) {
			getProperties();
		}
		return props.getProperty(key);
	}

}
