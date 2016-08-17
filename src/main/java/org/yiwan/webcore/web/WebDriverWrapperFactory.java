package org.yiwan.webcore.web;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.internal.ApacheHttpClient;
import org.openqa.selenium.remote.internal.HttpClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.pojo.TestCapability;
import org.yiwan.webcore.util.PropHelper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * @author Kenny Wang
 */
public class WebDriverWrapperFactory {
    private final static Logger logger = LoggerFactory.getLogger(WebDriverWrapperFactory.class);

    private final String os;
    private final String os_version;
    private final String browser;
    private final String browser_version;
    private final String resolution;
    private final Proxy seleniumProxy;

    public WebDriverWrapperFactory(TestCapability testCapability) {
        this(testCapability, null);
    }

    public WebDriverWrapperFactory(TestCapability testCapability, Proxy seleniumProxy) {
        this.os = testCapability.getOs() == null ? System.getProperty("os") : testCapability.getOs();
        this.os_version = testCapability.getOsVersion() == null ? System.getProperty("os.version") : testCapability.getOsVersion();
        this.browser = testCapability.getBrowser() == null ? System.getProperty("browser", PropHelper.DEFAULT_BROWSER) : testCapability.getBrowser();
        this.browser_version = testCapability.getBrowserVersion() == null ? System.getProperty("browser.version") : testCapability.getBrowserVersion();
        this.resolution = testCapability.getResolution() == null ? System.getProperty("resolution") : testCapability.getResolution();
        this.seleniumProxy = seleniumProxy;
    }

    public IWebDriverWrapper create() throws MalformedURLException {
        if (PropHelper.DUMMY_TEST) {
            return new DummyDriverWrapper();
        } else {
            WebDriver webDriver;
            if (PropHelper.REMOTE) {
                logger.debug("choose remote test mode");
                webDriver = setupRemoteBrowser();
            } else {
                logger.debug("choose local test mode");
                webDriver = setupLocalBrowser();
            }
            if (PropHelper.MAXIMIZE_BROWSER) {
                webDriver.manage().window().maximize();
            }
//        use explicit wait to replace implicitly wait
//        driver.manage().timeouts().implicitlyWait(PropHelper.TIMEOUT_INTERVAL, TimeUnit.SECONDS);
            return wrapWebDriver(webDriver);
        }
    }

    private IWebDriverWrapper wrapWebDriver(WebDriver webDriver) {
        switch (browser.toLowerCase()) {
            case "chrome":
                return new ChromeDriverWrapper(webDriver);
            case "ie":
                return new InternetExplorerDriverWrapper(webDriver);
            case "htmlunit":
                return new HtmlUnitDriverWrapper(webDriver);
            case "htmlunitjs":
                return new HtmlUnitDriverWrapper(webDriver);
            case "phantomjs":
                return new PhantomJSDriverWrapper(webDriver);
            default:
                return new FirefoxDriverWrapper(webDriver);
        }
    }

    private WebDriver setupRemoteBrowser() throws MalformedURLException {
        DesiredCapabilities capability = new DesiredCapabilities();
        URL url;
        if (PropHelper.BROWSERSTACK) {
            logger.debug("choose browserstack cloud test platform");
            configBrowserStackCapabilities(capability);
            url = new URL(PropHelper.BROWSERSTACK_URL);
        } else {
            logger.debug("choose self remote test platform");
            configSelfRemoteCapabilities(capability);
            url = new URL(PropHelper.REMOTE_ADDRESS);
        }
        HttpClient.Factory factory = new ApacheHttpClient.Factory(new HttpClientFactory(PropHelper.REMOTE_CONNECTION_TIMEOUT, PropHelper.REMOTE_SOCKET_TIMEOUT));
        HttpCommandExecutor executor = new HttpCommandExecutor(Collections.<String, CommandInfo>emptyMap(), url, factory);
        RemoteWebDriver rwd = new RemoteWebDriver(executor, capability);

        rwd.setFileDetector(new LocalFileDetector());
        return rwd;
    }

    private void configSelfRemoteCapabilities(DesiredCapabilities capability) {
        if (os != null) {
            capability.setPlatform(Platform.fromString(os));
            logger.debug("choose platform " + os);
        } else {
            capability.setPlatform(Platform.ANY);
        }
        configBrowserCapabilities(capability);
        logger.debug("choose browser " + browser);
        // set browser version
        if (browser_version != null) {
            capability.setVersion(browser_version);
            logger.debug("choose browser version " + browser_version);
        }
    }

    /**
     * setup local browser
     *
     * @return WebDriver
     */
    private WebDriver setupLocalBrowser() {
        logger.debug("choose browser " + browser);
        switch (browser.toLowerCase()) {
            case "chrome":
                return setupLocalChromeDriver();
            case "ie":
                return setupLocalInternetExplorerDriver();
            case "htmlunit":
                return setupLocalHtmlUnitDriver();
            case "htmlunitjs":
                return setupLocalHtmlUnitJSDriver();
            case "phantomjs":
                return setupLocalPhantomJSDriver();
            default:
                return setupLocalFirefoxDriver();
        }
    }

    private WebDriver setupLocalHtmlUnitDriver() {
        DesiredCapabilities capability = DesiredCapabilities.htmlUnit();
        configBrowserCapabilities(capability);
        return new HtmlUnitDriver(capability);
    }

    private WebDriver setupLocalHtmlUnitJSDriver() {
        DesiredCapabilities capability = DesiredCapabilities.htmlUnitWithJs();
        configBrowserCapabilities(capability);
        return new HtmlUnitDriver(capability);
    }

    private WebDriver setupLocalPhantomJSDriver() {
        System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PropHelper.PHANTOMJS_PATH);
        DesiredCapabilities capability = DesiredCapabilities.phantomjs();
        configBrowserCapabilities(capability);
        return new PhantomJSDriver(capability);
    }

    /**
     * setup local chrome browser
     *
     * @return WebDriver
     */
    private WebDriver setupLocalChromeDriver() {
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, PropHelper.CHROME_WEBDRIVER);
        DesiredCapabilities capability = DesiredCapabilities.chrome();
        configBrowserCapabilities(capability);
        return new ChromeDriver(capability);
    }

    /**
     * setup local Internet explorer driver
     *
     * @return WebDriver
     */
    private WebDriver setupLocalInternetExplorerDriver() {
        if (PropHelper.DEFAULT_IE_ARCH.equals("x64") && isOSX64()) {
            System.setProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY, PropHelper.IE_WEBDRIVER_X64);
        } else {
            System.setProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY, PropHelper.IE_WEBDRIVER_X86);
        }
        DesiredCapabilities capability = DesiredCapabilities.internetExplorer();
        configBrowserCapabilities(capability);
        return new InternetExplorerDriver(capability);
    }

    /**
     * setup local firefox driver
     *
     * @return WebDriver
     */
    private WebDriver setupLocalFirefoxDriver() {
//        if (PropHelper.FIREFOX_PATH != null && !PropHelper.FIREFOX_PATH.trim().isEmpty())
//            System.setProperty("webdriver.firefox.bin", PropHelper.FIREFOX_PATH);

        FirefoxBinary firefoxBinary;
        if (PropHelper.FIREFOX_PATH != null && !PropHelper.FIREFOX_PATH.trim().isEmpty()) {
            firefoxBinary = new FirefoxBinary(new File(PropHelper.FIREFOX_PATH));
        } else {
            firefoxBinary = new FirefoxBinary();
        }

        FirefoxProfile profile = new FirefoxProfile();
//        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
//        profile.setPreference("browser.download.dir", "D:\\mydownloads\\");
        profile.setAcceptUntrustedCertificates(true);
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
//        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream");
//        profile.setPreference("browser.helperApps.neverAsk.openFile", "text/csv,application/pdf,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,application/x-excel,application/x-msexcel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
//        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv,application/pdf,application/x-msexcel,application/excel,application/x-excel,application/excel,application/x-excel,application/excel,application/vnd.ms-excel,application/x-excel,application/x-msexcel,image/png,image/pjpeg,image/jpeg,text/html,text/plain,application/msword,application/xml,application/excel");
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/plain,text/xml,text/csv,image/jpeg,application/zip,application/vnd.ms-excel,application/pdf,application/xml");

        //Then add the proxy setting to the Firefox profile we created
//        profile.setPreference("network.proxy.http", "localhost");
//        profile.setPreference("network.proxy.http_port", "8888");

        DesiredCapabilities capability = DesiredCapabilities.firefox();
        configBrowserCapabilities(capability);
        return new FirefoxDriver(firefoxBinary, profile, capability);
    }

    /**
     * whether the local environment is arc 64 or not
     *
     * @return boolean
     */
    private boolean isOSX64() {
        Properties props = System.getProperties();
        String arch = props.getProperty("os.arch");
        return arch.contains("64");
    }

    private void configBrowserStackCapabilities(DesiredCapabilities capability) {
        if (os != null) {
            capability.setCapability("os", os);
            logger.debug("choose platform " + os);
            if (os_version != null) {
                capability.setCapability("os_version", os_version);
                logger.debug("choose platform version " + os_version);
            }
        }
        capability.setCapability("browser", browser);
        logger.debug("choose browser " + browser);
        if (browser_version != null) {
            capability.setCapability("browser_version", browser_version);
            logger.debug("choose browser version " + browser_version);
        }
        if (resolution != null) {
            capability.setCapability("resolution", resolution);
            logger.debug("choose platform resolution " + resolution);
        }
        configBrowserCapabilities(capability);
        capability.setCapability("project", PropHelper.PROJECT);
        capability.setCapability("build", PropHelper.BUILD);
        capability.setCapability("browserstack.local", PropHelper.BROWSERSTACK_LOCAL);
        capability.setCapability("browserstack.localIdentifier", PropHelper.BROWSERSTACK_LOCAL_IDENTIFIER);
        capability.setCapability("browserstack.debug", PropHelper.BROWSERSTACK_DEBUG);
    }

    private void configBrowserCapabilities(DesiredCapabilities capability) {
        switch (browser.toLowerCase()) {
            case "ie":
                configInternetExplorerCapabilities(capability);
                break;
            case "chrome":
                configChromeCapabilities(capability);
                break;
            case "htmlunit":
                break;
            case "htmlunitjs":
                break;
            case "phantomjs":
                configPhantomJSCapabilities(capability);
                break;
            default:
                configFirefoxCapabilities(capability);
        }
        capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, PropHelper.ACCEPT_SSL_CERTS);
        capability.setCapability(CapabilityType.HAS_NATIVE_EVENTS, PropHelper.NATIVE_EVENTS);
        if (PropHelper.UNEXPECTED_ALERT_BEHAVIOUR != null) {
            capability.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.fromString(PropHelper.UNEXPECTED_ALERT_BEHAVIOUR));
        }
        if (seleniumProxy != null) {
            capability.setCapability(CapabilityType.PROXY, seleniumProxy);
        }
    }

    private void configPhantomJSCapabilities(DesiredCapabilities capability) {
        capability.setCapability("takesScreenshot", true);
        String[] phantomjsCliArgs = StringUtils.split(PropHelper.PHANTOMJS_CLI_ARGS.trim());
        if (PropHelper.PHANTOMJS_CLI_ARGS != null && phantomjsCliArgs.length > 0) {
            capability.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, Arrays.asList(phantomjsCliArgs));
        }
        String[] phantomjsGhostdriverCliArgs = StringUtils.split(PropHelper.PHANTOMJS_GHOSTDRIVER_CLI_ARGS.trim());
        if (PropHelper.PHANTOMJS_GHOSTDRIVER_CLI_ARGS != null && phantomjsGhostdriverCliArgs.length > 0) {
            capability.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS, Arrays.asList(phantomjsGhostdriverCliArgs));
        }
    }

    private void configInternetExplorerCapabilities(DesiredCapabilities capability) {
        capability.setBrowserName(BrowserType.IE);
        capability.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, PropHelper.IGNORE_PROTECTED_MODE_SETTINGS);
        if (PropHelper.INITIAL_BROWSER_URL != null) {
            capability.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, PropHelper.INITIAL_BROWSER_URL);
        }
        capability.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, PropHelper.IGNORE_ZOOM_SETTING);
        capability.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, PropHelper.REQUIRE_WINDOW_FOCUS);
        capability.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, PropHelper.ENABLE_PERSISTENT_HOVER);
        // capability.setCapability("disable-popup-blocking", true);
    }

    private void configFirefoxCapabilities(DesiredCapabilities capability) {
        capability.setBrowserName(BrowserType.FIREFOX);
    }

    private void configChromeCapabilities(DesiredCapabilities capability) {
        capability.setBrowserName(BrowserType.CHROME);
    }

}