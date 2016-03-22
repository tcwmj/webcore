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
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.util.ProxyWrapper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * @author Kenny Wang
 */
public class WebDriverFactory {
    private final static Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);
    private final static Proxy SELENIUM_PROXY = ClientUtil.createSeleniumProxy(ProxyWrapper.getProxy());

    private final String os = System.getProperty("os");
    private final String os_version = System.getProperty("os.version");
    private final String browser = System.getProperty("browser", PropHelper.DEFAULT_BROWSER);
    private final String browser_version = System.getProperty("browser.version");
    private final String resolution = System.getProperty("resolution");

    public WebDriver createWebDriver() {
        WebDriver driver = null;
        if (PropHelper.REMOTE) {
            logger.debug("choose remote test mode");
            driver = setupRemoteBrowser();
        } else {
            logger.debug("choose local test mode");
            driver = setupLocalBrowser();
        }
        if (PropHelper.MAXIMIZE_BROWSER)
            driver.manage().window().maximize();
        // use explicit wait to replace implicitly wait
        // driver.manage().timeouts().implicitlyWait(PropHelper.TIMEOUT_INTERVAL,TimeUnit.SECONDS);
        return driver;
    }

    private WebDriver setupRemoteBrowser() {
        DesiredCapabilities capability = new DesiredCapabilities();
        URL url = null;
        if (PropHelper.BROWSERSTACK) {
            logger.debug("choose browserstack cloud test platform");
            configBrowserStackCapablities(capability, url);
        } else {
            logger.debug("choose self remote test platform");
            setupSelfRemoteCapabilities(capability, url);
        }
        RemoteWebDriver rwd = new RemoteWebDriver(url, capability);
        rwd.setFileDetector(new LocalFileDetector());
        return rwd;
    }

    private void setupSelfRemoteCapabilities(DesiredCapabilities capability, URL url) {
        if (os != null) {
            capability.setPlatform(Platform.fromString(os));
            logger.debug("choose platform " + os);
        } else {
            capability.setPlatform(Platform.ANY);
        }
        configBrowserCapbilities(capability);
        logger.debug("choose browser " + browser);
        // set browser version
        if (browser_version != null) {
            capability.setVersion(browser_version);
            logger.debug("choose browser version " + browser_version);
        }
        try {
            url = new URL(PropHelper.REMOTE_ADDRESS);
        } catch (MalformedURLException e) {
            logger.error("url " + PropHelper.REMOTE_ADDRESS + " is malformed");
        }
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
            default:
                return setupLocalFirefoxDriver();
        }
    }

    /**
     * setup local chrome browser
     *
     * @return WebDriver
     */
    private WebDriver setupLocalChromeDriver() {
        System.setProperty("webdriver.chrome.driver", PropHelper.CHROME_WEBDRIVER);
        DesiredCapabilities capability = DesiredCapabilities.chrome();
        configBrowserCapbilities(capability);
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
        configBrowserCapbilities(capability);
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
        configBrowserCapbilities(capability);
        return new FirefoxDriver(firefoxBinary, profile, capability);
    }

    /**
     * whether the local environment is arc 64 or not
     *
     * @return boolean
     */
    private Boolean isOSX64() {
        Properties props = System.getProperties();
        String arch = props.getProperty("os.arch");
        return arch.contains("64");
    }

    private void configBrowserStackCapablities(DesiredCapabilities capability, URL url) {
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
        configBrowserCapbilities(capability);
        capability.setCapability("project", PropHelper.PROJECT);
        capability.setCapability("build", PropHelper.BUILD);
        capability.setCapability("browserstack.local", PropHelper.BROWSERSTACK_LOCAL);
        capability.setCapability("browserstack.localIdentifier", PropHelper.BROWSERSTACK_LOCAL_IDENTIFIER);
        capability.setCapability("browserstack.debug", PropHelper.BROWSERSTACK_DEBUG);
        try {
            url = new URL(PropHelper.BROWSERSTACK_URL);
        } catch (MalformedURLException e) {
            logger.error("url " + PropHelper.BROWSERSTACK_URL + " is malformed");
        }
    }

    private void configBrowserCapbilities(DesiredCapabilities capability) {
        switch (browser.toLowerCase()) {
            case "ie":
                configInternetExplorerCapbilities(capability);
                break;
            case "chrome":
                configChromeCapbilities(capability);
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