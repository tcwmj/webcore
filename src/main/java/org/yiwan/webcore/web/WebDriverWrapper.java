package org.yiwan.webcore.web;

import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.locator.Locator;
import org.yiwan.webcore.locator.LocatorBean;
import org.yiwan.webcore.test.ITestTemplate;
import org.yiwan.webcore.util.JaxbHelper;
import org.yiwan.webcore.util.PropHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WebDriverWrapper {
    public final static HashMap<String, String> testmap = new HashMap<String, String>();
    protected final static LocatorBean l = JaxbHelper.unmarshal(
            ClassLoader.getSystemResourceAsStream(PropHelper.LOCATORS_FILE),
            ClassLoader.getSystemResourceAsStream(PropHelper.LOCATOR_SCHEMA), LocatorBean.class);
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ITestTemplate testCase;
    private WebDriver driver;
    private JavascriptExecutor js;
    private Wait<WebDriver> wait;
    private String baseUrl = PropHelper.getProperty("server.url");

    public WebDriverWrapper(ITestTemplate testCase) {
        this.testCase = testCase;
        this.driver = testCase.getWebDriver();
        this.js = (JavascriptExecutor) driver;
        this.wait = new WebDriverWait(driver, PropHelper.TIMEOUT_INTERVAL, PropHelper.TIMEOUT_POLLING_INTERVAL)
                .ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class)
                .ignoring(UnreachableBrowserException.class);
    }

    protected ITestTemplate getTestCase() {
        return testCase;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * browse base url
     */
    public void browse() {
        browse(getBaseUrl());
    }

    /**
     * navigate to a specified url
     *
     * @param url
     */
    public void browse(String url) {
        logger.debug("try to navigate to url {}", url);
        testCase.getSubject().nodifyObserversStart();
        driver.navigate().to(url);
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
    }

    /**
     * navigate forward
     */
    public void forward() {
        logger.debug("try to navigate forward");
        testCase.getSubject().nodifyObserversStart();
        driver.navigate().forward();
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
    }

    /**
     * navigate back
     */
    public void back() {
        logger.debug("try to navigate back");
        testCase.getSubject().nodifyObserversStart();
        driver.navigate().back();
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
    }

    /**
     * maximize browser window
     */
    public void maximize() {
        logger.debug("try to maximize browser");
        driver.manage().window().maximize();
    }

    /**
     * close current browser tab
     */
    public void close() {
        logger.debug("try to close browser tab with title {}", getTitle());
        try {
            driver.close();
        } catch (WebDriverException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * close all browser tabs
     */
    public void closeAll() {
        logger.debug("try to close all browser tabs");
        for (String handle : driver.getWindowHandles()) {
            switchToWindow(handle);
            close();
        }
    }

    /**
     * quit driver
     */
    public void quit() {
        logger.debug("try to quit driver");
        try {
            driver.quit();
        } catch (WebDriverException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * delete all cookies
     */
    public void deleteAllCookies() {
        logger.debug("try to delete all cookies");
        driver.manage().deleteAllCookies();
    }

    /**
     * click web element if it's clickable, please use this click method as
     * default
     *
     * @param locator
     */
    protected void click(Locator locator) {
        logger.debug("try to click {}", locator.toString());
        testCase.getSubject().nodifyObserversStart();
        waitClickable(locator).click();
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
    }

    /**
     * click element without considering anything, it may raise unexpected
     * exception
     *
     * @param locator
     */
    protected void silentClick(Locator locator) {
        logger.debug("try to click {} silently", locator.toString());
        testCase.getSubject().nodifyObserversStart();
        driver.findElement(locator.by()).click();
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
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
     * <p>
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
        logger.debug("try to click {} by executing javascript", locator.toString());
        testCase.getSubject().nodifyObserversStart();
        js.executeScript("arguments[0].click()", findElement(locator));
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
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
            forceWait(PropHelper.TIMEOUT_NAVIGATION_INTERVAL);
            if (System.currentTimeMillis() - now > PropHelper.TIMEOUT_INTERVAL * 1000) {
                logger.warn("time out occurs on loop clicking {}", locator.toString());
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
        logger.debug("try to double click {}", locator.toString());
        testCase.getSubject().nodifyObserversStart();
        Actions action = new Actions(driver);
        action.doubleClick(waitClickable(locator)).build().perform();
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
    }

    /**
     * Type value into the web edit box if it's visible
     *
     * @param locator
     * @param value
     */
    protected void type(Locator locator, CharSequence... value) {
        logger.debug("try to type {} on {}", value, locator.toString());
        waitVisible(locator).sendKeys(value);
        waitDocumentReady();
    }

    /**
     * Type value into the web edit box if it's visible
     *
     * @param locator
     * @param value
     */
    protected void type(Locator locator, String value) {
        logger.debug("try to type {} on {}", value, locator.toString());
        waitVisible(locator).sendKeys(value);
        waitDocumentReady();
    }

    /**
     * Clear the content of the web edit box if it's visible
     *
     * @param locator
     */
    protected void clear(Locator locator) {
        logger.debug("try to clear value on " + locator.toString());
        waitVisible(locator).clear();
        waitDocumentReady();
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
     * input value in the first locator if it exists, or the second locator if
     * the first doesn't exist
     *
     * @param locator1
     * @param locator2
     * @param value
     */
    protected void smartInput(Locator locator1, Locator locator2, String value) {
        if (isDisplayed(locator1))
            input(locator1, value);
        else
            input(locator2, value);
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
     * @param value   true indicate tick on, false indicate tick off
     */
    protected void tick(Locator locator, boolean value) {
        logger.debug("try to tick {} on {}", value, locator.toString());
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
     * @param value   true indicate tick on, false indicate tick off
     */
    protected void alteredTick(Locator locator, Boolean value) {
        logger.debug("try tick {} on {} alternately", value, locator.toString());
        if (value)
            setAttribute(locator, "checked", "checked");
        else
            removeAttribute(locator, "checked");
    }

    /**
     * Select all options that display text matching the argument. That is, when
     * given "Bar" this would select an option like:
     * <p>
     * &lt;option value="foo"&gt;Bar&lt;/option&gt;
     *
     * @param locator
     * @param text    The visible text to match against
     */
    protected void selectByVisibleText(final Locator locator, final String text) {
        logger.debug("try to select {} on {}", text, locator.toString());
        testCase.getSubject().nodifyObserversStart();
        new Select(waitVisible(locator)).selectByVisibleText(text);
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
    }

    /**
     * Clear all selected entries. This is only valid when the SELECT supports
     * multiple selections.
     *
     * @param locator
     * @throws UnsupportedOperationException If the SELECT does not support multiple selections
     */
    protected void deselectAll(final Locator locator) {
        logger.debug("try to deselect all options on {}", locator.toString());
        new Select(waitVisible(locator)).deselectAll();
        waitDocumentReady();
    }

    /**
     * Select all options that display text matching the argument. That is, when
     * given "Bar" this would select an option like:
     * <p>
     * &lt;option value="foo"&gt;Bar&lt;/option&gt;
     *
     * @param locator
     * @param texts   The visible text to match against
     */
    protected void selectByVisibleText(final Locator locator, final List<String> texts) {
        for (String text : texts) {
            selectByVisibleText(locator, text);
        }
    }

    /**
     * Select the option at the given index. This is done locator examing the
     * "index" attribute of an element, and not merely locator counting.
     *
     * @param locator
     * @param index   The option at this index will be selected
     */
    protected void selectByIndex(final Locator locator, final int index) {
        logger.debug("try to select index {} on {}", index, locator.toString());
        testCase.getSubject().nodifyObserversStart();
        new Select(waitVisible(locator)).selectByIndex(index);
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
    }

    /**
     * Select all options that have a value matching the argument. That is, when
     * given "foo" this would select an option like:
     * <p>
     * &lt;option value="foo"&gt;Bar&lt;/option&gt;
     *
     * @param locator
     * @param value   The value to match against
     */
    protected void selectByValue(final Locator locator, final String value) {
        logger.debug("try to select value {} on {}", value, locator.toString());
        testCase.getSubject().nodifyObserversStart();
        new Select(waitVisible(locator)).selectByValue(value);
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
    }

    /**
     * @param locator
     * @param text
     */
    protected void waitTextSelected(Locator locator, String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator.by(), text));
    }

    /**
     * wait such text to be present in specified locator
     *
     * @param locator
     * @param text
     */
    protected void waitTextTyped(Locator locator, String text) {
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
        assertThat(isTextSelectable(locator, text)).as("assert %s to be selectable on %s", text, locator.toString()).isTrue();
    }

    /**
     * assert web list current value
     *
     * @param locator
     * @param text
     */
    protected void assertSelectedValue(Locator locator, String text) {
        List<WebElement> elements = new Select(findElement(locator)).getAllSelectedOptions();
        Boolean selected = false;
        for (WebElement element : elements) {
            if (element.getText().trim().equals(text)) {
                selected = true;
                break;
            }
        }
        assertThat(selected).as("assert %s to be selected on %s", text, locator.toString()).isTrue();
    }

    /**
     * @param locator
     */
    protected void moveTo(Locator locator) {
        logger.debug("move mouse to {}", locator.toString());
        Actions action = new Actions(driver);
        action.moveToElement(waitVisible(locator)).build().perform();
        waitDocumentReady();
    }

    /**
     * whether locator is present or not
     *
     * @param locator
     * @return whether locator is present or not
     */
    protected boolean isPresent(Locator locator) {
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
        String message = "assert %s is enabled";
        if (enabled) {
            assertThat(actual).as(message, locator.toString()).isTrue();
        } else {
            assertThat(actual).as(message, locator.toString()).isFalse();
        }
    }

    /**
     * @param locator
     * @param displayed
     */
    protected void assertDisplayed(Locator locator, Boolean displayed) {
        Boolean actual = isDisplayed(locator);
        String message = "assert %s is displayed";
        if (displayed) {
            assertThat(actual).as(message, locator.toString()).isTrue();
        } else {
            assertThat(actual).as(message, locator.toString()).isFalse();
        }
    }

    /**
     * @param locator
     * @param selected
     */
    protected void assertSelected(Locator locator, Boolean selected) {
        Boolean actual = isSelected(locator);
        String message = "assert %s is selected";
        if (selected) {
            assertThat(actual).as(message, locator.toString()).isTrue();
        } else {
            assertThat(actual).as(message, locator.toString()).isFalse();
        }
    }

    /**
     * assert text on locator
     *
     * @param locator
     * @param text
     */
    protected void assertText(Locator locator, String text) {
        assertThat(findElement(locator).getText()).as("assert text displayed on %s", locator.toString()).isEqualTo(text);
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
        assertThat(actual).as("assert attribute %s of %s", attribute, locator.toString()).isEqualTo(value);
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
        return wait.until(ExpectedConditions.visibilityOf(findElement(locator)));
    }

    /**
     * wait the specified locator to be invisible
     *
     * @param locator
     */
    protected void waitInvisible(Locator locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator.by()));
    }

    /**
     * wait the specified locator to be present
     *
     * @param locator
     * @param timeout in seconds
     */
    protected void waitPresent(Locator locator, int timeout) {
        long t = System.currentTimeMillis();
        while (System.currentTimeMillis() - t < timeout * 100) {
            if (isPresent(locator)) {
                return;
            }
        }
        logger.warn("wait presence of {} timed out in {} seconds", timeout, locator.toString());
    }

    /**
     * wait the specified locator to be absent
     *
     * @param locator
     * @param timeout in seconds
     */
    protected void waitAbsent(Locator locator, int timeout) {
        long t = System.currentTimeMillis();
        while (System.currentTimeMillis() - t < timeout * 100) {
            if (!isPresent(locator)) {
                return;
            }
        }
        logger.warn("wait absence of {} timed out in {} seconds", timeout, locator.toString());
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
        assertThat(actual).as("assert css attribute %s of %s", attribute, locator.toString()).isEqualTo(value);
    }

    /**
     * @param key
     */
    protected void typeKeyEvent(int key) {
        logger.debug("type key event " + key);
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
     * @param millis Milliseconds
     */
    protected void forceWait(int millis) {
        logger.debug("force to wait in " + millis + " milliseconds");
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
     * get all text on found locators
     *
     * @param locator
     * @return text list
     */
    protected List<String> getTexts(Locator locator) {
        List<String> Texts = new ArrayList<String>();
        for (WebElement element : findElements(locator)) {
            Texts.add(element.getText());
        }
        return Texts;
    }

    /**
     * set innert text on such web element
     *
     * @param locator
     * @param text
     */
    protected void setText(Locator locator, String text) {
        logger.debug("try to set innertext of {} to {}", locator.toString(), text);
        js.executeScript("arguments[0].innerText=arguments[1]", findElement(locator), text);
        waitDocumentReady();
    }

    /**
     * set value on such web element, an alternative approach for method input
     *
     * @param locator
     * @param value
     */
    protected void setValue(Locator locator, String value) {
        logger.debug("try to set text of {} to {}", locator.toString(), value);
        js.executeScript("arguments[0].value=arguments[1]", findElement(locator), value);
        waitDocumentReady();
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
     * get all select options
     *
     * @param locator
     * @return
     */
    protected List<WebElement> getOptions(Locator locator) {
        return new Select(waitVisible(locator)).getOptions();
    }

    /**
     * get all select options' text
     *
     * @param locator
     * @return
     */
    protected List<String> getOptionTexts(Locator locator) {
        List<String> list = new ArrayList<String>();
        List<WebElement> options = getOptions(locator);
        for (WebElement option : options) {
            list.add(option.getText());
        }
        return list;
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
     * get selected text on such web list
     *
     * @param locator
     * @return string
     */
    protected List<String> getSelectedTexts(Locator locator) {
        List<String> list = new ArrayList<String>();
        List<WebElement> options = getAllSelectedOptions(locator);
        for (WebElement option : options) {
            list.add(option.getText());
        }
        return list;
    }

    /**
     * find element in presence on the page
     *
     * @param locator
     * @return WebElement
     */
    private WebElement findElement(Locator locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator.by()));
    }

    /**
     * find all elements in presence on the page
     *
     * @param locator
     * @return List&gt;WebElement&lt;
     */
    private List<WebElement> findElements(Locator locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator.by()));
    }

    /**
     * wait until page is loaded completely
     */
    private void waitDocumentReady() {
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
                    ready = true;
                    logger.warn("javascript error while waiting document to be ready");
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
     * @param event   String, such as "mouseover"
     */
    protected void triggerEvent(Locator locator, String event) {
        logger.debug("try to trigger {} on {}", event, locator.toString());
        testCase.getSubject().nodifyObserversStart();
        JavascriptLibrary javascript = new JavascriptLibrary();
        javascript.callEmbeddedSelenium(driver, "triggerEvent", findElement(locator), event);
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
    }

    /**
     * fire an event on such element
     *
     * @param locator
     * @param event   String, such as "onchange"
     */
    protected void fireEvent(Locator locator, String event) {
        logger.debug("try to fire {} on {}", event, locator.toString());
        testCase.getSubject().nodifyObserversStart();
        js.executeScript("arguments[0].fireEvent(arguments[1]);", findElement(locator), event);
        waitDocumentReady();
        testCase.getSubject().nodifyObserversStop();
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
        waitDocumentReady();
    }

    /**
     * immediately showing the user the result of some action without requiring
     * the user to manually scroll through the document to find the result
     *
     * @param locator
     * @param bAlignToTop true Default. Scrolls the object so that top of the object is
     *                    visible at the top of the window. <br/>
     *                    false Scrolls the object so that the bottom of the object is
     *                    visible at the bottom of the window.
     */
    protected void scrollIntoView(Locator locator, Boolean bAlignToTop) {
        logger.debug("try to scroll into view on {}, align to top is {}", locator.toString(), bAlignToTop);
        js.executeScript("arguments[0].scrollIntoView(arguments[1])", findElement(locator), bAlignToTop.toString());
        waitDocumentReady();
    }

    /**
     * Scroll page or scrollable element to a specific target element.
     *
     * @param locator
     */
    protected void scrollTo(Locator locator) {
        logger.debug("try to scroll to {}", locator.toString());
        WebElement element = findElement(locator);
        js.executeScript("window.scrollTo(arguments[0],arguments[1])", element.getLocation().x, element.getLocation().y);
        waitDocumentReady();
    }

    /**
     * switch to a window with a specified name or handle
     *
     * @param nameOrHandle
     */
    protected void switchToWindow(String nameOrHandle) {
        logger.debug("try to switch to window {}", nameOrHandle);
        driver.switchTo().window(nameOrHandle);
    }

    /**
     * @param locator frame locator
     */
    protected void switchToFrame(Locator locator) {
        logger.debug("try to switch to {}", locator.toString());
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator.by()));
    }

    /**
     * Switch to default content from a frame
     */
    protected void switchToDefault() {
        logger.debug("try to switch to default content");
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
        logger.debug("try to set attribute {} on {} to {}", attribute, locator.toString(), value);
        js.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", findElement(locator), attribute, value);
        waitDocumentReady();
    }

    /**
     * using java script to remove element attribute
     *
     * @param locator
     * @param attribute
     */
    protected void removeAttribute(Locator locator, String attribute) {
        logger.debug("try to remove attribute {} on {}", attribute, locator.toString());
        js.executeScript("arguments[0].removeAttribute(arguments[1])", findElement(locator), attribute);
        waitDocumentReady();
    }

    /**
     * generate page source file for HTML static analysis
     */
    // protected void generatePageSource() {
    // String currentUrl = driver.getCurrentUrl();
    // String fileName = currentUrl.replaceFirst("http://.*:\\d+/",
    // "").replaceFirst("\\?.*", "");
    // if (!testcase.getCurrentUrl().equals(currentUrl)) {
    // String pageSource = driver.getPageSource();
    // File file = new File("target/" + fileName + ".html");
    // file.getParentFile().mkdirs();
    // int i = 0;
    // while (file.exists() && i < 100) {
    // file = new File("target/" + fileName + "_" + i + ".html");
    // i++;
    // }
    // if (i < 100) {
    // try {
    // FileWriter fw = new FileWriter(file);
    // BufferedWriter bw = new BufferedWriter(fw);
    // bw.write(pageSource);
    // bw.close();
    // fw.close();
    // } catch (IOException e) {
    // logger.warn(e.getMessage(), e);
    // }
    // } else {
    // logger.warn("skipped generatePageSource due to counts of same page " +
    // currentUrl + " exceeds 100.");
    // }
    // testcase.setCurrentUrl(currentUrl);
    // }
    // }

    /**
     * wait the specified locator to be visible and enable
     *
     * @param locator web element locator
     * @return web element
     */
    protected WebElement waitClickable(Locator locator) {
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
    protected boolean isPageContains(String text) {
        return driver.getPageSource().contains(text);
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
        logger.debug("try to dismiss alert {}", getAlertText());
        findAlert().dismiss();
    }

    /**
     * accept the alert window
     */
    protected void acceptAlert() {
        logger.debug("try accept alert {}", getAlertText());
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
        assertThat(getAlertText()).as("assert alert text").isEqualTo(text);
    }

    /**
     * get page source of current page
     *
     * @return page source string
     */
    protected String getPageSource() {
        return driver.getPageSource();
    }

    /**
     * get current url address
     *
     * @return string value of current url
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * get current page title
     *
     * @return string value of title
     */
    protected String getTitle() {
        return driver.getTitle();
    }

    /**
     * assert text containing in the page source
     *
     * @param text
     * @param contain
     */
    protected void assertPageContains(String text, boolean contain) {
        assertThat(isPageContains(text)).as("assert page contains text %s", text).isEqualTo(contain);
    }
}
