package org.yiwan.webcore.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kenny Wang
 * 
 */
public class PropHelper {

	private final static Logger logger = LoggerFactory
			.getLogger(PropHelper.class);

	private static final String BASE_CONF = "base.properties";
	private static final String BIZ_CONF = "biz.properties";
	private static final String TEST_CONF = "test.properties";

	private static Properties props = new Properties();
	static {
		load(BASE_CONF);
		load(BIZ_CONF);
		load(TEST_CONF);
	}

	// from base conf
	public static final int TIMEOUT_INTERVAL = Integer.parseInt(props
			.getProperty("timeout.interval"));
	public static final int TIMEOUT_NAVIGATION_INTERVAL = Integer
			.parseInt(props.getProperty("timeout.navigation.interval"));
	public static final int TIMEOUT_POLLING_INTERVAL = Integer.parseInt(props
			.getProperty("timeout.polling.interval"));
	public static final int TIMEOUT_DOCUMENT_COMPLETE = Integer.parseInt(props
			.getProperty("timeout.document.complete"));
	public static final Integer TEST_RETRY_COUNT = Integer.valueOf(props
			.getProperty("test.retry.count"));
	public static final String SCREENSHOT_PATH = props
			.getProperty("path.screenshot");
	public static final String SOURCE_CODE_PATH = props
			.getProperty("sourcecode.path");
	public static final String SOURCE_CODE_ENCODING = props
			.getProperty("sourcecode.encoding");

	// from biz conf
	public static final int TIMEOUT_DIALOG_APPEAR = Integer.parseInt(props
			.getProperty("timeout.dialog.appear"));
	public static final int TIMEOUT_DIALOG_DISAPPEAR = Integer.parseInt(props
			.getProperty("timeout.dialog.disappear"));

	public static final String DEFAULT_LANG = props.getProperty("lang.default");

	public static final String DATA_SCHEMA = props
			.getProperty("path.dataschema");
	public static final String SYSTEM_DATA = props
			.getProperty("path.systemdata");
	public static final String TEST_DATA_FOLDER = props
			.getProperty("path.testdata.folder");
	public static final String MAPS_FOLDER = props
			.getProperty("path.maps.folder");

	public static final String REMOTE_ADDRESS = props
			.getProperty("remote.address");

	public static final String BROWSERSTACK_URL = props
			.getProperty("browserstackurl");
	public static final String PROJECT = props.getProperty("project");
	public static final String BUILD = props.getProperty("build");
	public static final String BROWSERSTACK_LOCAL = props
			.getProperty("browserstacklocal");
	public static final String BROWSERSTACK_LOCAL_IDENTIFIER = props
			.getProperty("browserstacklocalIdentifier");
	public static final String BROWSERSTACK_DEBUG = props
			.getProperty("browserstackdebug");

	// from test conf
	public static final String BASE_URL = props.getProperty("baseUrl");

	public static final String CURRENT_LANG = props.getProperty("lang.current");

	public static final String FIREFOX_PATH = props.getProperty("path.firefox");
	public static final String CHROME_WEBDRIVER = props
			.getProperty("path.webdriver.chrome");
	public static final String IE_WEBDRIVER_X86 = props
			.getProperty("path.webdriver.ie.x86");
	public static final String IE_WEBDRIVER_X64 = props
			.getProperty("path.webdriver.ie.x64");

	public static final String DEFAULT_OS = props.getProperty("os.default");
	public static final String DEFAULT_OS_VERSION = props
			.getProperty("os.version.default");
	public static final String DEFAULT_BROSWER = props
			.getProperty("broswer.default");
	public static final String DEFAULT_IE_ARCH = props
			.getProperty("broswer.ie.arch.default");
	public static final Boolean MAXIMIZE_BROSWER = Boolean.parseBoolean(props
			.getProperty("browser.maximize"));

	public static final Boolean REMOTE = Boolean.parseBoolean(props
			.getProperty("remote"));
	public static final Boolean BROWSERSTACK = Boolean.parseBoolean(props
			.getProperty("browserstack"));

	/**
	 * load properties from external file
	 * 
	 * @param path
	 */
	private static void load(String path) {
		try {
			logger.info("get conf from " + path);
			props.load(ClassLoader.getSystemResourceAsStream(path));
		} catch (IOException e) {
			logger.warn("conf file " + path + " doesn't exist");
		}
	}
}
