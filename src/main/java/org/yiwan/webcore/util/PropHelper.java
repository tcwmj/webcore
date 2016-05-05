package org.yiwan.webcore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Kenny Wang
 */
public class PropHelper {

    private final static Logger logger = LoggerFactory.getLogger(PropHelper.class);
    private final static Properties props = new Properties();

    static {
        load(System.getProperty("base.prop", "base.properties"));
        load(System.getProperty("biz.prop", "biz.properties"));
        load(System.getProperty("test.prop", "test.properties"));
    }

    // from base conf
    public static final int TIMEOUT_INTERVAL = Integer.parseInt(getProperty("timeout.interval"));
    public static final int TIMEOUT_NAVIGATION_INTERVAL = Integer.parseInt(getProperty("timeout.navigation.interval"));
    public static final int TIMEOUT_POLLING_INTERVAL = Integer.parseInt(getProperty("timeout.polling.interval"));
    public static final Integer TEST_RETRY_COUNT = Integer.valueOf(getProperty("test.retry.count"));
    public static final String RANDOM_RULE = getProperty("random.rule");
    public static final String SOURCE_CODE_PATH = getProperty("sourcecode.path");
    public static final String SOURCE_CODE_ENCODING = getProperty("sourcecode.encoding");
    public static final String REPLACEMENT_SYMBOL = getProperty("symbol.replacement");
    public static final String LOCATOR_SCHEMA = getProperty("path.locator.schema");
    public final static String DISCRIMINATOR_KEY = getProperty("discriminator.key");
    public static final String RESULT_FOLDER = getProperty("path.result.folder");
    public static final String SCREENSHOT_FOLDER = getProperty("path.screenshot.folder");
    public static final String LOG_FOLDER = getProperty("path.log.folder");
    public static final String HAR_FOLDER = getProperty("path.har.folder");
    public static final boolean DUMMY_TEST = Boolean.parseBoolean(getProperty("dummy.test"));
    // from biz conf
    public static final int TIMEOUT_DIALOG_APPEAR = Integer.parseInt(getProperty("timeout.dialog.appear"));
    public static final int TIMEOUT_DIALOG_DISAPPEAR = Integer.parseInt(getProperty("timeout.dialog.disappear"));
    public static final String DEFAULT_LANG = getProperty("lang.default");
    public static final String DATA_SCHEMA_FILE = getProperty("path.data.schema");
    public static final String LOCATORS_FILE = getProperty("path.locator");
    public static final String FEATURE_DATA_FOLDER = getProperty("path.source.feature.data.folder");
    public static final String SOURCE_SCENARIO_DATA_FOLDER = getProperty("path.source.scenario.data.folder");
    public static final String TARGET_SCENARIO_DATA_FOLDER = getProperty("path.target.scenario.data.folder");
    public static final String DOWNLOAD_FOLDER = getProperty("path.download.folder");
    public static final String MAPS_FOLDER = getProperty("path.maps.folder");
    public static final String REMOTE_ADDRESS = getProperty("remote.address");
    public static final String BROWSERSTACK_URL = getProperty("browserstackurl");
    public static final String PROJECT = getProperty("project");
    public static final String BUILD = getProperty("build");
    public static final String BROWSERSTACK_LOCAL = getProperty("browserstacklocal");
    public static final String BROWSERSTACK_LOCAL_IDENTIFIER = getProperty("browserstacklocalIdentifier");
    public static final String BROWSERSTACK_DEBUG = getProperty("browserstackdebug");
    // from test conf
    public static final String SERVER_INFO = getProperty("server.info");
    public static final String CURRENT_LANG = getProperty("lang.current");
    public static final String PHANTOMJS_PATH = getProperty("path.phantomjs");
    public static final String FIREFOX_PATH = getProperty("path.firefox");
    public static final String CHROME_WEBDRIVER = getProperty("path.webdriver.chrome");
    public static final String IE_WEBDRIVER_X86 = getProperty("path.webdriver.ie.x86");
    public static final String IE_WEBDRIVER_X64 = getProperty("path.webdriver.ie.x64");
    public static final String DEFAULT_BROWSER = getProperty("browser.default");
    public static final boolean IGNORE_PROTECTED_MODE_SETTINGS = Boolean.parseBoolean(getProperty("browser.ie.ignoreProtectedModeSettings"));
    public static final String INITIAL_BROWSER_URL = getProperty("browser.ie.initialBrowserUrl");
    public static final boolean IGNORE_ZOOM_SETTING = Boolean.parseBoolean(getProperty("browser.ie.ignoreZoomSetting"));
    public static final boolean REQUIRE_WINDOW_FOCUS = Boolean.parseBoolean(getProperty("browser.ie.requireWindowFocus"));
    public static final boolean ENABLE_PERSISTENT_HOVER = Boolean.parseBoolean(getProperty("browser.ie.enablePersistentHover"));
    public static final boolean ACCEPT_SSL_CERTS = Boolean.parseBoolean(getProperty("browser.acceptSSLCerts"));
    public static final boolean NATIVE_EVENTS = Boolean.parseBoolean(getProperty("browser.nativeEvents"));
    public static final String UNEXPECTED_ALERT_BEHAVIOUR = getProperty("browser.unexpectedAlertBehaviour");
    public static final String DEFAULT_IE_ARCH = getProperty("browser.ie.arch.default");
    public static final boolean MAXIMIZE_BROWSER = Boolean.parseBoolean(getProperty("browser.maximize"));
    public static final boolean REMOTE = Boolean.parseBoolean(getProperty("remote"));
    public static final boolean BROWSERSTACK = Boolean.parseBoolean(getProperty("browserstack"));
    public static final boolean ENABLE_PROXY = Boolean.parseBoolean(getProperty("proxy.enable"));
    public static final boolean ENABLE_DOWNLOAD = Boolean.parseBoolean(getProperty("download.enable"));
    public static final boolean ENABLE_HAR = Boolean.parseBoolean(getProperty("har.enable"));
    public static final boolean ENABLE_RECORD_TRANSACTION_TIMESTAMP = Boolean.parseBoolean(getProperty("record.transaction.timestamp.enable"));
    public static final boolean ENABLE_CAPTURE_TRANSACTION_SCREENSHOT = Boolean.parseBoolean(getProperty("capture.transaction.screeshot.enable"));

    /**
     * load properties from external file
     *
     * @param file
     */
    private static void load(String file) {
//        logger.debug("load property file in resource " + file);
        InputStream is = ClassLoader.getSystemResourceAsStream(file);
        try {
            props.load(is);
        } catch (Exception e) {
            logger.error("file {} was not found", file);
        }
    }

    /**
     * get property from system first, if null then get from properties file
     *
     * @param key
     * @return property value
     */
    public static String getProperty(String key) {
        // return System.getProperty(key) == null ? props.getProperty(key) :
        // System.getProperty(key);
        return System.getProperty(key, props.getProperty(key));
    }

}
