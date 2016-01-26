package org.yiwan.webcore.web;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.yiwan.webcore.locator.Locator;
import org.yiwan.webcore.locator.LocatorBean;
import org.yiwan.webcore.util.JaxbHelper;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.util.TestBase;

import com.thoughtworks.selenium.webdriven.JavascriptLibrary;

public class WebDriverWrapper {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final static LocatorBean l = JaxbHelper.unmarshal(
			ClassLoader.getSystemResourceAsStream(PropHelper.LOCATORS_FILE),
			ClassLoader.getSystemResourceAsStream(PropHelper.LOCATOR_SCHEMA), LocatorBean.class);

	protected TestBase testcase;

	private WebDriver driver;
	private JavascriptExecutor js;
	private Wait<WebDriver> wait;

	public WebDriverWrapper(TestBase testcase) {
		this.testcase = testcase;
		this.driver = testcase.getDriver();
		this.js = testcase.getJavascriptExecutor();
		this.wait = testcase.getWebDriverWait();
	}

	/**
	 * navigate to a specified url
	 * 
	 * @param url
	 */
	protected void browse(String url) {
		logger.info("navigate to url " + url);
		waitDocumentReady();
		driver.navigate().to(url);
	}

	/**
	 * navigate forward
	 * 
	 */
	protected void forward() {
		logger.info("navigate forward");
		waitDocumentReady();
		driver.navigate().forward();
	}

	/**
	 * navigate back
	 * 
	 */
	protected void back() {
		logger.info("navigate back");
		waitDocumentReady();
		driver.navigate().back();
	}

	/**
	 * maximize browser window
	 */
	protected void maximize() {
		logger.info("maximizing browser");
		driver.manage().window().maximize();
	}

	/**
	 * close current browser tab
	 */
	protected void close() {
		logger.info("close browser tab with title " + getTitle());
		try {
			driver.close();
		} catch (WebDriverException e) {
			logger.warn("close browser tab", e);
		}
	}

	/**
	 * close all browser tabs
	 */
	protected void closeAll() {
		logger.info("close all browser tabs");
		if (driver instanceof WebDriver) {
			for (String handle : driver.getWindowHandles()) {
				switchToWindow(handle);
				close();
			}
		}
	}

	/**
	 * quit driver
	 */
	protected void quit() {
		logger.info("quit driver");
		try {
			driver.quit();
		} catch (WebDriverException e) {
			logger.warn("quit driver", e);
		}
	}

	/**
	 * click web element if it's clickable, please use this click method as
	 * default
	 * 
	 * @param locator
	 */
	protected void click(Locator locator) {
		logger.info("click " + locator.toString());
		waitClickable(locator).click();
	}

	/**
	 * click element without considering anything, it may raise unexpected
	 * exception
	 * 
	 * @param locator
	 */
	protected void silentClick(Locator locator) {
		logger.info("silent click " + locator.toString());
		waitDocumentReady();
		driver.findElement(locator.by()).click();
	}

	/**
	 * forced to click element even if it's not clickable, it may raise
	 * unexpected exception, please use method click as default
	 * 
	 * @param locator
	 */
	protected void forcedClick(Locator locator) {
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
	protected void smartClick(Locator locator) {
		if (isDisplayed(locator))
			click(locator);
	}

	/**
	 * click element if it's displayed, otherwise click the next one
	 * 
	 * /**
	 * 
	 * @param locators
	 */
	protected void smartClick(Locator... locators) {
		for (Locator locator : locators) {
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
	protected void jsClick(Locator locator) {
		logger.info("by javascript click " + locator.toString());
		js.executeScript("arguments[0].click();", findElement(locator));
	}

	/**
	 * click the first element in a loop while it's displayed
	 * 
	 * @param locator
	 */
	protected void loopClick(Locator locator) {
		long now = System.currentTimeMillis();
		while (isDisplayed(locator)) {
			click(locator);
			if (System.currentTimeMillis() - now > PropHelper.TIMEOUT_INTERVAL * 1000) {
				logger.warn("Time out on loop clicking " + locator.toString());
				return;
			}
		}
	}

	/**
	 * Double click web element if it's clickable
	 * 
	 * @param locator
	 */
	protected void doubleClick(Locator locator) {
		logger.info("double click " + locator.toString());
		Actions action = new Actions(driver);
		action.doubleClick(waitClickable(locator)).build().perform();
	}

	/**
	 * Type value into the web edit box if it's visible
	 * 
	 * @param locator
	 * @param value
	 */
	protected void type(Locator locator, CharSequence... value) {
		logger.info("type " + value + " on " + locator.toString());
		waitClickable(locator).sendKeys(value);
	}

	/**
	 * Type value into the web edit box if it's visible
	 * 
	 * @param locator
	 * @param value
	 */
	protected void type(Locator locator, String value) {
		logger.info("type " + value + " on " + locator.toString());
		waitClickable(locator).sendKeys(value);
	}

	/**
	 * Clear the content of the web edit box if it's visible
	 * 
	 * @param locator
	 */
	protected void clear(Locator locator) {
		logger.info("clear " + locator.toString());
		waitClickable(locator).clear();
	}

	/**
	 * clear the web edit box and input the value
	 * 
	 * @param locator
	 * @param value
	 */
	protected void input(Locator locator, String value) {
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
	protected void ajaxInput(Locator locator, String value, Locator ajaxLocator) {
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
	protected void tick(Locator locator, boolean value) {
		logger.info("tick " + value + " on " + locator.toString());
		if (isTicked(locator) != value)
			click(locator);
	}

	/**
	 * web check box ticked or not
	 * 
	 * @param locator
	 * @return ticked or not
	 */
	protected boolean isTicked(Locator locator) {
		String checked = getAttribute(locator, "checked");
		if (checked == null || !checked.toLowerCase().equals("true"))
			return false;
		else
			return true;
	}

	/**
	 * using java script to tick web check box
	 * 
	 * @param locator
	 * @param value
	 *            true indicate tick on, false indicate tick off
	 */
	protected void alteredTick(Locator locator, Boolean value) {
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
	protected void selectByVisibleText(final Locator locator, final String text) {
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
	protected void deselectAll(final Locator locator) {
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
	protected void selectByVisibleText(final Locator locator, final List<String> texts) {
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
	protected void selectByIndex(final Locator locator, final int index) {
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
	protected void selectByValue(final Locator locator, final String value) {
		logger.info("select value " + value + " on " + locator.toString());
		new Select(waitVisible(locator)).selectByValue(value);
	}

	/**
	 * @param locator
	 * @param text
	 */
	protected void waitTextSelected(Locator locator, String text) {
		logger.debug("wait " + text + " to be selected on " + locator.toString());
		waitDocumentReady();
		wait.until(ExpectedConditions.textToBePresentInElementLocated(locator.by(), text));
	}

	/**
	 * wait such text to be present in specified locator
	 * 
	 * @param locator
	 * @param text
	 */
	protected void waitTextTyped(Locator locator, String text) {
		logger.debug("wait " + text + " to be typed on " + locator.toString());
		waitDocumentReady();
		wait.until(ExpectedConditions.textToBePresentInElementLocated(locator.by(), text));
	}

	/**
	 * @param locator
	 * @param text
	 * @return whether text is selectable or not
	 */
	protected boolean isTextSelectable(Locator locator, String text) {
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
	protected void assertTextSelectable(Locator locator, String text) {
		String message = "assert " + text + " to be selectable on " + locator.toString();
		Assert.assertTrue(isTextSelectable(locator, text), message);
	}

	/**
	 * assert web list current value
	 * 
	 * @param locator
	 * @param text
	 */
	protected void assertSelectedValue(Locator locator, String text) {
		String message = "assert " + text + " to be selected on " + locator.toString();
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
	protected void moveTo(Locator locator) {
		logger.info("move mouse to " + locator.toString());
		Actions action = new Actions(driver);
		action.moveToElement(waitVisible(locator)).build().perform();
	}

	/**
	 * whether locator is present or not
	 * 
	 * @param locator
	 * @return whether locator is present or not
	 */
	protected boolean isPresent(Locator locator) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			driver.findElement(locator.by());
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
	protected boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
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
	protected boolean isEnabled(Locator locator) {
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
	protected boolean isDisplayed(Locator locator) {
		waitDocumentReady();
		Boolean ret = false;
		try {
			ret = driver.findElement(locator.by()).isDisplayed();
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
	protected boolean isSelected(Locator locator) {
		Boolean ret = false;
		ret = findElement(locator).isSelected();
		return ret;
	}

	/**
	 * @param locator
	 * @param enabled
	 */
	protected void assertEnabled(Locator locator, Boolean enabled) {
		Boolean actual = isEnabled(locator);
		String message = "assert enabled of " + locator.toString();
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
	protected void assertDisplayed(Locator locator, Boolean displayed) {
		Boolean actual = isDisplayed(locator);
		String message = "assert displayed of " + locator.toString();
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
	protected void assertSelected(Locator locator, Boolean selected) {
		Boolean actual = isSelected(locator);
		String message = "assert selected of " + locator.toString();
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
	protected void assertText(Locator locator, String text) {
		String message = "assert text is " + text + " on " + locator.toString();
		Assert.assertEquals(findElement(locator).getText(), text, message);
	}

	/**
	 * get value of specified attribute
	 * 
	 * @param locator
	 * @param attribute
	 * @return attribute value
	 */
	protected String getAttribute(Locator locator, String attribute) {
		return findElement(locator).getAttribute(attribute);
	}

	/**
	 * @param locator
	 * @param attribute
	 * @param value
	 */
	protected void assertAttribute(Locator locator, String attribute, String value) {
		String actual = getAttribute(locator, attribute);
		String message = "assert " + attribute + " is " + value + " on " + locator.toString();
		Assert.assertEquals(actual, value, message);
	}

	/**
	 * assert value of aria-disabled attribute
	 * 
	 * @param locator
	 * @param value
	 */
	protected void assertAriaDisabled(Locator locator, String value) {
		assertAttribute(locator, "aria-disabled", value);
	}

	/**
	 * assert value of aria-selected attribute
	 * 
	 * @param locator
	 * @param value
	 */
	protected void assertAriaSelected(Locator locator, String value) {
		assertAttribute(locator, "aria-selected", value);
	}

	/**
	 * wait the specified locator to be visible
	 * 
	 * @param locator
	 * @return WebElement
	 */
	protected WebElement waitVisible(Locator locator) {
		logger.debug("wait visibility of " + locator.toString());
		waitDocumentReady();
		return wait.until(ExpectedConditions.visibilityOf(findElement(locator)));
	}

	/**
	 * wait the specified locator to be invisible
	 * 
	 * @param locator
	 */
	protected void waitInvisible(Locator locator) {
		logger.debug("wait invisibility of " + locator.toString());
		waitDocumentReady();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(locator.by()));
	}

	/**
	 * wait the specified locator to be present
	 * 
	 * @param locator
	 * @param timeout
	 *            in seconds
	 */
	protected void waitPresent(Locator locator, int timeout) {
		logger.debug("in " + timeout + " seconds. wait present of " + locator.toString());
		waitDocumentReady();
		long t = System.currentTimeMillis();
		while (System.currentTimeMillis() - t < timeout * 100) {
			if (isPresent(locator)) {
				return;
			}
		}
		logger.warn("Timed out");
	}

	/**
	 * wait the specified locator to be absent
	 * 
	 * @param locator
	 * @param timeout
	 *            in seconds
	 */
	protected void waitAbsent(Locator locator, int timeout) {
		logger.debug("in " + timeout + " seconds. wait absent of " + locator.toString());
		waitDocumentReady();
		long t = System.currentTimeMillis();
		while (System.currentTimeMillis() - t < timeout * 100) {
			if (!isPresent(locator)) {
				return;
			}
		}
		logger.warn("Timed out");
	}

	/**
	 * assert page's title to be specified value
	 * 
	 * @param title
	 */
	protected void assertTitle(String title) {
		waitTitle(title);
	}

	/**
	 * wait page's title to be a specified value
	 * 
	 * @param title
	 */
	protected void waitTitle(String title) {
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
	protected String getCssValue(Locator locator, String attribute) {
		return findElement(locator).getCssValue(attribute);
	}

	/**
	 * assert css attribute value
	 * 
	 * @param locator
	 * @param attribute
	 * @param value
	 */
	protected void assertCssValue(Locator locator, String attribute, String value) {
		String actual = getCssValue(locator, attribute);
		String message = "assert css " + attribute + " is " + value + " on " + locator.toString();
		Assert.assertEquals(actual, value, message);
	}

	/**
	 * @param key
	 */
	protected void typeKeyEvent(int key) {
		logger.info("type key event " + key);
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(key);
		} catch (AWTException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * force to wait specified seconds
	 * 
	 * @param millis
	 *            Milliseconds
	 */
	protected void forceWait(int millis) {
		logger.info("force to wait in " + millis + " milliseconds");
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * get text on such web element
	 * 
	 * @param locator
	 * @return string
	 */
	protected String getText(Locator locator) {
		return waitVisible(locator).getText();
	}

	/**
	 * set innert text on such web element
	 * 
	 * @param locator
	 * @param text
	 */
	protected void setText(Locator locator, String text) {
		logger.info("set innertext to " + text + " on " + locator.toString());
		js.executeScript("arguments[0].innerText = '" + text + "';", findElement(locator));
	}

	/**
	 * set value on such web element, an alternative approach for method input
	 * 
	 * @param locator
	 * @param value
	 */
	protected void setValue(Locator locator, String value) {
		logger.info("set value " + value + " on " + locator.toString());
		js.executeScript("arguments[0].value = '" + value + "';", findElement(locator));
	}

	/**
	 * get all selected options as a web element list
	 * 
	 * @param locator
	 * @return List&gt;WebElement&lt;
	 */
	protected List<WebElement> getAllSelectedOptions(Locator locator) {
		return new Select(waitVisible(locator)).getAllSelectedOptions();
	}

	/**
	 * get selected text on such web list
	 * 
	 * @param locator
	 * @return string
	 */
	protected String getSelectedText(Locator locator) {
		return getAllSelectedOptions(locator).get(0).getText();
	}

	/**
	 * find element in presence on the page
	 * 
	 * @param locator
	 * @return WebElement
	 */
	private WebElement findElement(Locator locator) {
		waitDocumentReady();
		return wait.until(ExpectedConditions.presenceOfElementLocated(locator.by()));
	}

	/**
	 * find all elements in presence on the page
	 * 
	 * @param locator
	 * @return List&gt;WebElement&lt;
	 */
	@SuppressWarnings("unused")
	private List<WebElement> findElements(Locator locator) {
		waitDocumentReady();
		return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator.by()));
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
	 * trigger an event on such element
	 * 
	 * @param locator
	 * @param event
	 *            String, such as "mouseover"
	 */
	protected void triggerEvent(Locator locator, String event) {
		logger.info("trigger " + event + " on " + locator.toString());
		JavascriptLibrary javascript = new JavascriptLibrary();
		javascript.callEmbeddedSelenium(driver, "triggerEvent", findElement(locator), event);
	}

	/**
	 * fire an event on such element
	 * 
	 * @param locator
	 * @param event
	 *            String, such as "onchange"
	 */
	protected void fireEvent(Locator locator, String event) {
		logger.info("fire " + event + " on " + locator.toString());
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
	protected void scrollIntoView(Locator locator) {
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
	protected void scrollIntoView(Locator locator, Boolean bAlignToTop) {
		logger.info("align to top is " + bAlignToTop + ", scroll into view on " + locator.toString());
		js.executeScript("arguments[0].scrollIntoView(" + bAlignToTop.toString() + ");", findElement(locator));
	}

	/**
	 * Scroll page or scrollable element to a specific target element.
	 * 
	 * @param locator
	 */
	protected void scrollTo(Locator locator) {
		logger.info("scroll to " + locator.toString());
		WebElement element = findElement(locator);
		js.executeScript("window.scrollTo(" + element.getLocation().x + "," + element.getLocation().y + ")");
	}

	/**
	 * switch to a window with a specified name or handle
	 * 
	 * @param nameOrHandle
	 */
	protected void switchToWindow(String nameOrHandle) {
		logger.info("switch to window with name or handle " + nameOrHandle);
		driver.switchTo().window(nameOrHandle);
	}

	/**
	 * @param locator
	 *            frame locator
	 */
	protected void switchToFrame(Locator locator) {
		logger.info("switch to " + locator.toString());
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator.by()));
	}

	/**
	 * Switch to default content from a frame
	 */
	protected void switchToDefault() {
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
	protected void setAttribute(Locator locator, String attribute, String value) {
		logger.info("set attribute " + attribute + " to " + value + " on " + locator.toString());
		js.executeScript("arguments[0].setAttribute('" + attribute + "', arguments[1])", findElement(locator), value);
	}

	/**
	 * using java script to remove element attribute
	 * 
	 * @param locator
	 * @param attribute
	 */
	protected void removeAttribute(Locator locator, String attribute) {
		logger.info("remove attribute " + attribute + " on " + locator.toString());
		js.executeScript("arguments[0].removeAttribute('" + attribute + "')", findElement(locator));
	}

	/**
	 * generate page source file for HTML static analysis
	 */
	protected void generatePageSource() {
		String currentUrl = driver.getCurrentUrl();
		String fileName = currentUrl.replaceFirst("http://.*:\\d+/", "").replaceFirst("\\?.*", "");
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
	protected WebElement waitClickable(Locator locator) {
		logger.debug("wait clickable of " + locator.toString());
		waitDocumentReady();
		return wait.until(ExpectedConditions.elementToBeClickable(locator.by()));
	}

	/**
	 * using java script to get row number of cell element in web table
	 * 
	 * @param locator
	 */
	protected long getCellRow(Locator locator) {
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
	protected long getCellColumn(Locator locator) {
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
	protected long getRow(Locator locator) {
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
	protected long getRowCount(Locator locator) {
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
	protected boolean isContain(String text) {
		waitDocumentReady();
		return driver.getPageSource().contains(text);
	}

	/**
	 * assert page source contains such text
	 * 
	 * @param text
	 * @param displayed
	 */
	protected void assertTextDisplayed(String text, boolean displayed) {
		Assert.assertEquals(isContain(text), displayed);
	}

	/**
	 * find alert
	 * 
	 * @return Alert
	 */
	protected Alert findAlert() {
		return wait.until(ExpectedConditions.alertIsPresent());// driver.switchTo().alert();
	}

	/**
	 * dismiss the alert window
	 */
	protected void dismissAlert() {
		logger.info("dismiss alert " + getAlertText());
		findAlert().dismiss();
	}

	/**
	 * accept the alert window
	 */
	protected void acceptAlert() {
		logger.info("accept alert " + getAlertText());
		findAlert().accept();
	}

	/**
	 * get text from alert window
	 * 
	 * @return alert text string
	 */
	protected String getAlertText() {
		return findAlert().getText();
	}

	/**
	 * assert text on alert window
	 * 
	 * @param text
	 */
	protected void assertAlertText(String text) {
		Assert.assertEquals(getAlertText(), text);
	}

	/**
	 * get page source of current page
	 * 
	 * @return page source string
	 */
	protected String getPageSource() {
		waitDocumentReady();
		return driver.getPageSource();
	}

	/**
	 * get current url address
	 * 
	 * @return string value of current url
	 */
	protected String getCurrentUrl() {
		waitDocumentReady();
		return driver.getCurrentUrl();
	}

	/**
	 * get current page title
	 * 
	 * @return string value of title
	 */
	protected String getTitle() {
		waitDocumentReady();
		return driver.getTitle();
	}
}
