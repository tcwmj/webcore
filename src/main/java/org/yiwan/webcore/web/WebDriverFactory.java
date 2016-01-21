package org.yiwan.webcore.web;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.yiwan.webcore.util.Helper;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.util.ProxyHelper;
import org.yiwan.webcore.util.TestTemplate;

import com.thoughtworks.selenium.webdriven.JavascriptLibrary;

import net.lightbody.bmp.client.ClientUtil;

/**
 * @author Kenny Wang
 * 
 */
public class WebDriverFactory {
	private final static Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);

	public String os;
	public String os_version;
	public String browser;
	public String browser_version;
	public String resolution;

	private TestTemplate testcase;
	private WebDriver wd;
	private JavascriptExecutor js;
	private Wait<WebDriver> wait;

	private final static Proxy SELENIUM_PROXY = ClientUtil.createSeleniumProxy(ProxyHelper.getProxy());

	public WebDriverFactory(TestTemplate testcase, String os, String os_version, String browser, String browser_version,
			String resolution) {
		super();
		this.testcase = testcase;
		this.os = (null == os) ? PropHelper.DEFAULT_OS : os;
		this.os_version = (null == os_version) ? PropHelper.DEFAULT_OS_VERSION : os_version;
		this.browser = (null == browser) ? PropHelper.DEFAULT_BROSWER : browser;
		this.browser_version = browser_version;
		this.resolution = resolution;

		if (PropHelper.REMOTE) {
			logger.info("choose remote test mode");
			wd = setupRemoteBrowser();
		} else {
			logger.info("choose local test mode");
			wd = setupLocalBrowser();
		}
		js = (JavascriptExecutor) wd;
		// driver.manage().timeouts()
		// .implicitlyWait(Property.TIMEOUT_INTERVAL, TimeUnit.SECONDS);
		if (PropHelper.MAXIMIZE_BROSWER) {
			logger.info("maximizing browser");
			wd.manage().window().maximize();
		}
		wait = new WebDriverWait(wd, PropHelper.TIMEOUT_INTERVAL, PropHelper.TIMEOUT_POLLING_INTERVAL)
				.ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class)
				.ignoring(UnreachableBrowserException.class);
	}

	public WebDriver createWebDriver(){
		return null;
	}
	
	private WebDriver setupRemoteBrowser() {
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
	private WebDriver setupLocalBrowser() {
		logger.info("choose test browser " + browser);
		switch (browser.toLowerCase()) {
		case "chrome":
			return setupChome();
		case "ie":
			return setupInternetExplorer();
		default:
			return setupFirefox();
		}
	}

	/**
	 * setup chrome browser
	 * 
	 * @return WebDriver
	 */
	private WebDriver setupChome() {
		System.setProperty("webdriver.chrome.driver", PropHelper.CHROME_WEBDRIVER);
		DesiredCapabilities capability = new DesiredCapabilities();
		capability.setCapability(CapabilityType.PROXY, SELENIUM_PROXY);
		return new ChromeDriver(capability);
	}

	/**
	 * setup Internet explorer driver
	 * 
	 * @return WebDriver
	 */
	private WebDriver setupInternetExplorer() {
		if (PropHelper.DEFAULT_IE_ARCH.equals("x86"))
			System.setProperty("webdriver.ie.driver", PropHelper.IE_WEBDRIVER_X86);
		else if (PropHelper.DEFAULT_IE_ARCH.equals("x64") && isOSX64())
			System.setProperty("webdriver.ie.driver", PropHelper.IE_WEBDRIVER_X64);
		else if (isOSX64())
			System.setProperty("webdriver.ie.driver", PropHelper.IE_WEBDRIVER_X64);
		else
			System.setProperty("webdriver.ie.driver", PropHelper.IE_WEBDRIVER_X86);

		DesiredCapabilities capability = DesiredCapabilities.internetExplorer();
		// ignore ie protected mode behavior
		// capability.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
		// true);
		capability.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
		capability.setCapability(CapabilityType.PROXY, SELENIUM_PROXY);
		return new InternetExplorerDriver(capability);
	}

	/**
	 * setup firefox driver
	 * 
	 * @return WebDriver
	 */
	private WebDriver setupFirefox() {
		// if (Property.FIREFOX_DIR != null
		// && !Property.FIREFOX_DIR.trim().isEmpty())
		// System.setProperty("webdriver.firefox.bin", Property.FIREFOX_DIR);

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

	/**
	 * navigate to a specified url
	 * 
	 * @param url
	 */
	public void navigateTo(String url) {
		logger.info("navigate to url " + url);
		waitDocumentReady();
		wd.navigate().to(url);
	}

	/**
	 * navigate forward
	 * 
	 */
	public void navigateForward() {
		logger.info("navigate forward");
		waitDocumentReady();
		wd.navigate().forward();
	}

	/**
	 * navigate back
	 * 
	 */
	public void navigateBack() {
		logger.info("navigate back");
		waitDocumentReady();
		wd.navigate().back();
	}

	/**
	 * close current browser tab
	 */
	public void close() {
		logger.info("close browser tab with title " + wd.getTitle());
		try {
			wd.close();
		} catch (WebDriverException e) {
			logger.warn("close browser tab", e);
		}
	}

	/**
	 * close all browser tabs
	 */
	public void closeAll() {
		logger.info("close all browser tabs");
		if (wd instanceof WebDriver) {
			for (String handle : wd.getWindowHandles()) {
				switchToWindow(handle);
				close();
			}
		}
	}

	/**
	 * quit driver
	 */
	public void quit() {
		logger.info("quit driver");
		try {
			wd.quit();
		} catch (WebDriverException e) {
			logger.warn("quit driver", e);
		}
	}

	/**
	 * Click web element if it's clickable, please use this click method as
	 * default
	 * 
	 * @param locator
	 */
	public void click(By locator) {
		logger.info("click " + locator.toString());
		waitClickable(locator).click();
	}

	/**
	 * click element without considering anything, it may raise unexpected
	 * exception
	 * 
	 * @param locator
	 */
	private void silentClick(By locator) {
		logger.info("silent click " + locator.toString());
		waitDocumentReady();
		wd.findElement(locator).click();
	}

	/**
	 * forced to click element even if it's not clickable, it may raise
	 * unexpected exception, please use method click as default
	 * 
	 * @param locator
	 */
	public void forcedClick(By locator) {
		try {
			click(locator);
		} catch (WebDriverException e) {
			silentClick(locator);
		}
	}

	/**
	 * click an element if it's displayed, otherwise skip this action
	 * 
	 * @param locator
	 */
	public void smartClick(By locator) {
		if (isDisplayed(locator))
			click(locator);
	}

	/**
	 * click the first element if it's displayed, otherwise click the 2nd
	 * element
	 * 
	 * @param locator1
	 * @param locator2
	 */
	public void smartClick(By locator1, By locator2) {
		if (isDisplayed(locator1))
			click(locator1);
		else
			click(locator2);
	}

	/**
	 * click the element if it's displayed, otherwise click the next element,
	 * quit the method until the click action taking effective or elements used
	 * out
	 * 
	 * @param locators
	 */
	public void smartClick(List<By> locators) {
		for (By locator : locators) {
			if (isDisplayed(locator)) {
				click(locator);
				break;
			}
		}
	}

	/**
	 * click a locator by javascript
	 * 
	 * @param locator
	 */
	public void jsClick(By locator) {
		logger.info("click " + locator.toString() + " by javascript");
		js.executeScript("arguments[0].click();", findElement(locator));
	}

	/**
	 * click the first element in a loop while it's displayed
	 * 
	 * @param locator
	 */
	public void loopClick(By locator) {
		while (isDisplayed(locator)) {
			click(locator);
		}
	}

	/**
	 * Double click web element if it's clickable
	 * 
	 * @param locator
	 */
	public void doubleClick(By locator) {
		logger.info("double click " + locator.toString());
		Actions action = new Actions(wd);
		action.doubleClick(waitClickable(locator)).build().perform();
	}

	/**
	 * Type value into the web edit box if it's visible
	 * 
	 * @param locator
	 * @param value
	 */
	public void type(By locator, CharSequence... value) {
		logger.info("type " + value + " on " + locator.toString());
		waitClickable(locator).sendKeys(value);
	}

	/**
	 * Type value into the web edit box if it's visible
	 * 
	 * @param locator
	 * @param value
	 */
	public void type(By locator, String value) {
		logger.info("type " + value + " on " + locator.toString());
		waitClickable(locator).sendKeys(value);
	}

	/**
	 * Clear the content of the web edit box if it's visible
	 * 
	 * @param locator
	 */
	public void clear(By locator) {
		logger.info("clear " + locator.toString());
		waitClickable(locator).clear();
	}

	/**
	 * clear the web edit box and input the value
	 * 
	 * @param locator
	 * @param value
	 */
	public void input(By locator, String value) {
		clear(locator);
		type(locator, value);
	}

	/**
	 * clear the web edit box and input the value, then click the ajax locator
	 * 
	 * @param locator
	 * @param value
	 * @param ajaxLocator
	 */
	public void ajaxInput(By locator, String value, By ajaxLocator) {
		input(locator, value);
		click(ajaxLocator);
	}

	/**
	 * tick web check box if it's visible
	 * 
	 * @param locator
	 * @param value
	 *            true indicate tick on, false indicate tick off
	 */
	public void tick(By locator, Boolean value) {
		logger.info("tick " + value + " on " + locator.toString());
		String checked = getAttribute(locator, "checked");
		if (checked == null || !checked.toLowerCase().equals("true")) {
			if (value)
				click(locator);
		} else {
			if (!value)
				click(locator);
		}
	}

	/**
	 * using java script to tick web check box
	 * 
	 * @param locator
	 * @param value
	 *            true indicate tick on, false indicate tick off
	 */
	public void alteredTick(By locator, Boolean value) {
		logger.info("altered tick " + value + " on " + locator.toString());
		if (value)
			setAttribute(locator, "checked", "checked");
		else
			removeAttribute(locator, "checked");
	}

	/**
	 * Select all options that display text matching the argument. That is, when
	 * given "Bar" this would select an option like:
	 * 
	 * &lt;option value="foo"&gt;Bar&lt;/option&gt;
	 * 
	 * @param locator
	 * @param text
	 *            The visible text to match against
	 */
	public void selectByVisibleText(final By locator, final String text) {
		logger.info("select " + text + " on " + locator.toString());
		waitDocumentReady();
		new Select(waitVisible(locator)).selectByVisibleText(text);
	}

	/**
	 * Clear all selected entries. This is only valid when the SELECT supports
	 * multiple selections.
	 * 
	 * @throws UnsupportedOperationException
	 *             If the SELECT does not support multiple selections
	 * 
	 * @param locator
	 */
	public void deselectAll(final By locator) {
		logger.info("deselect all options on " + locator.toString());
		waitDocumentReady();
		new Select(waitVisible(locator)).deselectAll();
	}

	/**
	 * Select all options that display text matching the argument. That is, when
	 * given "Bar" this would select an option like:
	 * 
	 * &lt;option value="foo"&gt;Bar&lt;/option&gt;
	 * 
	 * @param locator
	 * @param texts
	 *            The visible text to match against
	 */
	public void selectByVisibleText(final By locator, final List<String> texts) {
		logger.info("select " + texts.toString() + " on " + locator.toString());
		waitDocumentReady();
		Select select = new Select(waitVisible(locator));
		for (String text : texts) {
			select.selectByVisibleText(text);
			waitDocumentReady();
		}
	}

	/**
	 * Select the option at the given index. This is done locator examing the
	 * "index" attribute of an element, and not merely locator counting.
	 * 
	 * @param locator
	 * @param index
	 *            The option at this index will be selected
	 */
	public void selectByIndex(final By locator, final int index) {
		logger.info("select index " + index + " on " + locator.toString());
		new Select(waitVisible(locator)).selectByIndex(index);
	}

	/**
	 * Select all options that have a value matching the argument. That is, when
	 * given "foo" this would select an option like:
	 * 
	 * &lt;option value="foo"&gt;Bar&lt;/option&gt;
	 * 
	 * @param locator
	 * @param value
	 *            The value to match against
	 */
	public void selectByValue(final By locator, final String value) {
		logger.info("select value " + value + " on " + locator.toString());
		new Select(waitVisible(locator)).selectByValue(value);
	}

	/**
	 * @param locator
	 * @param text
	 */
	public void waitTextSelected(By locator, String text) {
		logger.debug("wait text " + text + " to be selected on element " + locator.toString());
		waitDocumentReady();
		wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
	}

	/**
	 * wait such text to be present in specified locator
	 * 
	 * @param locator
	 * @param text
	 */
	public void waitTextTyped(By locator, String text) {
		logger.debug("wait text " + text + " to be typed on element " + locator.toString());
		waitDocumentReady();
		wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
	}

	/**
	 * @param locator
	 * @param text
	 * @return whether text is selectable or not
	 */
	public Boolean isTextSelectable(By locator, String text) {
		for (WebElement e : getAllSelectedOptions(locator)) {
			if (text.equals(e.getText())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * assert text exists in the web list
	 * 
	 * @param locator
	 * @param text
	 */
	public void assertTextSelectable(By locator, String text) {
		String message = "assert text " + text + " of locator " + locator.toString() + " to be selectable";
		Assert.assertTrue(isTextSelectable(locator, text), message);
	}

	/**
	 * assert web list current value
	 * 
	 * @param locator
	 * @param text
	 */
	public void assertSelectedValue(By locator, String text) {
		String message = "assert option text " + text + " to be selected of locator " + locator.toString();
		List<WebElement> elements = new Select(findElement(locator)).getAllSelectedOptions();
		Boolean selected = false;
		for (WebElement element : elements) {
			if (element.getText().trim().equals(text)) {
				selected = true;
				break;
			}
		}
		Assert.assertTrue(selected, message);
	}

	/**
	 * @param locator
	 */
	public void moveTo(By locator) {
		logger.info("move mouse to " + locator.toString());
		Actions action = new Actions(wd);
		action.moveToElement(waitVisible(locator)).build().perform();
	}

	/**
	 * whether locator is present or not
	 * 
	 * @param locator
	 * @return whether locator is present or not
	 */
	public boolean isPresent(By locator) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			wd.findElement(locator);
			ret = true;
		} catch (NoSuchElementException | StaleElementReferenceException e) {
		}
		return ret;
	}

	/**
	 * whether alert is present or not
	 * 
	 * @return boolean
	 */
	public boolean isAlertPresent() {
		try {
			wd.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	/**
	 * whether locator is enabled or not
	 * 
	 * @param locator
	 * @return boolean
	 */
	public boolean isEnabled(By locator) {
		Boolean ret = false;
		ret = findElement(locator).isEnabled();
		return ret;
	}

	/**
	 * whether locator is displayed or not
	 * 
	 * @param locator
	 * @return boolean
	 */
	public boolean isDisplayed(By locator) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			ret = wd.findElement(locator).isDisplayed();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
		}
		return ret;
	}

	/**
	 * whether locator is selected or not
	 * 
	 * @param locator
	 * @return boolean
	 */
	public boolean isSelected(By locator) {
		Boolean ret = false;
		ret = findElement(locator).isSelected();
		return ret;
	}

	/**
	 * @param locator
	 * @param enabled
	 */
	public void assertEnabled(By locator, Boolean enabled) {
		Boolean actual = isEnabled(locator);
		String message = "assert enabled of locator " + locator.toString();
		if (enabled) {
			Assert.assertTrue(actual, message);
		} else {
			Assert.assertFalse(actual, message);
		}
	}

	/**
	 * @param locator
	 * @param displayed
	 */
	public void assertDisplayed(By locator, Boolean displayed) {
		Boolean actual = isDisplayed(locator);
		String message = "assert displayed of locator " + locator.toString();
		if (displayed) {
			Assert.assertTrue(actual, message);
		} else {
			Assert.assertFalse(actual, message);
		}
	}

	/**
	 * @param locator
	 * @param selected
	 */
	public void assertSelected(By locator, Boolean selected) {
		Boolean actual = isSelected(locator);
		String message = "assert being selected of locator " + locator.toString();
		if (selected) {
			Assert.assertTrue(actual, message);
		} else {
			Assert.assertFalse(actual, message);
		}
	}

	/**
	 * assert text on locator
	 * 
	 * @param locator
	 * @param text
	 */
	public void assertText(By locator, String text) {
		String message = "assert text of locator " + locator.toString();
		Assert.assertEquals(findElement(locator).getText(), text, message);
	}

	/**
	 * get value of specified attribute
	 * 
	 * @param locator
	 * @param attribute
	 * @return attribute value
	 */
	public String getAttribute(By locator, String attribute) {
		return findElement(locator).getAttribute(attribute);
	}

	/**
	 * @param locator
	 * @param attribute
	 * @param value
	 */
	public void assertAttribute(By locator, String attribute, String value) {
		String actual = getAttribute(locator, attribute);
		String message = "assert attribute " + attribute + " of locator " + locator.toString();
		Assert.assertEquals(actual, value, message);
	}

	/**
	 * assert value of aria-disabled attribute
	 * 
	 * @param locator
	 * @param value
	 */
	public void assertAriaDisabled(By locator, String value) {
		assertAttribute(locator, "aria-disabled", value);
	}

	/**
	 * assert value of aria-selected attribute
	 * 
	 * @param locator
	 * @param value
	 */
	public void assertAriaSelected(By locator, String value) {
		assertAttribute(locator, "aria-selected", value);
	}

	/**
	 * wait the specified locator to be visible
	 * 
	 * @param locator
	 * @return WebElement
	 */
	public WebElement waitVisible(By locator) {
		logger.debug("wait element " + locator.toString() + " to be visible");
		waitDocumentReady();
		return wait.until(ExpectedConditions.visibilityOf(findElement(locator)));
	}

	/**
	 * wait the specified locator to be invisible
	 * 
	 * @param locator
	 */
	public void waitInvisible(By locator) {
		logger.debug("wait element " + locator.toString() + " to be invisible");
		waitDocumentReady();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	/**
	 * wait the specified locator to be present
	 * 
	 * @param locator
	 * @param timeout
	 *            in seconds
	 */
	public void waitPresent(By locator, int timeout) {
		logger.debug("wait element " + locator.toString() + " to be present in " + timeout + " seconds");
		waitDocumentReady();
		long t = System.currentTimeMillis();
		while (System.currentTimeMillis() - t < timeout * 100) {
			if (isPresent(locator)) {
				return;
			}
		}
		logger.warn(
				"Timed out after " + timeout + " seconds waiting for element " + locator.toString() + " to be present");
	}

	/**
	 * wait the specified locator to be absent
	 * 
	 * @param locator
	 * @param timeout
	 *            in seconds
	 */
	public void waitAbsent(By locator, int timeout) {
		logger.debug("wait element " + locator.toString() + " to be absent in " + timeout + " seconds");
		waitDocumentReady();
		long t = System.currentTimeMillis();
		while (System.currentTimeMillis() - t < timeout * 100) {
			if (!isPresent(locator)) {
				return;
			}
		}
		logger.warn(
				"Timed out after " + timeout + " seconds waiting for element " + locator.toString() + " to be absent");
	}

	/**
	 * save screenshot for local or remote testing
	 * 
	 * @param fileName
	 */
	public void saveScreenShot(String fileName) {
		new File(getTestcase().getScreenshotFolder()).mkdirs();
		TakesScreenshot tsDriver;
		if (PropHelper.REMOTE)
			tsDriver = (TakesScreenshot) (new Augmenter().augment(wd));
		else
			tsDriver = (TakesScreenshot) wd;
		File image = new File(getTestcase().getScreenshotFolder() + fileName == null ? "" : fileName + ".png");
		tsDriver.getScreenshotAs(OutputType.FILE).renameTo(image);
		logger.info("take screenshot to " + image.getPath());
	}

	/**
	 * save sreenshot for local or remote testing
	 * 
	 * @param testresult
	 */
	public void saveScreenShot(ITestResult testresult) {
		TakesScreenshot tsDriver;
		if (PropHelper.REMOTE)
			// RemoteWebDriver does not implement the TakesScreenshot class
			// if the driver does have the Capabilities to take a screenshot
			// then Augmenter will add the TakesScreenshot methods to the
			// instance
			tsDriver = (TakesScreenshot) (new Augmenter().augment(wd));
		else
			tsDriver = (TakesScreenshot) wd;

		try {
			File screenshot = tsDriver.getScreenshotAs(OutputType.FILE);
			String filePath = getTestcase().getScreenshotFolder() + testresult.getTestClass().getName() + "."
					+ testresult.getName() + ".png";
			FileUtils.copyFile(screenshot, new File(filePath));
			Reporter.setCurrentTestResult(testresult);
			filePath = filePath.replaceAll("\\\\", "/");
			Method method = testresult.getInstance().getClass().getMethod("report", String.class);
			method.invoke(testresult.getInstance(), Helper.getTestReportStyle("../../../" + filePath,
					"<img src=\"../../../" + filePath + "\" width=\"400\" height=\"300\"/>"));
		} catch (Exception e) {
			logger.error(testresult.getTestClass().getName() + "." + testresult.getName() + " saveScreentshot failed "
					+ e.getMessage(), e);
		}
	}

	/**
	 * assert page's title to be specified value
	 * 
	 * @param title
	 */
	public void assertTitle(String title) {
		waitTitle(title);
	}

	/**
	 * wait page's title to be a specified value
	 * 
	 * @param title
	 */
	public void waitTitle(String title) {
		logger.debug("wait page title to be " + title);
		waitDocumentReady();
		wait.until(ExpectedConditions.titleIs(title));
	}

	/**
	 * get css attribute value
	 * 
	 * @param locator
	 * @param attribute
	 * @return string
	 */
	public String getCssValue(By locator, String attribute) {
		return findElement(locator).getCssValue(attribute);
	}

	/**
	 * assert css attribute value
	 * 
	 * @param locator
	 * @param attribute
	 * @param value
	 */
	public void assertCssValue(By locator, String attribute, String value) {
		String actual = getCssValue(locator, attribute);
		String message = "assert css attribute " + attribute + " of locator " + locator.toString();
		Assert.assertEquals(actual, value, message);
	}

	/**
	 * @param key
	 */
	public void typeKeyEvent(int key) {
		logger.info("type key event " + key);
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(key);
		} catch (AWTException e) {
			logger.error("exception occurred while typing key event", e);
		}
	}

	/**
	 * force to wait specified seconds
	 * 
	 * @param millis
	 *            Milliseconds
	 */
	public void forceWait(int millis) {
		logger.info("force to wait in " + millis + " milliseconds");
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			logger.error("force to wait", e);
		}
	}

	/**
	 * get text on such web element
	 * 
	 * @param locator
	 * @return string
	 */
	public String getText(By locator) {
		return waitVisible(locator).getText();
	}

	/**
	 * set innert text on such web element
	 * 
	 * @param locator
	 * @param text
	 */
	public void setText(By locator, String text) {
		logger.info("set innertext to " + text + " on " + locator.toString());
		js.executeScript("arguments[0].innerText = '" + text + "';", findElement(locator));
	}

	/**
	 * set value on such web element, an alternative approach for method input
	 * 
	 * @param locator
	 * @param value
	 */
	public void setValue(By locator, String value) {
		logger.info("set value " + value + " on " + locator.toString());
		js.executeScript("arguments[0].value = '" + value + "';", findElement(locator));
	}

	/**
	 * get all selected options as a web element list
	 * 
	 * @param locator
	 * @return List&gt;WebElement&lt;
	 */
	public List<WebElement> getAllSelectedOptions(By locator) {
		return new Select(waitVisible(locator)).getAllSelectedOptions();
	}

	/**
	 * get selected text on such web list
	 * 
	 * @param locator
	 * @return string
	 */
	public String getSelectedText(By locator) {
		return getAllSelectedOptions(locator).get(0).getText();
	}

	/**
	 * find element in presence on the page
	 * 
	 * @param locator
	 * @return WebElement
	 */
	private WebElement findElement(By locator) {
		waitDocumentReady();
		return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
	}

	/**
	 * find all elements in presence on the page
	 * 
	 * @param locator
	 * @return List&gt;WebElement&lt;
	 */
	@SuppressWarnings("unused")
	private List<WebElement> findElements(By locator) {
		waitDocumentReady();
		return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
	}

	/**
	 * trigger an event on such element
	 * 
	 * @param locator
	 * @param event
	 *            String, such as "mouseover"
	 */
	public void triggerEvent(By locator, String event) {
		logger.info("trigger event " + event + " on " + locator.toString());
		JavascriptLibrary javascript = new JavascriptLibrary();
		javascript.callEmbeddedSelenium(wd, "triggerEvent", findElement(locator), event);
	}

	/**
	 * fire an event on such element
	 * 
	 * @param locator
	 * @param event
	 *            String, such as "onchange"
	 */
	public void fireEvent(By locator, String event) {
		logger.info("fire event " + event + " on " + locator.toString());
		js.executeScript("arguments[0].fireEvent('" + event + "');", findElement(locator));
	}

	/**
	 * immediately showing the user the result of some action without requiring
	 * the user to manually scroll through the document to find the result
	 * Scrolls the object so that top of the object is visible at the top of the
	 * window.
	 * 
	 * @param locator
	 */
	public void scrollIntoView(By locator) {
		scrollIntoView(locator, true);
	}

	/**
	 * immediately showing the user the result of some action without requiring
	 * the user to manually scroll through the document to find the result
	 * 
	 * @param locator
	 * @param bAlignToTop
	 *            true Default. Scrolls the object so that top of the object is
	 *            visible at the top of the window. <br/>
	 *            false Scrolls the object so that the bottom of the object is
	 *            visible at the bottom of the window.
	 */
	public void scrollIntoView(By locator, Boolean bAlignToTop) {
		logger.info("scroll into view of " + locator.toString() + ", and align to top is " + bAlignToTop);
		js.executeScript("arguments[0].scrollIntoView(" + bAlignToTop.toString() + ");", findElement(locator));
	}

	/**
	 * Scroll page or scrollable element to a specific target element.
	 * 
	 * @param locator
	 */
	public void scrollTo(By locator) {
		logger.info("scroll to " + locator.toString());
		WebElement element = findElement(locator);
		js.executeScript("window.scrollTo(" + element.getLocation().x + "," + element.getLocation().y + ")");
	}

	/**
	 * switch to a window with a specified name or handle
	 * 
	 * @param nameOrHandle
	 */
	public void switchToWindow(String nameOrHandle) {
		logger.info("switch to window with name or handle " + nameOrHandle);
		wd.switchTo().window(nameOrHandle);
	}

	/**
	 * @param locator
	 *            frame locator
	 */
	public void switchToFrame(By locator) {
		logger.info("switch to frame " + locator.toString());
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
	}

	/**
	 * Switch to default content from a frame
	 */
	public void switchToDefault() {
		logger.info("switch to default content");
		wd.switchTo().defaultContent();
	}

	/**
	 * using java script to set element attribute
	 * 
	 * @param locator
	 * @param attribute
	 * @param value
	 */
	public void setAttribute(By locator, String attribute, String value) {
		logger.info("set attribute " + attribute + " to " + value + " on " + locator.toString());
		js.executeScript("arguments[0].setAttribute('" + attribute + "', arguments[1])", findElement(locator), value);
	}

	/**
	 * using java script to remove element attribute
	 * 
	 * @param locator
	 * @param attribute
	 */
	public void removeAttribute(By locator, String attribute) {
		logger.info("remove attribute " + attribute + " on " + locator.toString());
		js.executeScript("arguments[0].removeAttribute('" + attribute + "')", findElement(locator));
	}

	/**
	 * wait until page is loaded completely
	 */
	private void waitDocumentReady() {
		logger.debug("wait document to be ready");
		final long t = System.currentTimeMillis();
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				boolean ready = false;
				if (System.currentTimeMillis() - t > PropHelper.TIMEOUT_DOCUMENT_COMPLETE * 1000)
					throw new TimeoutException("Timed out after " + PropHelper.TIMEOUT_DOCUMENT_COMPLETE
							+ " seconds while waiting for document to be ready");
				try {
					ready = js.executeScript("return document.readyState").equals("complete");
				} catch (WebDriverException e) {
					// logger.warn("javascript error while waiting document to
					// be ready");
					ready = true;
				}
				return ready;
			}
		});

		// generatePageSource();
	}

	/**
	 * generate page source file for HTML static analysis
	 */
	@SuppressWarnings("unused")
	private void generatePageSource() {
		String currentUrl = wd.getCurrentUrl();
		String fileName = currentUrl.replaceFirst("http://.*:\\d+/", "").replaceFirst("\\?.*", "");
		if (!testcase.getCurrentUrl().equals(currentUrl)) {
			String pageSource = wd.getPageSource();
			File file = new File("target/" + fileName + ".html");
			file.getParentFile().mkdirs();
			int i = 0;
			while (file.exists() && i < 100) {
				file = new File("target/" + fileName + "_" + i + ".html");
				i++;
			}
			if (i < 100) {
				try {
					FileWriter fw = new FileWriter(file);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(pageSource);
					bw.close();
					fw.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
			} else {
				logger.warn("skipped generatePageSource due to counts of same page " + currentUrl + " exceeds 100.");
			}
			testcase.setCurrentUrl(currentUrl);
		}
	}

	/**
	 * wait the specified locator to be visible and enable
	 * 
	 * @param locator
	 *            web element locator
	 * @return web element
	 */
	public WebElement waitClickable(By locator) {
		logger.debug("wait element " + locator.toString() + " to be visible and enable");
		waitDocumentReady();
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	/**
	 * using java script to get row number of cell element in web table
	 * 
	 * @param locator
	 */
	public long getCellRow(By locator) {
		long ret = -1;
		ret = (long) js.executeScript("return arguments[0].parentNode.rowIndex", findElement(locator));
		ret++;// row index starts with zero
		return ret;
	}

	/**
	 * using java script to get column number of cell element in web table
	 * 
	 * @param locator
	 */
	public long getCellColumn(By locator) {
		long ret = -1;
		ret = (long) js.executeScript("return arguments[0].cellIndex", findElement(locator));
		ret++;// column index starts with zero
		return ret;
	}

	/**
	 * using java script to get row number of row element in web table
	 * 
	 * @param locator
	 */
	public long getRow(By locator) {
		long ret = -1;
		ret = (long) js.executeScript("return arguments[0].rowIndex", findElement(locator));
		ret++;// row index starts with zero
		return ret;
	}

	/**
	 * using java script to get row count of web table
	 * 
	 * @param locator
	 * @return long
	 */
	public long getRowCount(By locator) {
		long ret = -1;
		ret = (long) js.executeScript("return arguments[0].rows.length", findElement(locator));
		return ret;
	}

	/**
	 * is page source contains such text
	 * 
	 * @param text
	 * @return boolean
	 */
	public Boolean isContains(String text) {
		waitDocumentReady();
		return wd.getPageSource().contains(text);
	}

	/**
	 * assert page source contains such text
	 * 
	 * @param text
	 * @param displayed
	 */
	public void assertTextDisplayed(String text, Boolean displayed) {
		Assert.assertEquals(isContains(text), displayed);
	}

	/**
	 * find alert
	 * 
	 * @return Alert
	 */
	private Alert findAlert() {
		return wait.until(ExpectedConditions.alertIsPresent());// driver.switchTo().alert();
	}

	/**
	 * dismiss the alert window
	 */
	public void dismissAlert() {
		logger.info("dismiss alert " + getAlertText());
		findAlert().dismiss();
	}

	/**
	 * accept the alert window
	 */
	public void acceptAlert() {
		logger.info("accept alert " + getAlertText());
		findAlert().accept();
	}

	/**
	 * get text from alert window
	 * 
	 * @return alert text string
	 */
	public String getAlertText() {
		return findAlert().getText();
	}

	/**
	 * assert text on alert window
	 * 
	 * @param text
	 */
	public void assertAlertText(String text) {
		Assert.assertEquals(getAlertText(), text);
	}

	/**
	 * get page source of current page
	 * 
	 * @return page source string
	 */
	public String getPageSource() {
		waitDocumentReady();
		return wd.getPageSource();
	}

	/**
	 * get current test case instance
	 * 
	 * @return TestCaseTemplate
	 */
	public TestTemplate getTestcase() {
		return testcase;
	}

	/**
	 * get selenium webdriver runtime object
	 * 
	 * @return selenium webdriver
	 */
	public WebDriver getWebDriver() {
		return wd;
	}

	/**
	 * get current url address
	 * 
	 * @return string value of current url
	 */
	public String getCurrentUrl() {
		waitDocumentReady();
		return wd.getCurrentUrl();
	}

	/**
	 * get current page title
	 * 
	 * @return string value of title
	 */
	public String getTitle() {
		waitDocumentReady();
		return wd.getTitle();
	}
}