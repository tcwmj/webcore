package org.yiwan.webcore.web;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.util.ProxyHelper;
import org.yiwan.webcore.util.TestBase;

import net.lightbody.bmp.client.ClientUtil;

/**
 * @author Kenny Wang
 * 
 */
public class WebDriverFactory {
	private final static Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);
	private final static Proxy SELENIUM_PROXY = ClientUtil.createSeleniumProxy(ProxyHelper.getProxy());

	public WebDriverFactory(TestBase testcase) {
		super();
	}

	public WebDriver createWebDriver(String os, String os_version, String browser, String browser_version,
			String resolution) {
		os = (null == os) ? PropHelper.DEFAULT_OS : os;
		os_version = (null == os_version) ? PropHelper.DEFAULT_OS_VERSION : os_version;
		browser = (null == browser) ? PropHelper.DEFAULT_BROSWER : browser;

		if (PropHelper.REMOTE) {
			logger.info("choose remote test mode");
			return setupRemoteBrowser(os, os_version, browser, browser_version, resolution);
		} else {
			logger.info("choose local test mode");
			return setupLocalBrowser(browser);
		}
	}

	private WebDriver setupRemoteBrowser(String os, String os_version, String browser, String browser_version,
			String resolution) {
		DesiredCapabilities capability = new DesiredCapabilities();
		URL url = null;
		// setup browserstack remote testing
		if (PropHelper.BROWSERSTACK) {
			logger.info("choose browserstack cloud test platform for remote strategy");
			if (os != null) {
				capability.setCapability("os", os);
				logger.info("choose test platform " + os);
			}
			if (os_version != null) {
				capability.setCapability("os_version", os_version);
				logger.info("choose test platform version " + os_version);
			}
			capability.setCapability("browser", browser);
			logger.info("choose test browser " + browser);
			if (browser_version != null) {
				capability.setCapability("browser_version", browser_version);
				logger.info("choose test browser version " + browser_version);
			}
			if (resolution != null) {
				capability.setCapability("resolution", resolution);
				logger.info("choose test platform resolution " + resolution);
			}
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
		} else {
			// setup local remote testing
			logger.info("choose customized cloud test platform for remote strategy");
			if (os != null) {
				switch (os.toLowerCase()) {
				case "mac":
					capability.setPlatform(Platform.MAC);
					break;
				case "vista":
					capability.setPlatform(Platform.VISTA);
					break;
				case "win8":
					capability.setPlatform(Platform.WIN8);
					break;
				case "win8.1":
					capability.setPlatform(Platform.WIN8_1);
					break;
				case "xp":
					capability.setPlatform(Platform.XP);
					break;
				case "windows":
					capability.setPlatform(Platform.WINDOWS);
					break;
				case "unix":
					capability.setPlatform(Platform.UNIX);
					break;
				case "linux":
					capability.setPlatform(Platform.LINUX);
					break;
				default:
					capability.setPlatform(Platform.WINDOWS);
				}
				logger.info("choose test platform " + os);
			}
			// set browser type
			if (browser.equalsIgnoreCase("ie")) {
				capability.setBrowserName(BrowserType.IE);
				capability.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				capability.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
			} else {
				capability.setBrowserName(browser);
			}
			logger.info("choose test browser " + browser);
			// set browser version
			if (browser_version != null) {
				capability.setVersion(browser_version);
				logger.info("choose test browser version " + browser_version);
			}
			try {
				url = new URL(PropHelper.REMOTE_ADDRESS);
			} catch (MalformedURLException e) {
				logger.error("url " + PropHelper.REMOTE_ADDRESS + " is malformed");
			}
		}

		capability.setCapability(CapabilityType.PROXY, SELENIUM_PROXY);
		RemoteWebDriver rwd = new RemoteWebDriver(url, capability);
		rwd.setFileDetector(new LocalFileDetector());
		return rwd;
	}

	/**
	 * setup local browser
	 * 
	 * @return WebDriver
	 */
	private WebDriver setupLocalBrowser(String browser) {
		logger.info("choose test browser " + browser);
		switch (browser.toLowerCase()) {
		case "chrome":
			return setupLocalChomeDriver();
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
	private WebDriver setupLocalChomeDriver() {
		System.setProperty("webdriver.chrome.driver", PropHelper.CHROME_WEBDRIVER);
		DesiredCapabilities capability = new DesiredCapabilities();
		capability.setCapability(CapabilityType.PROXY, SELENIUM_PROXY);
		return new ChromeDriver(capability);
	}

	/**
	 * setup local Internet explorer driver
	 * 
	 * @return WebDriver
	 */
	private WebDriver setupLocalInternetExplorerDriver() {
		if (PropHelper.DEFAULT_IE_ARCH.equals("x86"))
			System.setProperty("webdriver.ie.driver", PropHelper.IE_WEBDRIVER_X86);
		else if (PropHelper.DEFAULT_IE_ARCH.equals("x64") && isOSX64())
			System.setProperty("webdriver.ie.driver", PropHelper.IE_WEBDRIVER_X64);
		else if (isOSX64())
			System.setProperty("webdriver.ie.driver", PropHelper.IE_WEBDRIVER_X64);
		else
			System.setProperty("webdriver.ie.driver", PropHelper.IE_WEBDRIVER_X86);

		DesiredCapabilities capability = DesiredCapabilities.internetExplorer();
		// capability.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
		// true);
		// capability.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL,
		// "about:blank");
		capability.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
		capability.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
		capability.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
		// capability.setCapability("disable-popup-blocking", true);
		capability.setCapability(CapabilityType.HAS_NATIVE_EVENTS, false);
		capability.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
		capability.setCapability(CapabilityType.PROXY, SELENIUM_PROXY);
		return new InternetExplorerDriver(capability);
	}

	/**
	 * setup local firefox driver
	 * 
	 * @return WebDriver
	 */
	private WebDriver setupLocalFirefoxDriver() {
		// if (PropHelper.FIREFOX_PATH != null &&
		// !PropHelper.FIREFOX_PATH.trim().isEmpty())
		// System.setProperty("webdriver.firefox.bin", PropHelper.FIREFOX_PATH);

		FirefoxBinary firefoxBinary;
		if (PropHelper.FIREFOX_PATH != null && !PropHelper.FIREFOX_PATH.trim().isEmpty())
			firefoxBinary = new FirefoxBinary(new File(PropHelper.FIREFOX_PATH));
		else
			firefoxBinary = new FirefoxBinary();

		FirefoxProfile firefoxProfile = new FirefoxProfile();
		firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/zip,application/vnd.ms-excel");

		DesiredCapabilities capability = DesiredCapabilities.firefox();
		capability.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
		capability.setCapability(CapabilityType.PROXY, SELENIUM_PROXY);
		return new FirefoxDriver(firefoxBinary, firefoxProfile, capability);
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
}