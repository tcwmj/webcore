package org.yiwan.webcore.web;

import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.proxy.ProxyWrapper;
import org.yiwan.webcore.test.pojo.TestCapability;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.chrome.ChromeDriverWrapper;
import org.yiwan.webcore.web.dummy.DummyDriverWrapper;
import org.yiwan.webcore.web.firefox.FirefoxDriverWrapper;
import org.yiwan.webcore.web.htmlunit.HtmlUnitDriverWrapper;
import org.yiwan.webcore.web.ie.InternetExplorerDriverWrapper;
import org.yiwan.webcore.web.phantomjs.PhantomJSDriverWrapper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * @author Kenny Wang
 */
public class WebDriverWrapperFactory {
    private final static Logger logger = LoggerFactory.getLogger(WebDriverWrapperFactory.class);
    private final static Proxy SELENIUM_PROXY = ClientUtil.createSeleniumProxy(ProxyWrapper.getProxy());

    private final String os;
    private final String os_version;
    private final String browser;
    private final String browser_version;
    private final String resolution;

    public WebDriverWrapperFactory(TestCapability testCapability) {
        this.os = testCapability.getOs() == null ? System.getProperty("os") : testCapability.getOs();
        this.os_version = testCapability.getOsVersion() == null ? System.getProperty("os.version") : testCapability.getOsVersion();
        this.browser = testCapability.getBrowser() == null ? System.getProperty("browser", PropHelper.DEFAULT_BROWSER) : testCapability.getBrowser();
        this.browser_version = testCapability.getBrowserVersion() == null ? System.getProperty("browser.version") : testCapability.getBrowserVersion();
        this.resolution = testCapability.getResolution() == null ? System.getProperty("resolution") : testCapability.getResolution();
    }

    public IWebDriverWrapper create() throws MalformedURLException {
        if (PropHelper.DUMMY_TEST) {
            return new DummyDriverWrapper();
        } else {
            WebDriver webDriver = null;
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
        URL url = null;
        if (PropHelper.BROWSERSTACK) {
            logger.debug("choose browserstack cloud test platform");
            configBrowserStackCapablities(capability, url);
        } else {
            logger.debug("choose self remote test platform");
            configSelfRemoteCapabilities(capability, url);
        }
        RemoteWebDriver rwd = new RemoteWebDriver(url, capability);
        rwd.setFileDetector(new LocalFileDetector());
        return rwd;
    }

    private void configSelfRemoteCapabilities(DesiredCapabilities capability, URL url) throws MalformedURLException {
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
        url = new URL(PropHelper.REMOTE_ADDRESS);
    }

    /**
     * setup local browser
     *
     * @return WebDriver
     */
    private WebDriver setupLocalBrowser() {
        logger.debug("choose test browser " + browser);
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
        System.setProperty("phantomjs.binary.path", PropHelper.PHANTOMJS_PATH);
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
        System.setProperty("webdriver.chrome.driver", PropHelper.CHROME_WEBDRIVER);
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
            System.setProperty("webdriver.ie.driver", PropHelper.IE_WEBDRIVER_X64);
        } else {
            System.setProperty("webdriver.ie.driver", PropHelper.IE_WEBDRIVER_X86);
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

    private void configBrowserStackCapablities(DesiredCapabilities capability, URL url) throws MalformedURLException {
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
        url = new URL(PropHelper.BROWSERSTACK_URL);
    }

    private void configBrowserCapabilities(DesiredCapabilities capability) {
        switch (browser.toLowerCase()) {
            case "ie":
                configInternetExplorerCapbilities(capability);
                break;
            case "chrome":
                configChromeCapbilities(capability);
                break;
            case "htmlunit":
                break;
            case "htmlunitjs":
                break;
            case "phantomjs":
                break;
            default:
                configFirefoxCapbilities(capability);
        }
        capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, PropHelper.ACCEPT_SSL_CERTS);
        capability.setCapability(CapabilityType.HAS_NATIVE_EVENTS, PropHelper.NATIVE_EVENTS);
        if (PropHelper.UNEXPECTED_ALERT_BEHAVIOUR != null)
            capability.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
                    UnexpectedAlertBehaviour.fromString(PropHelper.UNEXPECTED_ALERT_BEHAVIOUR));
        if (PropHelper.ENABLE_PROXY)
            capability.setCapability(CapabilityType.PROXY, SELENIUM_PROXY);
    }

    private void configInternetExplorerCapbilities(DesiredCapabilities capability) {
        capability.setBrowserName(BrowserType.IE);
        capability.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
                PropHelper.IGNORE_PROTECTED_MODE_SETTINGS);
        if (PropHelper.INITIAL_BROWSER_URL != null)
            capability.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, PropHelper.INITIAL_BROWSER_URL);
        capability.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, PropHelper.IGNORE_ZOOM_SETTING);
        capability.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, PropHelper.REQUIRE_WINDOW_FOCUS);
        capability.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, PropHelper.ENABLE_PERSISTENT_HOVER);
        // capability.setCapability("disable-popup-blocking", true);
    }

    private void configFirefoxCapbilities(DesiredCapabilities capability) {
        capability.setBrowserName(BrowserType.FIREFOX);
    }

    private void configChromeCapbilities(DesiredCapabilities capability) {
        capability.setBrowserName(BrowserType.CHROME);
    }
}