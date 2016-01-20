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

	private final static Logger logger = LoggerFactory.getLogger(PropHelper.class);

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
	public static final int TIMEOUT_INTERVAL = Integer.parseInt(getProperty("timeout.interval"));
	public static final int TIMEOUT_NAVIGATION_INTERVAL = Integer.parseInt(getProperty("timeout.navigation.interval"));
	public static final int TIMEOUT_POLLING_INTERVAL = Integer.parseInt(getProperty("timeout.polling.interval"));
	public static final int TIMEOUT_DOCUMENT_COMPLETE = Integer.parseInt(getProperty("timeout.document.complete"));
	public static final Integer TEST_RETRY_COUNT = Integer.valueOf(getProperty("test.retry.count"));
	public static final String RANDOM_RULE = getProperty("random.rule");
	public static final String SOURCE_CODE_PATH = getProperty("sourcecode.path");
	public static final String SOURCE_CODE_ENCODING = getProperty("sourcecode.encoding");
	public static final String REPLACEMENT_SYMBOL = getProperty("symbol.replacement");
	public static final String LOCATOR_SCHEMA = getProperty("path.locator.schema");

	// from biz conf
	public static final int TIMEOUT_DIALOG_APPEAR = Integer.parseInt(getProperty("timeout.dialog.appear"));
	public static final int TIMEOUT_DIALOG_DISAPPEAR = Integer.parseInt(getProperty("timeout.dialog.disappear"));

	public static final String DEFAULT_LANG = getProperty("lang.default");

	public static final String DATA_SCHEMA_FILE = getProperty("path.data.schema");
	public static final String LOCATORS_FILE = getProperty("path.locator");
	public static final String TEST_DATA_FOLDER = getProperty("path.testdata.folder");
	public static final String MAPS_FOLDER = getProperty("path.maps.folder");

	public static final String REMOTE_ADDRESS = getProperty("remote.address");

	public static final String BROWSERSTACK_URL = getProperty("browserstackurl");
	public static final String PROJECT = getProperty("project");
	public static final String BUILD = getProperty("build");
	public static final String BROWSERSTACK_LOCAL = getProperty("browserstacklocal");
	public static final String BROWSERSTACK_LOCAL_IDENTIFIER = getProperty("browserstacklocalIdentifier");
	public static final String BROWSERSTACK_DEBUG = getProperty("browserstackdebug");

	// from test conf
	public static final String BASE_URL = getProperty("server.url");

	public static final String CURRENT_LANG = getProperty("lang.current");

	public static final String FIREFOX_PATH = getProperty("path.firefox");
	public static final String CHROME_WEBDRIVER = getProperty("path.webdriver.chrome");
	public static final String IE_WEBDRIVER_X86 = getProperty("path.webdriver.ie.x86");
	public static final String IE_WEBDRIVER_X64 = getProperty("path.webdriver.ie.x64");
	public static final String SYSTEM_DATA = getProperty("path.systemdata");

	public static final String DEFAULT_OS = getProperty("os.default");
	public static final String DEFAULT_OS_VERSION = getProperty("os.version.default");
	public static final String DEFAULT_BROSWER = getProperty("broswer.default");
	public static final String DEFAULT_IE_ARCH = getProperty("broswer.ie.arch.default");
	public static final Boolean MAXIMIZE_BROSWER = Boolean.parseBoolean(getProperty("browser.maximize"));

	public static final Boolean REMOTE = Boolean.parseBoolean(getProperty("remote"));
	public static final Boolean BROWSERSTACK = Boolean.parseBoolean(getProperty("browserstack"));
	public static final Boolean ENABLE_HAR = Boolean.parseBoolean(getProperty("HAR.enable"));

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

	/**
	 * get property from system first, if null then get from properties file
	 * 
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		return System.getProperty(key) == null ? props.getProperty(key) : System.getProperty(key);
	}
}
