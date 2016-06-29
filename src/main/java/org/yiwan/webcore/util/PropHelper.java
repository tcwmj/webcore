package org.yiwan.webcore.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.pojo.ApplicationServer;
import org.yiwan.webcore.test.pojo.DatabaseServer;
import org.yiwan.webcore.test.pojo.TestEnvironment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import static java.lang.String.format;

/**
 * @author Kenny Wang
 */
public class PropHelper {
    private final static Logger logger = LoggerFactory.getLogger(PropHelper.class);
    private final static Properties props = new Properties();
    private static boolean hasLoaded = false;

    // from base conf
    public static final long TIMEOUT_INTERVAL = Long.parseLong(getProperty("timeout.interval"));
    public static final long TIMEOUT_NAVIGATION_INTERVAL = Long.parseLong(getProperty("timeout.navigation.interval"));
    public static final long TIMEOUT_POLLING_INTERVAL = Long.parseLong(getProperty("timeout.polling.interval"));
    public static final Integer TEST_RETRY_COUNT = Integer.valueOf(getProperty("test.retry.count"));
    public static final String RANDOM_POLICY = getProperty("random.policy");
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
    public static final long TIMEOUT_DIALOG_APPEAR = Long.parseLong(getProperty("timeout.dialog.appear"));
    public static final long TIMEOUT_DIALOG_DISAPPEAR = Long.parseLong(getProperty("timeout.dialog.disappear"));
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
    @Deprecated
    public static final String SERVER_URL = getProperty("server.url");
    public static final String SERVER_INFO = getServerInfo("server.info");
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
    public static final boolean ENABLE_FILE_DOWNLOAD = Boolean.parseBoolean(getProperty("download.enable"));
    public static final boolean HTTP_STATUS_TO_204 = Boolean.parseBoolean(getProperty("http.status.to.204"));
    public static final boolean ENABLE_HTTP_ARCHIVE = Boolean.parseBoolean(getProperty("har.enable"));
    public static final boolean ENABLE_TRANSACTION_TIMESTAMP_RECORD = Boolean.parseBoolean(getProperty("record.transaction.timestamp.enable"));
    public static final boolean ENABLE_TRANSACTION_SCREENSHOT_CAPTURE = Boolean.parseBoolean(getProperty("capture.transaction.screeshot.enable"));
    public static final String PHANTOMJS_CLI_ARGS = getProperty("browser.phantomjs.cli.args");
    public static final String PHANTOMJS_GHOSTDRIVER_CLI_ARGS = getProperty("browser.phantomjs.ghostdriver.cli.args");
    public static final String TRANSACTION_TIMESTAMPS_FILE = getProperty("trasaction.timestamps.file");
    public static final boolean ENABLE_PENETRATION_TEST = Boolean.parseBoolean(getProperty("penetration.test.enable"));
    public static final String ZAP_SERVER_HOST = getProperty("zap.server.host");
    public static final int ZAP_SERVER_PORT = Integer.parseInt(getProperty("zap.server.port"));
    public static final String ZAP_API_KEY = getProperty("zap.api.key");
    public static final String PENETRATION_TEST_HTML_REPORT_FILE = getProperty("penetration.test.html.report.file");

    public static final boolean ENABLE_XSD_VALIDATION = Boolean.parseBoolean(getProperty("xsd.validation.enable"));
    public static final int MAXIMUM_RESPONSE_BUFFER_SIZE = Integer.parseInt(getProperty("maximum.response.buffer.size"));

    /**
     * load properties from external file
     *
     * @param file
     */
    private static void load(String file) {
        try (InputStream is = ClassLoader.getSystemResourceAsStream(file)) {
            if (is != null) {
                props.load(is);
                is.close();
            } else {
                logger.warn(format("%s was not provided", file));
            }
        } catch (IOException e) {
            logger.error(file, e);
        }
    }

    private static boolean load() {
        load(System.getProperty("base.prop", "base.properties"));
        load(System.getProperty("biz.prop", "biz.properties"));
        load(System.getProperty("test.prop", "test.properties"));
        return true;
    }

    /**
     * get property from system first, if null then get from properties file
     *
     * @param key
     * @return property value
     */
    public static String getProperty(String key) {
        if (!hasLoaded) {
            hasLoaded = load();
        }
//        return System.getProperty(key) == null ? props.getProperty(key) : System.getProperty(key);
        return System.getProperty(key, props.getProperty(key));
    }

    public static String getServerInfo(String key) {
        if (SERVER_URL != null) {//for compatible with server.url
            TestEnvironment testEnvironment = new TestEnvironment();
            ApplicationServer applicationServer = new ApplicationServer();
            applicationServer.setUrl(SERVER_URL);
            DatabaseServer databaseServer = new DatabaseServer();
            databaseServer.setDump("data/system/default.xml");
            testEnvironment.setApplicationServers(Arrays.asList(applicationServer));
            testEnvironment.setDatabaseServers(Arrays.asList(databaseServer));
            return String.format("[%s]", testEnvironment.toString());
        } else {
            String url = getProperty(key);
            String serverInfo = "";
            try (InputStream is = ClassLoader.getSystemResourceAsStream(url)) {
                if (null != is) {
                    serverInfo = IOUtils.toString(is, "UTF-8");
                    is.close();
                } else {
                    serverInfo = FileUtils.readFileToString(new File(url), "UTF-8");
                }
            } catch (IOException e) {
                logger.error(url, e);
            }
            return serverInfo;
        }
    }

}
