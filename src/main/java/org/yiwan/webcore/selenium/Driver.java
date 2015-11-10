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
			driver = setupRemoteBrowser();
			this.downloaddir = getRemoteDownloadDir();
		} else {
			driver = setupLocalBrowser();
			this.downloaddir = getLocalDownloadDir();
		}
		// driver.manage().timeouts()
		// .implicitlyWait(Property.TIMEOUT_INTERVAL, TimeUnit.SECONDS);
		if (Property.MAXIMIZE_BROSWER)
			driver.manage().window().maximize();
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
			if (os != null)
				capability.setCapability("os", os);
			if (os_version != null)
				capability.setCapability("os_version", os_version);
			capability.setCapability("browser", browser);
			if (browser_version != null)
				capability.setCapability("browser_version", browser_version);
			if (resolution != null)
				capability.setCapability("resolution", resolution);
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
		} else {// setup local remote testing
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
			}
			capability
					.setBrowserName(browser.equalsIgnoreCase("ie") ? BrowserType.IE
							: browser);
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
		logger.debug("Try to navigate to url " + url);
		driver.navigate().to(url);
		waitDocumentReady();
	}

	/**
	 * navigate forward
	 * 
	 */
	public void navigateForward() {
		driver.navigate().forward();
		waitDocumentReady();
	}

	/**
	 * navigate back
	 * 
	 */
	public void navigateBack() {
		driver.navigate().back();
		waitDocumentReady();
	}

	/**
	 * quit driver
	 */
	public void quit() {
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
	 * @param by
	 */
	public void click(By by) {
		logger.debug("Try to click " + by.toString());
		wait.until(ExpectedConditions.elementToBeClickable(findElement(by)))
				.click();
		waitDocumentReady();
	}

	/**
	 * click element without considering anything, it may raise unexpected
	 * exception
	 * 
	 * @param by
	 */
	private void silentClick(By by) {
		driver.findElement(by).click();
		waitDocumentReady();
	}

	/**
	 * forced to click element even if it's not clickable, it may raise
	 * unexpected exception, please use method click as default
	 * 
	 * @param by
	 */
	public void forcedClick(By by) {
		try {
			click(by);
		} catch (StaleElementReferenceException | TimeoutException e) {
			silentClick(by);
		}
	}

	/**
	 * click an element if it's displayed, otherwise skip this action
	 * 
	 * @param by
	 */
	public void smartClick(By by) {
		if (isDisplayed(by))
			click(by);
	}

	/**
	 * click the first element if it's displayed, otherwise click the 2nd
	 * element
	 * 
	 * @param by
	 */
	public void smartClick(By by1, By by2) {
		if (isDisplayed(by1))
			click(by1);
		else
			click(by2);
	}

	/**
	 * click the element if it's displayed, otherwise click the next element,
	 * quit the method until the click action taking effective or elements used
	 * out
	 * 
	 * @param by
	 */
	public void smartClick(List<By> bys) {
		for (By by : bys) {
			if (isDisplayed(by)) {
				click(by);
				break;
			}
		}
	}

	/**
	 * click the first element in a loop while it's displayed
	 * 
	 * @param by
	 */
	public void loopClick(By by) {
		while (isDisplayed(by)) {
			click(by);
		}
	}

	/**
	 * Double click web element if it's clickable
	 * 
	 * @param by
	 */
	public void doubleClick(By by) {
		logger.debug("Try to double click " + by.toString());
		WebElement element = wait.until(ExpectedConditions
				.elementToBeClickable(findElement(by)));
		Actions action = new Actions(driver);
		action.doubleClick(element).build().perform();
		waitDocumentReady();
	}

	/**
	 * Type value into the web edit box if it's visible
	 * 
	 * @param by
	 * @param value
	 */
	public void type(By by, CharSequence... value) {
		logger.debug("Try to type value " + value + " on " + by.toString());
		wait.until(ExpectedConditions.visibilityOf(findElement(by))).sendKeys(
				value);
		waitDocumentReady();
	}

	/**
	 * Clear the content of the web edit box if it's visible
	 * 
	 * @param by
	 */
	public void clear(By by) {
		logger.debug("Try to clear value on " + by.toString());
		wait.until(ExpectedConditions.visibilityOf(findElement(by))).clear();
		waitDocumentReady();
	}

	/**
	 * clear the web edit box and input the value
	 * 
	 * @param by
	 * @param value
	 */
	public void input(By by, String value) {
		clear(by);
		type(by, value);
	}

	/**
	 * clear the web edit box and input the value, then click the ajax locator
	 * 
	 * @param by
	 * @param value
	 * @param ajaxLocator
	 */
	public void ajaxInput(By by, String value, By ajaxLocator) {
		input(by, value);
		click(ajaxLocator);
	}

	/**
	 * tick web check box if it's visible
	 * 
	 * @param by
	 * @param value
	 *            true indicate tick on, false indicate tick off
	 */
	public void tick(By by, Boolean value) {
		String checked = getAttribute(by, "checked");
		if (checked == null || !checked.toLowerCase().equals("true")) {
			if (value)
				click(by);
		} else {
			if (!value)
				click(by);
		}
	}

	/**
	 * using java script to tick web check box
	 * 
	 * @param by
	 * @param value
	 *            true indicate tick on, false indicate tick off
	 */
	public void alteredTick(By by, Boolean value) {
		if (value)
			setAttribute(by, "checked", "checked");
		else
			removeAttribute(by, "checked");
	}

	/**
	 * Select all options that display text matching the argument. That is, when
	 * given "Bar" this would select an option like:
	 * 
	 * &lt;option value="foo"&gt;Bar&lt;/option&gt;
	 * 
	 * @param by
	 * @param text
	 *            The visible text to match against
	 */
	public void selectByVisibleText(final By by, final String text) {
		logger.debug("Try to select text " + text + " on " + by.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(by)));
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
	 * @param by
	 */
	public void deselectAll(final By by) {
		logger.debug("Try to deselect all options on " + by.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(by)));
		new Select(element).deselectAll();
		waitDocumentReady();
	}

	/**
	 * Select all options that display text matching the argument. That is, when
	 * given "Bar" this would select an option like:
	 * 
	 * &lt;option value="foo"&gt;Bar&lt;/option&gt;
	 * 
	 * @param by
	 * @param texts
	 *            The visible text to match against
	 */
	public void selectByVisibleText(final By by, final List<String> texts) {
		logger.debug("Try to select text " + texts.toString() + " on "
				+ by.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(by)));
		Select select = new Select(element);
		for (String text : texts) {
			select.selectByVisibleText(text);
			waitDocumentReady();
		}
	}

	/**
	 * Select the option at the given index. This is done by examing the "index"
	 * attribute of an element, and not merely by counting.
	 * 
	 * @param by
	 * @param index
	 *            The option at this index will be selected
	 */
	public void selectByIndex(final By by, final int index) {
		logger.debug("Try to select index " + index + " on " + by.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(by)));
		new Select(element).selectByIndex(index);
		waitDocumentReady();
	}

	/**
	 * Select all options that have a value matching the argument. That is, when
	 * given "foo" this would select an option like:
	 * 
	 * &lt;option value="foo"&gt;Bar&lt;/option&gt;
	 * 
	 * @param by
	 * @param value
	 *            The value to match against
	 */
	public void selectByValue(final By by, final String value) {
		logger.debug("Try to select value " + value + " on " + by.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(by)));
		new Select(element).selectByValue(value);
		waitDocumentReady();
	}

	/**
	 * @param by
	 * @param text
	 */
	public void waitTextSelected(By by, String text) {
		wait.until(ExpectedConditions.textToBePresentInElement(findElement(by),
				text));
	}

	/**
	 * wait such text to be present in specified locator
	 * 
	 * @param by
	 * @param text
	 */
	public void waitTextTyped(By by, String text) {
		wait.until(ExpectedConditions.textToBePresentInElementValue(
				findElement(by), text));
	}

	/**
	 * @param by
	 * @param text
	 * @return
	 */
	public Boolean isTextSelectable(By by, String text) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(by)));
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
	 * @param by
	 * @param text
	 */
	public void assertTextSelectable(By by, String text) {
		String message = "assert text " + text + " of locator " + by.toString()
				+ " to be selectable";
		Assert.assertTrue(isTextSelectable(by, text), message);
	}

	/**
	 * assert web list current value
	 * 
	 * @param by
	 * @param text
	 */
	public void assertSelectedValue(By by, String text) {
		String message = "assert option text " + text
				+ " to be selected of locator " + by.toString();
		List<WebElement> elements = new Select(findElement(by))
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
	 * @param by
	 */
	public void moveTo(By by) {
		logger.debug("Try to move mouse to " + by.toString());
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(by)));
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
		waitDocumentReady();
	}

	/**
	 * @param by
	 * @return
	 */
	public boolean isPresent(By by) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			driver.findElement(by);
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
	 * @param by
	 * @return
	 */
	public boolean isEnabled(By by) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			// driver.manage().timeouts()
			// .implicitlyWait(seconds, TimeUnit.SECONDS);
			ret = findElement(by).isEnabled();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			e.printStackTrace();
		}
		// finally {
		// driver.manage()
		// .timeouts()
		// .implicitlyWait(Property.TIMEOUT_INTERVAL, TimeUnit.SECONDS);
		// }
		// return isEnabled(by, Property.POLLING_INTERVAL);
		return ret;

	}

	/**
	 * @param by
	 * @return
	 */
	public boolean isDisplayed(By by) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			// driver.manage().timeouts()
			// .implicitlyWait(seconds, TimeUnit.SECONDS);
			ret = driver.findElement(by).isDisplayed();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
		}
		// finally {
		// driver.manage()
		// .timeouts()
		// .implicitlyWait(Property.TIMEOUT_INTERVAL, TimeUnit.SECONDS);
		// }
		// return isDisplayed(by, Property.TIMEOUT_INTERVAL);
		return ret;
	}

	/**
	 * @param by
	 * @return
	 */
	public boolean isSelected(By by) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			// driver.manage().timeouts()
			// .implicitlyWait(seconds, TimeUnit.SECONDS);
			ret = driver.findElement(by).isSelected();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			e.printStackTrace();
		}
		// finally {
		// driver.manage()
		// .timeouts()
		// .implicitlyWait(Property.TIMEOUT_INTERVAL, TimeUnit.SECONDS);
		// }
		// return isSelected(by, Property.TIMEOUT_INTERVAL);
		return ret;

	}

	/**
	 * @param by
	 * @param enabled
	 */
	public void assertEnabled(By by, Boolean enabled) {
		Boolean actual = isEnabled(by);
		String message = "assert enabled of locator " + by.toString();
		if (enabled) {
			Assert.assertTrue(actual, message);
		} else {
			Assert.assertFalse(actual, message);
		}
	}

	/**
	 * @param by
	 * @param displayed
	 */
	public void assertDisplayed(By by, Boolean displayed) {
		Boolean actual = isDisplayed(by);
		String message = "assert displayed of locator " + by.toString();
		if (displayed) {
			Assert.assertTrue(actual, message);
		} else {
			Assert.assertFalse(actual, message);
		}
	}

	/**
	 * @param by
	 * @param selected
	 */
	public void assertSelected(By by, Boolean selected) {
		Boolean actual = isSelected(by);
		String message = "assert being selected of locator " + by.toString();
		if (selected) {
			Assert.assertTrue(actual, message);
		} else {
			Assert.assertFalse(actual, message);
		}
	}

	/**
	 * @param by
	 * @param text
	 */
	public void assertText(By by, String text) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(by)));
		String message = "assert text of locator " + by.toString();
		Assert.assertEquals(element.getText(), text, message);
	}

	/**
	 * @param by
	 * @param attribute
	 * @return
	 */
	public String getAttribute(By by, String attribute) {
		WebElement element = findElement(by);
		return element.getAttribute(attribute);
	}

	/**
	 * @param by
	 * @param attribute
	 * @param value
	 */
	public void assertAttribute(By by, String attribute, String value) {
		String actual = getAttribute(by, attribute);
		String message = "assert attribute " + attribute + " of locator "
				+ by.toString();
		Assert.assertEquals(actual, value, message);
	}

	/**
	 * @param by
	 * @param value
	 */
	public void assertAriaDisabled(By by, String value) {
		assertAttribute(by, "aria-disabled", value);
	}

	/**
	 * @param by
	 * @param value
	 */
	public void assertAriaSelected(By by, String value) {
		assertAttribute(by, "aria-selected", value);
	}

	/**
	 * wait the specified locator to be visible
	 * 
	 * @param by
	 */
	public void waitVisible(By by) {
		wait.until(ExpectedConditions.visibilityOf(findElement(by)));
	}

	/**
	 * wait the specified locator to be invisible
	 * 
	 * @param by
	 */
	public void waitInvisible(By by) {
		wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
	}

	/**
	 * wait the specified locator to be visible
	 * 
	 * @param by
	 * @param timeout
	 *            in seconds
	 */
	public void waitVisible(By by, int timeout) {
		long t = System.currentTimeMillis();
		while (System.currentTimeMillis() - t < timeout * 100) {
			if (isPresent(by)) {
				return;
			}
		}
		logger.warn("Timed out after " + timeout
				+ " seconds waiting for element " + by.toString()
				+ " to be visible");
	}

	/**
	 * wait the specified locator to be invisible
	 * 
	 * @param by
	 * @param timeout
	 *            in seconds
	 */
	public void waitInvisible(By by, int timeout) {
		long t = System.currentTimeMillis();
		while (System.currentTimeMillis() - t < timeout * 100) {
			if (!isPresent(by)) {
				return;
			}
		}
		logger.warn("Timed out after " + timeout
				+ " seconds waiting for element " + by.toString()
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
		logger.debug("take screenshot to " + image.getPath());
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
			logger.error(testresult.getTestClass().getName() + "."
					+ testresult.getName() + " saveScreentshot failed "
					+ e.getMessage());
			e.printStackTrace();
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
		wait.until(ExpectedConditions.titleIs(title));
	}

	/**
	 * @param by
	 * @param attribute
	 * @return
	 */
	public String getCSSAttribute(By by, String attribute) {
		WebElement element = findElement(by);
		return element.getCssValue(attribute);
	}

	/**
	 * @param by
	 * @param attribute
	 * @param value
	 */
	public void assertCSSAttribute(By by, String attribute, String value) {
		String actual = getCSSAttribute(by, attribute);
		String message = "assert css attribute " + attribute + " of locator "
				+ by.toString();
		Assert.assertEquals(actual, value, message);
	}

	/**
	 * @param key
	 */
	public void typeKeyEvent(int key) {
		logger.debug("Try to type key event " + key);
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
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * get text on such web element
	 * 
	 * @param by
	 * @return string
	 */
	public String getText(By by) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(by)));
		return element.getText();
	}

	/**
	 * set text on such web element
	 * 
	 * @param by
	 * @param text
	 */
	public void setText(By by, String text) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		try {
			javascript
					.executeScript("arguments[0].innerText = '" + text + "';",
							findElement(by));
		} catch (WebDriverException e) {
			// e.printStackTrace();
		}

	}

	/**
	 * @param by
	 * @return
	 */
	public List<WebElement> getAllSelectedOptions(By by) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(by)));
		return new Select(element).getAllSelectedOptions();
	}

	/**
	 * get selected text on such web list
	 * 
	 * @param by
	 * @return string
	 */
	public String getSelectedText(By by) {
		return getAllSelectedOptions(by).get(0).getText();
	}

	/**
	 * @param by
	 * @return
	 */
	private WebElement findElement(By by) {
		waitDocumentReady();
		return wait.until(ExpectedConditions.presenceOfElementLocated(by));
	}

	/**
	 * @param by
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<WebElement> findElements(By by) {
		waitDocumentReady();
		return wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
	}

	/**
	 * trigger an event on such element
	 * 
	 * @param by
	 * @param event
	 *            String, such as "mouseover"
	 */
	public void triggerEvent(By by, String event) {
		JavascriptLibrary javascript = new JavascriptLibrary();
		try {
			javascript.callEmbeddedSelenium(driver, "triggerEvent",
					findElement(by), event);
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + by.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			// e2.printStackTrace();
		}
		waitDocumentReady();
	}

	/**
	 * fire an event on such element
	 * 
	 * @param by
	 * @param event
	 *            String, such as "onchange"
	 */
	public void fireEvent(By by, String event) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		try {
			javascript.executeScript("arguments[0].fireEvent(\"" + event
					+ "\")", findElement(by));
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + by.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			// e2.printStackTrace();
		}
		waitDocumentReady();
	}

	/**
	 * @param by
	 */
	public void scrollIntoView(By by) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		try {
			javascript.executeScript("arguments[0].scrollIntoView(true);",
					findElement(by));
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + by.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			// e2.printStackTrace();
		}
		waitDocumentReady();
	}

	/**
	 * @param by
	 *            frame locator
	 */
	public void switchToFrame(By by) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(findElement(by)));
		driver.switchTo().frame(element);
	}

	/**
	 * Switch to default content from a frame
	 */
	public void switchToDefault() {
		driver.switchTo().defaultContent();
	}

	/**
	 * using java script to set element attribute
	 * 
	 * @param by
	 * @param attribute
	 * @param value
	 */
	public void setAttribute(By by, String attribute, String value) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		try {
			javascript.executeScript("arguments[0].setAttribute('" + attribute
					+ "', arguments[1])", findElement(by), value);
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + by.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			// e.printStackTrace();
		}
	}

	/**
	 * using java script to remove element attribute
	 * 
	 * @param by
	 * @param attribute
	 */
	public void removeAttribute(By by, String attribute) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		try {
			javascript.executeScript("arguments[0].removeAttribute('"
					+ attribute + "')", findElement(by));
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + by.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			// e.printStackTrace();
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
		} catch (WebDriverException e) {
			logger.warn(
					"excpetion occurred while trying to wait document to be ready\n"
							+ e.getMessage(), e);
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
	 * @param by
	 */
	public void waitClickable(By by) {
		wait.until(ExpectedConditions.elementToBeClickable(findElement(by)));
	}

	/**
	 * using java script to get row number of cell element in web table
	 * 
	 * @param by
	 */
	public long getCellRow(By by) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		long ret = -1;
		try {
			ret = (long) javascript.executeScript(
					"return arguments[0].parentNode.rowIndex", findElement(by));
			ret++;// row index starts with zero
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + by.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			e2.printStackTrace();
		}
		return ret;
	}

	/**
	 * using java script to get column number of cell element in web table
	 * 
	 * @param by
	 */
	public long getCellColumn(By by) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		long ret = -1;
		try {
			ret = (long) javascript.executeScript(
					"return arguments[0].cellIndex", findElement(by));
			ret++;// column index starts with zero
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + by.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			e2.printStackTrace();
		}
		return ret;
	}

	/**
	 * using java script to get row number of row element in web table
	 * 
	 * @param by
	 */
	public long getRow(By by) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		long ret = -1;
		try {
			ret = (long) javascript.executeScript(
					"return arguments[0].rowIndex", findElement(by));
			ret++;// row index starts with zero
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + by.toString() + " was not found", e1);
		} catch (WebDriverException e2) {
			e2.printStackTrace();
		}
		return ret;
	}

	/**
	 * using java script to get row count of web table
	 * 
	 * @param by
	 */
	public long getRowCount(By by) {
		JavascriptExecutor javascript = (JavascriptExecutor) driver;
		long ret = -1;
		try {
			ret = (long) javascript.executeScript(
					"return arguments[0].rows.length", findElement(by));
		} catch (ElementNotFoundException e1) {
			logger.error("locator " + by.toString() + " was not found", e1);
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
		findAlert().dismiss();
	}

	/**
	 * accept the alert window
	 */
	public void acceptAlert() {
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