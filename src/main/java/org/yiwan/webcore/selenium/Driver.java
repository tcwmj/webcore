package org.yiwan.webcore.selenium;

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
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
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
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.yiwan.webcore.test.TestCaseTemplate;
import org.yiwan.webcore.util.Helper;
import org.yiwan.webcore.util.Property;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;

/**
 * @author Kenny Wang
 * 
 */
public class Driver {
	protected Logger logger = Logger.getLogger(this.getClass());

	public String os;
	public String os_version;
	public String browser;
	public String browser_version;
	public String resolution;
	public String downloaddir;

	private TestCaseTemplate testcase;
	private WebDriver driver;
	private Wait<WebDriver> wait;

	public Driver(TestCaseTemplate testcase, String os, String os_version,
			String browser, String browser_version, String resolution) {
		super();
		this.testcase = testcase;
		this.os = (null == os) ? Property.WINDOWS : os;
		this.os_version = (null == os_version) ? Property.WINDOWS_7
				: os_version;
		this.browser = (null == browser) ? Property.DEFAULT_BROSWER : browser;
		this.browser_version = browser_version;
		this.resolution = resolution;

		if (Property.REMOTE) {
			logger.info("choose remote test mode");
			driver = setupRemoteBrowser();
			this.downloaddir = getRemoteDownloadDir();
		} else {
			logger.info("choose local test mode");
			driver = setupLocalBrowser();
			this.downloaddir = getLocalDownloadDir();
		}
		// driver.manage().timeouts()
		// .implicitlyWait(Property.TIMEOUT_INTERVAL, TimeUnit.SECONDS);
		if (Property.MAXIMIZE_BROSWER) {
			logger.info("maximizing browser");
			driver.manage().window().maximize();
		}
		wait = new WebDriverWait(driver, Property.TIMEOUT_INTERVAL,
				Property.POLLING_INTERVAL)
				.ignoring(StaleElementReferenceException.class)
				.ignoring(NoSuchElementException.class)
				.ignoring(UnreachableBrowserException.class);
	}

	private WebDriver setupRemoteBrowser() {
		DesiredCapabilities capability = new DesiredCapabilities();
		URL url = null;
		// setup browserstack remote testing
		if (Property.BROWSERSTACK) {
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
			capability.setCapability("project", Property.PROJECT);
			capability.setCapability("build", Property.BUILD);
			capability.setCapability("browserstack.local",
					Property.BROWSERSTACK_LOCAL);
			capability.setCapability("browserstack.localIdentifier",
					Property.BROWSERSTACK_LOCAL_IDENTIFIER);
			capability.setCapability("browserstack.debug",
					Property.BROWSERSTACK_DEBUG);
			try {
				url = new URL(Property.BROWSERSTACK_URL);
			} catch (MalformedURLException e) {
				logger.error("url " + Property.BROWSERSTACK_URL
						+ " is malformed");
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
				capability
						.setCapability(
								InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
								true);
				capability.setCapability(
						CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
						UnexpectedAlertBehaviour.IGNORE);
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
				url = new URL(Property.REMOTE_ADDRESS);
			} catch (MalformedURLException e) {
				logger.error("url " + Property.REMOTE_ADDRESS + " is malformed");
			}
		}
		RemoteWebDriver rwd = new RemoteWebDriver(url, capability);
		rwd.setFileDetector(new LocalFileDetector());
		return rwd;
	}

	/**
	 * @return
	 */
	private WebDriver setupLocalBrowser() {
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
	 * @return
	 */
	private WebDriver setupChome() {
		System.setProperty("webdriver.chrome.driver",
				Property.WEB_DRIVER_CHROME);
		return new ChromeDriver();
	}

	/**
	 * setup Internet explorer driver
	 * 
	 * @return
	 */
	private WebDriver setupInternetExplorer() {
		if (Property.INTERNET_EXPLORER_PREFERRED.equals("x86"))
			System.setProperty("webdriver.ie.driver",
					Property.WEB_DRIVER_IE_X86);
		else if (Property.INTERNET_EXPLORER_PREFERRED.equals("x64")
				&& isOSX64())
			System.setProperty("webdriver.ie.driver",
					Property.WEB_DRIVER_IE_X64);
		else if (isOSX64())
			System.setProperty("webdriver.ie.driver",
					Property.WEB_DRIVER_IE_X64);
		else
			System.setProperty("webdriver.ie.driver",
					Property.WEB_DRIVER_IE_X86);

		DesiredCapabilities capabilities = DesiredCapabilities
				.internetExplorer();
		capabilities
				.setCapability(
						InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
						true);
		// ignore the unexpected alert behavior, so as to handle the business
		// alert in the test script
		capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
				UnexpectedAlertBehaviour.IGNORE);
		return new InternetExplorerDriver(capabilities);
	}

	/**
	 * setup firefox driver
	 * 
	 * @return
	 */
	private WebDriver setupFirefox() {
		// if (Property.FIREFOX_DIR != null
		// && !Property.FIREFOX_DIR.trim().isEmpty())
		// System.setProperty("webdriver.firefox.bin", Property.FIREFOX_DIR);

		FirefoxBinary firefoxBinary;
		if (Property.FIREFOX_DIR != null
				&& !Property.FIREFOX_DIR.trim().isEmpty())
			firefoxBinary = new FirefoxBinary(new File(Property.FIREFOX_DIR));
		else
			firefoxBinary = new FirefoxBinary();

		FirefoxProfile firefoxProfile = new FirefoxProfile();
		firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/zip,application/vnd.ms-excel");

		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
				UnexpectedAlertBehaviour.IGNORE);

		return new FirefoxDriver(firefoxBinary, firefoxProfile, capabilities);
	}

	/**
	 * @return
	 */
	private Boolean isOSX64() {
		Properties props = System.getProperties();
		String arch = props.getProperty("os.arch");
		return arch.contains("64");
	}

	/**
	 * @return
	 */
	private String getRemoteDownloadDir() {
		if (os == Property.OSX)
			return Property.DOWNLOAD_OSX_DIR;
		else {
			if (os_version == Property.WINDOWS_XP)
				return Property.DOWNLOAD_WINXP_DIR;
			else
				return Property.DOWNLOAD_WINNT_DIR;
		}
	}

	/**
	 * @return
	 */
	private String getLocalDownloadDir() {
		Properties props = System.getProperties();
		String osname = props.getProperty("os.name").toUpperCase();
		String username = props.getProperty("user.name");
		if (osname.compareTo(Property.WINDOWS) >= 0)
			if (osname.compareTo(Property.WINDOWS_XP) >= 0)
				return "C:/Documents and Settings/" + username
						+ "/My Documents/Downloads";
			else
				return "C:/Users/" + username + "/Downloads";
		else
			return "/Users/" + username + "/Downloads";
	}

	/**
	 * navigate to a specified url
	 * 
	 * @param url
	 */
	public void navigateTo(String url) {
		logger.info("Try to navigate to url " + url);
		driver.navigate().to(url);
		waitDocumentReady();
	}

	/**
	 * navigate forward
	 * 
	 */
	public void navigateForward() {
		logger.info("Try to navigate forward");
		driver.navigate().forward();
		waitDocumentReady();
	}

	/**
	 * navigate back
	 * 
	 */
	public void navigateBack() {
		logger.info("Try to navigate back");
		driver.navigate().back();
		waitDocumentReady();
	}

	/**
	 * quit driver
	 */
	public void quit() {
		logger.info("Try to quit driver");
		if (driver instanceof WebDriver)
			try {
				driver.quit();
			} catch (UnreachableBrowserException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Click web element if it's clickable, please use this click method as
	 * default
	 * 
	 * @param locator
	 */
	public void click(By locator) {
		logger.info("Try to click " + locator.toString());
		wait.until(
				ExpectedConditions.elementToBeClickable(findElement(locator)))
				.click();
		waitDocumentReady();
	}

	/**
	 * click element without considering anything, it may raise unexpected
	 * exception
	 * 
	 * @param locator
	 */
	private void silentClick(By locator) {
		logger.info("Try to silent click " + locator.toString());
		driver.findElement(locator).click();
		waitDocumentReady();
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
	 * @param locator
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
	 * @param locator
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
		logger.info("Try to double click " + locator.toString());
		WebElement element = wait.until(ExpectedConditions
				.elementToBeClickable(findElement(locator)));
		Actions action = new Actions(driver);
		action.doubleClick(element).build().perform();
		waitDocumentReady();
	}

	/**
	 * Type value into the web edit box if it's visible
	 * 
	 * @param locator
	 * @param value
	 */
	public void type(By locator, CharSequence... value) {
		logger.info("Try to type value " + value + " on " + locator.toString());
		wait.until(ExpectedConditions.visibilityOf(findElement(locator)))
				.sendKeys(value);
		waitDocumentReady();
	}

	/**
	 * Clear the content of the web edit box if it's visible
	 * 
	 * @param locator
	 */
	public void clear(By locator) {
		logger.info("Try to clear value on " + locator.toString());
		wait.until(ExpectedConditions.visibilityOf(findElement(locator)))
				.clear();
		waitDocumentReady();
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
		logger.info("Try to tick " + value + " on " + locator.toString());
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
		logger.info("Try to altered tick " + value + " on "
				+ locator.toString());
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
		logger.info("Try to select text " + text + " on " + locator.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(locator)));
		new Select(element).selectByVisibleText(text);
		waitDocumentReady();
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
		logger.info("Try to deselect all options on " + locator.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(locator)));
		new Select(element).deselectAll();
		waitDocumentReady();
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
		logger.info("Try to select text " + texts.toString() + " on "
				+ locator.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(locator)));
		Select select = new Select(element);
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
		logger.info("Try to select index " + index + " on "
				+ locator.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(locator)));
		new Select(element).selectByIndex(index);
		waitDocumentReady();
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
		logger.info("Try to select value " + value + " on "
				+ locator.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(locator)));
		new Select(element).selectByValue(value);
		waitDocumentReady();
	}

	/**
	 * @param locator
	 * @param text
	 */
	public void waitTextSelected(By locator, String text) {
		logger.info("wait text " + text + " to be selected on "
				+ locator.toString());
		wait.until(ExpectedConditions.textToBePresentInElement(
				findElement(locator), text));
	}

	/**
	 * wait such text to be present in specified locator
	 * 
	 * @param locator
	 * @param text
	 */
	public void waitTextTyped(By locator, String text) {
		logger.info("wait text " + text + " to be typed on "
				+ locator.toString());
		wait.until(ExpectedConditions.textToBePresentInElementValue(
				findElement(locator), text));
	}

	/**
	 * @param locator
	 * @param text
	 * @return
	 */
	public Boolean isTextSelectable(By locator, String text) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(locator)));
		List<WebElement> elements = element.findElements(By.tagName("option"));
		for (WebElement e : elements) {
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
		String message = "assert text " + text + " of locator "
				+ locator.toString() + " to be selectable";
		Assert.assertTrue(isTextSelectable(locator, text), message);
	}

	/**
	 * assert web list current value
	 * 
	 * @param locator
	 * @param text
	 */
	public void assertSelectedValue(By locator, String text) {
		String message = "assert option text " + text
				+ " to be selected of locator " + locator.toString();
		List<WebElement> elements = new Select(findElement(locator))
				.getAllSelectedOptions();
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
		logger.info("Try to move mouse to " + locator.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(locator)));
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
		waitDocumentReady();
	}

	/**
	 * @param locator
	 * @return
	 */
	public boolean isPresent(By locator) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			driver.findElement(locator);
			ret = true;
		} catch (NoSuchElementException | StaleElementReferenceException e) {
		}
		return ret;
	}

	/**
	 * @return
	 */
	public boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	/**
	 * @param locator
	 * @return
	 */
	public boolean isEnabled(By locator) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			ret = findElement(locator).isEnabled();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
		}
		return ret;

	}

	/**
	 * @param locator
	 * @return
	 */
	public boolean isDisplayed(By locator) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			ret = driver.findElement(locator).isDisplayed();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
		}
		return ret;
	}

	/**
	 * @param locator
	 * @return
	 */
	public boolean isSelected(By locator) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			ret = driver.findElement(locator).isSelected();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
		}
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
		String message = "assert being selected of locator "
				+ locator.toString();
		if (selected) {
			Assert.assertTrue(actual, message);
		} else {
			Assert.assertFalse(actual, message);
		}
	}

	/**
	 * @param locator
	 * @param text
	 */
	public void assertText(By locator, String text) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(locator)));
		String message = "assert text of locator " + locator.toString();
		Assert.assertEquals(element.getText(), text, message);
	}

	/**
	 * @param locator
	 * @param attribute
	 * @return
	 */
	public String getAttribute(By locator, String attribute) {
		WebElement element = findElement(locator);
		return element.getAttribute(attribute);
	}

	/**
	 * @param locator
	 * @param attribute
	 * @param value
	 */
	public void assertAttribute(By locator, String attribute, String value) {
		String actual = getAttribute(locator, attribute);
		String message = "assert attribute " + attribute + " of locator "
				+ locator.toString();
		Assert.assertEquals(actual, value, message);
	}

	/**
	 * @param locator
	 * @param value
	 */
	public void assertAriaDisabled(By locator, String value) {
		assertAttribute(locator, "aria-disabled", value);
	}

	/**
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
	 * @return
	 */
	public WebElement waitVisible(By locator) {
		logger.info("wait element " + locator.toString() + " to be visible");
		return wait
				.until(ExpectedConditions.visibilityOf(findElement(locator)));
	}

	/**
	 * wait the specified locator to be invisible
	 * 
	 * @param locator
	 */
	public void waitInvisible(By locator) {
		logger.info("wait element " + locator.toString() + " to be invisible");
		wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	/**
	 * wait the specified locator to be visible
	 * 
	 * @param locator
	 * @param timeout
	 *            in seconds
	 */
	public void waitVisible(By locator, int timeout) {
		logger.info("wait element " + locator.toString() + " to be visible in "
				+ timeout + " seconds");
		long t = System.currentTimeMillis();
		while (System.currentTimeMillis() - t < timeout * 100) {
			if (isPresent(locator)) {
				return;
			}
		}
		logger.warn("Timed out after " + timeout
				+ " seconds waiting for element " + locator.toString()
				+ " to be visible");
	}

	/**
	 * wait the specified locator to be invisible
	 * 
	 * @param locator
	 * @param timeout
	 *            in seconds
	 */
	public void waitInvisible(By locator, int timeout) {
		logger.info("wait element " + locator.toString()
				+ " to be invisible in " + timeout + " seconds");
		long t = System.currentTimeMillis();
		while (System.currentTimeMillis() - t < timeout * 100) {
			if (!isPresent(locator)) {
				return;
			}
		}
		logger.warn("Timed out after " + timeout
				+ " seconds waiting for element " + locator.toString()
				+ " to be invisible");
	}

	/**
	 * save sreenshot for local or remote testing
	 * 
	 * @param fileName
	 */
	public void saveScreenShot(String fileName) {
		if (!(new File(Property.SCREENSHOT_DIR).isDirectory())) {
			new File(Property.SCREENSHOT_DIR).mkdir();
		}
		TakesScreenshot tsDriver;
		if (Property.REMOTE)
			tsDriver = (TakesScreenshot) (new Augmenter().augment(driver));
		else
			tsDriver = (TakesScreenshot) driver;
		File image = new File(Property.SCREENSHOT_DIR + File.separator
				+ fileName == null ? "" : fileName + ".png");
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
		if (Property.REMOTE)
			// RemoteWebDriver does not implement the TakesScreenshot class
			// if the driver does have the Capabilities to take a screenshot
			// then Augmenter will add the TakesScreenshot methods to the
			// instance
			tsDriver = (TakesScreenshot) (new Augmenter().augment(driver));
		else
			tsDriver = (TakesScreenshot) driver;

		try {
			File screenshot = tsDriver.getScreenshotAs(OutputType.FILE);
			String filePath = Property.SCREENSHOT_DIR + File.separator
					+ testresult.getTestClass().getName() + "."
					+ testresult.getName() + ".png";
			FileUtils.copyFile(screenshot, new File(filePath));
			Reporter.setCurrentTestResult(testresult);
			filePath = filePath.replaceAll("\\\\", "/");
			Method method = testresult.getInstance().getClass()
					.getMethod("report", String.class);
			method.invoke(testresult.getInstance(), Helper.getTestReportStyle(
					"../../../" + filePath, "<img src=\"../../../" + filePath
							+ "\" width=\"400\" height=\"300\"/>"));
		} catch (Exception e) {
			logger.error(
					testresult.getTestClass().getName() + "."
							+ testresult.getName() + " saveScreentshot failed "
							+ e.getMessage(), e);
		}
	}

	/**
	 * assert page's title to be specified value
	 * 
	 * @param title
	 */
	public void assertTitle(String title) {
		wait.until(ExpectedConditions.titleIs(title));
	}

	/**
	 * wait page's title to be a specified value
	 * 
	 * @param title
	 */
	public void waitTitle(String title) {
		logger.info("wait page title to be " + title);
		wait.until(ExpectedConditions.titleIs(title));
	}

	/**
	 * @param locator
	 * @param attribute
	 * @return
	 */
	public String getCSSAttribute(By locator, String attribute) {
		WebElement element = findElement(locator);
		return element.getCssValue(attribute);
	}

	/**
	 * @param locator
	 * @param attribute
	 * @param value
	 */
	public void assertCSSAttribute(By locator, String attribute, String value) {
		String actual = getCSSAttribute(locator, attribute);
		String message = "assert css attribute " + attribute + " of locator "
				+ locator.toString();
		Assert.assertEquals(actual, value, message);
	}

	/**
	 * @param key
	 */
	public void typeKeyEvent(int key) {
		logger.info("Try to type key event " + key);
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(key);
		} catch (AWTException e) {
			logger.error("typeKeyEvent error " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * force to wait specified seconds
	 * 
	 * @param millis
	 *            Milliseconds
	 */
	public void forceWait(int millis) {
		logger.info("force wait in " + millis + " seconds");
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	/**
	 * get text on such web element
	 * 
	 * @param locator
	 * @return string
	 */
	public String getText(By locator) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(locator)));
		return element.getText();
	}

	/**
	 * set text on such web element
	 * 
	 * @param locator
	 * @param text
	 */
	public void setText(By locator, String text) {
		logger.info("set text " + text + " on element " + locator.toString());
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		try {
			javascript.executeScript(
					"arguments[0].innerText = '" + text + "';",
					findElement(locator));
		} catch (WebDriverException e) {
			// e.printStackTrace();
		}

	}

	/**
	 * @param locator
	 * @return
	 */
	public List<WebElement> getAllSelectedOptions(By locator) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(locator)));
		return new Select(element).getAllSelectedOptions();
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
	 * @param locator
	 * @return
	 */
	private WebElement findElement(By locator) {
		waitDocumentReady();
		return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
	}

	/**
	 * @param locator
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<WebElement> findElements(By locator) {
		waitDocumentReady();
		return wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(locator));
	}

	/**
	 * trigger an event on such element
	 * 
	 * @param locator
	 * @param event
	 *            String, such as "mouseover"
	 */
	public void triggerEvent(By locator, String event) {
		logger.info("trigger event " + event + " on element "
				+ locator.toString());
		JavascriptLibrary javascript = new JavascriptLibrary();
		try {
			javascript.callEmbeddedSelenium(driver, "triggerEvent",
					findElement(locator), event);
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + locator.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			// e2.printStackTrace();
		}
		waitDocumentReady();
	}

	/**
	 * fire an event on such element
	 * 
	 * @param locator
	 * @param event
	 *            String, such as "onchange"
	 */
	public void fireEvent(By locator, String event) {
		logger.info("fire event " + event + " on element " + locator.toString());
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		try {
			javascript.executeScript("arguments[0].fireEvent(\"" + event
					+ "\")", findElement(locator));
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + locator.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			// e2.printStackTrace();
		}
		waitDocumentReady();
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
		logger.info("scroll into view of element " + locator.toString()
				+ ", and align to top is " + bAlignToTop);
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		try {
			javascript.executeScript("arguments[0].scrollIntoView("
					+ bAlignToTop.toString() + ");", findElement(locator));
		} catch (WebDriverException e) {
			logger.error(e);
		}
		waitDocumentReady();
	}

	/**
	 * Scroll page or scrollable element to a specific target element.
	 * 
	 * @param locator
	 */
	public void scrollTo(By locator) {
		logger.info("scroll to element " + locator.toString());
		WebElement element = findElement(locator);
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		try {
			javascript.executeScript("window.scrollTo("
					+ element.getLocation().x + "," + element.getLocation().y
					+ ")");
		} catch (WebDriverException e) {
			logger.error(e);
		}
		waitDocumentReady();
	}

	/**
	 * @param locator
	 *            frame locator
	 */
	public void switchToFrame(By locator) {
		logger.info("switch to frame " + locator.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(locator)));
		driver.switchTo().frame(element);
	}

	/**
	 * Switch to default content from a frame
	 */
	public void switchToDefault() {
		logger.info("switch to default content");
		driver.switchTo().defaultContent();
	}

	/**
	 * using java script to set element attribute
	 * 
	 * @param locator
	 * @param attribute
	 * @param value
	 */
	public void setAttribute(By locator, String attribute, String value) {
		logger.info("set attribute " + attribute + " to " + value
				+ " on element " + locator.toString());
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		try {
			javascript.executeScript("arguments[0].setAttribute('" + attribute
					+ "', arguments[1])", findElement(locator), value);
		} catch (WebDriverException e) {
			logger.error(e);
		}
	}

	/**
	 * using java script to remove element attribute
	 * 
	 * @param locator
	 * @param attribute
	 */
	public void removeAttribute(By locator, String attribute) {
		logger.info("remove attribute " + attribute + " on element "
				+ locator.toString());
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		try {
			javascript.executeScript("arguments[0].removeAttribute('"
					+ attribute + "')", findElement(locator));
		} catch (WebDriverException e) {
			logger.error(e);
		}
	}

	/**
	 * wait until page is loaded completely
	 */
	private void waitDocumentReady() {
		final long t = System.currentTimeMillis();
		try {
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					if (System.currentTimeMillis() - t > Property.TIMEOUT_DOCUMENT_COMPLETE * 1000)
						throw new TimeoutException("Timed out after "
								+ Property.TIMEOUT_DOCUMENT_COMPLETE
								+ " seconds waiting for document to be ready");
					return ((JavascriptExecutor) driver).executeScript(
							"return document.readyState").equals("complete");
				}
			});
		} catch (TimeoutException e1) {
			logger.warn(e1.getMessage());
		} catch (WebDriverException e2) {
			logger.warn("excpetion occurred while trying to wait document to be ready");
		}
		// generatePageSource();
	}

	/**
	 * generate page source file for HTML static analysis
	 */
	@SuppressWarnings("unused")
	private void generatePageSource() {
		String currentUrl = driver.getCurrentUrl();
		String fileName = currentUrl.replaceFirst("http://.*:\\d+/", "")
				.replaceFirst("\\?.*", "");
		if (!testcase.getCurrentUrl().equals(currentUrl)) {
			String pageSource = driver.getPageSource();
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
				logger.warn("skipped generatePageSource due to counts of same page "
						+ currentUrl + " exceeds 100.");
			}
			testcase.setCurrentUrl(currentUrl);
		}
	}

	/**
	 * wait locator to be clickable
	 * 
	 * @param locator
	 */
	public void waitClickable(By locator) {
		logger.info("wait element " + locator.toString() + " to be clickable");
		wait.until(ExpectedConditions
				.elementToBeClickable(findElement(locator)));
	}

	/**
	 * using java script to get row number of cell element in web table
	 * 
	 * @param locator
	 */
	public long getCellRow(By locator) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		long ret = -1;
		try {
			ret = (long) javascript.executeScript(
					"return arguments[0].parentNode.rowIndex",
					findElement(locator));
			ret++;// row index starts with zero
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + locator.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			e2.printStackTrace();
		}
		return ret;
	}

	/**
	 * using java script to get column number of cell element in web table
	 * 
	 * @param locator
	 */
	public long getCellColumn(By locator) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		long ret = -1;
		try {
			ret = (long) javascript.executeScript(
					"return arguments[0].cellIndex", findElement(locator));
			ret++;// column index starts with zero
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + locator.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			e2.printStackTrace();
		}
		return ret;
	}

	/**
	 * using java script to get row number of row element in web table
	 * 
	 * @param locator
	 */
	public long getRow(By locator) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		long ret = -1;
		try {
			ret = (long) javascript.executeScript(
					"return arguments[0].rowIndex", findElement(locator));
			ret++;// row index starts with zero
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + locator.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			e2.printStackTrace();
		}
		return ret;
	}

	/**
	 * using java script to get row count of web table
	 * 
	 * @param locator
	 */
	public long getRowCount(By locator) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		long ret = -1;
		try {
			ret = (long) javascript.executeScript(
					"return arguments[0].rows.length", findElement(locator));
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + locator.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			e2.printStackTrace();
		}
		return ret;
	}

	/**
	 * is page source contains such text
	 * 
	 * @param text
	 * @return
	 */
	public Boolean isContains(String text) {
		return driver.getPageSource().contains(text);
	}

	/**
	 * assert page source contains such text
	 * 
	 * @param text
	 * @param contains
	 */
	public void assertTextDisplayed(String text, Boolean displayed) {
		assertTextDisplayed(text, displayed, Property.NAVIGATION_INTERVAL);
	}

	/**
	 * assert page source contains such text
	 * 
	 * @param text
	 * @param contains
	 * @param timeout
	 *            in seconds
	 */
	public void assertTextDisplayed(String text, Boolean displayed, long timeout) {
		long t = System.currentTimeMillis();
		while (System.currentTimeMillis() - t < timeout * 1000) {
			if (displayed.equals(isContains(text)))
				break;
		}
		Assert.assertEquals(isContains(text), displayed);
	}

	/**
	 * find alert
	 * 
	 * @return
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
	 * @return
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

}