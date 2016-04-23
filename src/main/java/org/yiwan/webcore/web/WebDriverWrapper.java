package org.yiwan.webcore.web;

import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractListAssert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.locator.Locator;
import org.yiwan.webcore.util.PropHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WebDriverWrapper implements IWebDriverWrapper {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private WebDriver driver;
    private JavascriptExecutor js;
    private Wait<org.openqa.selenium.WebDriver> wait;

    public WebDriverWrapper(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.wait = new WebDriverWait(driver, PropHelper.TIMEOUT_INTERVAL, PropHelper.TIMEOUT_POLLING_INTERVAL).ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class).ignoring(UnreachableBrowserException.class);
    }

    @Override
    public IBrowseNavigation navigate() {
        return new IBrowseNavigation() {
            @Override
            public IBrowseNavigation to(String url) {
                logger.debug("try to navigate to url {}", url);
                driver.navigate().to(url);
                waitThat().documentComplete();
                return this;
            }

            @Override
            public IBrowseNavigation forward() {
                logger.debug("try to navigate forward");
                driver.navigate().forward();
                waitThat().documentComplete();
                return this;
            }

            @Override
            public IBrowseNavigation backward() {
                logger.debug("try to navigate back");
                driver.navigate().back();
                waitThat().documentComplete();
                return this;
            }
        };
    }

    /**
     * maximize browser window
     */
    @Override
    public IWebDriverWrapper maximize() {
        logger.debug("try to maximize browser");
        driver.manage().window().maximize();
        return this;
    }

    /**
     * close current browser tab
     */
    @Override
    public IWebDriverWrapper close() {
        logger.debug("try to close browser tab with title {}", getPageTitle());
        driver.close();
        return this;
    }

    /**
     * close all browser tabs
     */
    @Override
    public IWebDriverWrapper closeAll() {
        logger.debug("try to close all browser tabs");
        for (String handle : driver.getWindowHandles()) {
            switchToWindow(handle);
            close();
        }
        return this;
    }

    /**
     * quit driver
     */
    @Override
    public IWebDriverWrapper quit() {
        logger.debug("try to quit driver");
        driver.quit();
        return this;
    }

    /**
     * delete all cookies
     */
    @Override
    public IWebDriverWrapper deleteAllCookies() {
        logger.debug("try to delete all cookies");
        driver.manage().deleteAllCookies();
        return this;
    }

    /**
     * is page source contains such text
     *
     * @param text
     * @return boolean
     */
    @Override
    public boolean isPageSourceContains(String text) {
        return driver.getPageSource().contains(text);
    }

    /**
     * get page source of current page
     *
     * @return page source string
     */
    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    /**
     * get current url address
     *
     * @return string value of current url
     */
    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * switch to a window with a specified name or handle
     *
     * @param nameOrHandle
     */
    @Override
    public IWebDriverWrapper switchToWindow(String nameOrHandle) {
        logger.debug("try to switch to window {}", nameOrHandle);
        driver.switchTo().window(nameOrHandle);
        return this;
    }

    /**
     * Switch to default content from a frame
     */
    @Override
    public IWebDriverWrapper switchToDefaultWindow() {
        logger.debug("try to switch to default content");
        driver.switchTo().defaultContent();
        return this;
    }

    @Override
    public IWebDriverWrapper switchToFrame(int index) {
        logger.debug("try to switch to frame {}", index);
        driver.switchTo().frame(index);
        return this;
    }

    @Override
    public IWebDriverWrapper switchToFrame(String nameOrId) {
        logger.debug("try to switch to frame {}", nameOrId);
        driver.switchTo().frame(nameOrId);
        return this;
    }

    /**
     * get current page title
     *
     * @return string value of title
     */
    @Override
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * click element if it's displayed, otherwise click the next one
     */
    @Override
    public IWebDriverWrapper smartClick(Locator... locators) {
        for (Locator locator : locators) {
            if (element(locator).smartClick()) {
                break;
            }
        }
        return this;
    }

    /**
     * input value in the first locator if it exists, otherwise input the next one
     *
     * @param value
     * @param locators
     */
    @Override
    public IWebDriverWrapper smartInput(String value, Locator... locators) {
        for (Locator locator : locators) {
            if (element(locator).smartInput(value)) {
                break;
            }
        }
        return this;
    }

    /**
     * capture screenshot for local or remote testing
     *
     * @return screenshot TakesScreenshot
     */
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) {
        TakesScreenshot takesScreenshot = null;
        if (PropHelper.REMOTE) {
            // RemoteWebDriver does not implement the TakesScreenshot class if
            // the driver does have the Capabilities to take a screenshot then
            // Augmenter will add the TakesScreenshot methods to the instance
            takesScreenshot = (TakesScreenshot) (new Augmenter().augment(driver));
        } else {
            takesScreenshot = (TakesScreenshot) driver;
        }
        return takesScreenshot.getScreenshotAs(target);
    }

    /**
     * @param key
     */
    @Override
    public IWebDriverWrapper typeKeyEvent(int key) throws AWTException {
        logger.debug("type key event " + key);
        Robot robot;
        robot = new Robot();
        robot.keyPress(key);
        return this;
    }

    @Override
    public IActionsWrapper actions() {
        return new IActionsWrapper() {
            private Actions actions = new Actions(driver);

            @Override
            public IActionsWrapper click() {
                actions.click();
                return this;
            }

            @Override
            public IActionsWrapper click(Locator locator) {
                actions.click(waitThat(locator).toBeClickable());
                return this;
            }

            @Override
            public IActionsWrapper doubleClick(Locator locator) {
                actions.doubleClick(waitThat(locator).toBeClickable());
                return this;
            }

            @Override
            public IActionsWrapper sendKeys(Locator locator, CharSequence... keysToSend) {
                actions.sendKeys(waitThat(locator).toBeVisible(), keysToSend);
                return this;
            }

            @Override
            public IActionsWrapper sendKeys(CharSequence... keysToSend) {
                actions.sendKeys(keysToSend);
                return this;
            }

            @Override
            public IActionsWrapper moveToElement(Locator locator) {
                actions.moveToElement(waitThat(locator).toBeVisible());
                return this;
            }

            @Override
            public Action build() {
                final Action action = actions.build();
                return new Action() {
                    @Override
                    public void perform() {
                        action.perform();
                        waitThat().documentComplete();
                    }
                };
            }

            @Override
            public void perform() {
                actions.perform();
                waitThat().documentComplete();
            }
        };
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return js.executeScript(script, args);
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return js.executeAsyncScript(script, args);
    }

    @Override
    public IWebElementWrapper element(final Locator locator) {
        return new IWebElementWrapper() {
            /**
             * click web element if it's clickable, please use this click method as default
             */
            @Override
            public IWebElementWrapper click() {
                logger.debug("try to click {}", locator);
                waitThat(locator).toBeClickable().click();
                waitThat().documentComplete();
                return this;
            }

            /**
             * click element without considering anything, it may raise unexpected exception
             */
            @Override
            public IWebElementWrapper silentClick() {
                logger.debug("try to click {} silently", locator);
                driver.findElement(locator.by()).click();
                waitThat().documentComplete();
                return this;
            }

            /**
             * forced to click element even if it's not clickable, it may raise unexpected exception, please use method click as default
             */
            @Override
            public IWebElementWrapper forcedClick() {
                try {
                    return click();
                } catch (WebDriverException e) {
                    return silentClick();
                }
            }

            /**
             * click an element if it's displayed, otherwise skip this action
             *
             * @return boolean
             */
            @Override
            public boolean smartClick() {
                if (isDisplayed()) {
                    click();
                    return true;
                }
                return false;
            }

            /**
             * click a locator by javascript
             */
            @Override
            public IWebElementWrapper jsClick() {
                logger.debug("try to click {} by executing javascript", locator);
                executeScript("arguments[0].click()", waitThat(locator).toBePresent());
                waitThat().documentComplete();
                return this;
            }

            /**
             * click the first element in a loop while it's displayed
             */
            @Override
            public IWebElementWrapper loopClick() throws InterruptedException {
                long now = System.currentTimeMillis();
                while (isDisplayed()) {
                    click();
                    waitThat().timeout(PropHelper.TIMEOUT_NAVIGATION_INTERVAL);
                    if (System.currentTimeMillis() - now > PropHelper.TIMEOUT_INTERVAL * 1000) {
                        logger.warn("time out occurs on loop clicking {}", locator);
                        break;
                    }
                }
                return this;
            }

            /**
             * double click web element if it's clickable
             */
            @Override
            public IWebElementWrapper doubleClick() {
                logger.debug("try to double click {}", locator);
                actions().doubleClick(locator).build().perform();
                return this;
            }

            /**
             * Type value into the web edit box if it's visible
             *
             * @param value
             */
            @Override
            public IWebElementWrapper type(CharSequence... value) {
                logger.debug("try to type {} on {}", StringUtils.join(value), locator);
                waitThat(locator).toBeVisible().sendKeys(value);
                waitThat().documentComplete();
                return this;
            }

            /**
             * Clear the content of the web edit box if it's visible
             */
            @Override
            public IWebElementWrapper clear() {
                logger.debug("try to clear value on " + locator);
                waitThat(locator).toBeVisible().clear();
                waitThat().documentComplete();
                return this;
            }

            /**
             * clear the web edit box and input the value
             *
             * @param value
             */
            @Override
            public IWebElementWrapper input(String value) {
                return clear().type(value);
            }

            /**
             * input an element if it's displayed, otherwise skip this action
             *
             * @param value
             * @return boolean
             */
            @Override
            public boolean smartInput(String value) {
                if (isDisplayed()) {
                    input(value);
                    return true;
                }
                return false;
            }

            /**
             * clear the web edit box and input the value, then click the ajax locator
             *
             * @param value
             * @param ajaxLocator
             */
            @Override
            public IWebElementWrapper ajaxInput(String value, Locator ajaxLocator) {
                input(value);
                element(ajaxLocator).click();
                return this;
            }

            /**
             * check web check box on or off if it's visible
             *
             * @param checked on or off
             */
            @Override
            public IWebElementWrapper check(boolean checked) {
                logger.debug("try to check {} {}", checked ? "on" : "off", locator);
                if (isChecked() != checked) {
                    click();
                }
                return this;
            }

            /**
             * web check box checked or not
             *
             * @return checked or not
             */
            @Override
            public boolean isChecked() {
//                String checked = getAttribute("checked");
//                if (checked == null || !checked.toLowerCase().equals("true")) {
//                    return false;
//                } else {
//                    return true;
//                }
                return isSelected();
            }

            /**
             * using java script to check web check box on or off
             *
             * @param checked on or off
             */
            @Override
            public IWebElementWrapper jsCheck(boolean checked) {
                logger.debug("try to check {} {} by javascript", checked ? "on" : "off", locator);
                if (checked) {
                    setAttribute("checked", "checked");
                } else {
                    removeAttribute("checked");
                }
                return this;
            }

            /**
             * Select all options that display text matching the argument. That is, when
             * given "Bar" this would select an option like:
             * <p/>
             * &lt;option value="foo"&gt;Bar&lt;/option&gt;
             *
             * @param text The visible text to match against
             */
            @Override
            public IWebElementWrapper selectByVisibleText(String text) {
                logger.debug("try to select {} on {}", text, locator);
                new Select(waitThat(locator).toBeVisible()).selectByVisibleText(text);
                waitThat().documentComplete();
                return this;
            }

            /**
             * Clear all selected entries. This is only valid when the SELECT supports
             * multiple selections.
             *
             * @throws UnsupportedOperationException If the SELECT does not support multiple selections
             */
            @Override
            public IWebElementWrapper deselectAll() {
                logger.debug("try to deselect all options on {}", locator);
                new Select(waitThat(locator).toBeVisible()).deselectAll();
                waitThat().documentComplete();
                return this;
            }

            @Override
            public IWebElementWrapper deselectByVisibleText(String text) {
                return null;
            }

            @Override
            public IWebElementWrapper deselectByVisibleText(List<String> texts) {
                return null;
            }

            @Override
            public IWebElementWrapper deselectByIndex(int index) {
                return null;
            }

            @Override
            public IWebElementWrapper deselectByValue(String value) {
                return null;
            }

            /**
             * Select all options that display text matching the argument. That is, when
             * given "Bar" this would select an option like:
             * <p/>
             * &lt;option value="foo"&gt;Bar&lt;/option&gt;
             *
             * @param texts The visible text to match against
             */
            @Override
            public IWebElementWrapper selectByVisibleText(List<String> texts) {
                for (String text : texts) {
                    selectByVisibleText(text);
                }
                return this;
            }

            /**
             * Select the option at the given index. This is done locator examing the
             * "index" attributeValueOf of an element, and not merely locator counting.
             *
             * @param index The option at this index will be selected
             */
            @Override
            public IWebElementWrapper selectByIndex(int index) {
                logger.debug("try to select index {} on {}", index, locator);
                new Select(waitThat(locator).toBeVisible()).selectByIndex(index);
                waitThat().documentComplete();
                return this;
            }

            /**
             * Select all options that have a value matching the argument. That is, when
             * given "foo" this would select an option like:
             * <p/>
             * &lt;option value="foo"&gt;Bar&lt;/option&gt;
             *
             * @param value The value to match against
             */
            @Override
            public IWebElementWrapper selectByValue(String value) {
                logger.debug("try to select value {} on {}", value, locator);
                new Select(waitThat(locator).toBeVisible()).selectByValue(value);
                waitThat().documentComplete();
                return this;
            }

            /**
             * @param text
             * @return whether text is selectable or not
             */
            @Override
            public boolean isTextSelectable(String text) {
                for (WebElement e : getAllSelectedOptions()) {
                    if (text.equals(e.getText())) {
                        return true;
                    }
                }
                return false;
            }

            /**
             */
            @Override
            public IWebElementWrapper moveTo() {
                logger.debug("move mouse to {}", locator);
                actions().moveToElement(locator).build().perform();
                return this;
            }

            /**
             * whether locator is present or not
             *
             * @return whether locator is present or not
             */
            @Override
            public boolean isPresent() {
                try {
                    driver.findElement(locator.by());
                    return true;
                } catch (WebDriverException e) {
                    return false;
                }
            }

            /**
             * whether locator is enabled or not
             *
             * @return boolean
             */
            @Override
            public boolean isEnabled() {
                return waitThat(locator).toBePresent().isEnabled();
            }

            /**
             * whether locator is displayed or not
             *
             * @return boolean
             */
            @Override
            public boolean isDisplayed() {
                try {
                    return driver.findElement(locator.by()).isDisplayed();
                } catch (WebDriverException e) {
                    return false;
                }
            }

            /**
             * whether locator is selected or not
             *
             * @return boolean
             */
            @Override
            public boolean isSelected() {
                return waitThat(locator).toBePresent().isSelected();
            }

            /**
             * get value of specified attributeValueOf
             *
             * @param attribute
             * @return attributeValueOf value
             */
            @Override
            public String getAttribute(String attribute) {
                return waitThat(locator).toBePresent().getAttribute(attribute);
            }

            /**
             * get css attributeValueOf value
             *
             * @param attribute
             * @return string
             */
            @Override
            public String getCssValue(String attribute) {
                return waitThat(locator).toBePresent().getCssValue(attribute);
            }

            /**
             * get text on such web element
             *
             * @return string
             */
            @Override
            public String getInnerText() {
                return waitThat(locator).toBeVisible().getText();
            }

            /**
             * get all text on found locators
             *
             * @return text list
             */
            @Override
            public List<String> getAllInnerTexts() {
                List<String> Texts = new ArrayList<String>();
                for (WebElement element : waitThat(locator).toBeAllPresent()) {
                    Texts.add(element.getText());
                }
                return Texts;
            }

            /**
             * set innert text on such web element
             *
             * @param text
             */
            @Override
            public IWebElementWrapper setText(String text) {
                logger.debug("try to set innertext of {} to {}", locator, text);
                executeScript("arguments[0].innerText=arguments[1]", waitThat(locator).toBePresent(), text);
                waitThat().documentComplete();
                return this;
            }

            /**
             * set value on such web element, an alternative approach for method input
             *
             * @param value
             */
            @Override
            public IWebElementWrapper setValue(String value) {
                logger.debug("try to set text of {} to {}", locator, value);
                executeScript("arguments[0].value=arguments[1]", waitThat(locator).toBePresent(), value);
                waitThat().documentComplete();
                return this;
            }

            /**
             * get all selected options in web list element
             *
             * @return List&gt;WebElement&lt;
             */
            @Override
            public List<WebElement> getAllSelectedOptions() {
                return new Select(waitThat(locator).toBeVisible()).getAllSelectedOptions();
            }

            /**
             * get all options in web list element
             *
             * @return List&gt;WebElement&lt;
             */
            @Override
            public List<WebElement> getAllOptions() {
                return new Select(waitThat(locator).toBeVisible()).getOptions();
            }

            /**
             * get all options text in web list element
             *
             * @return List&gt;String&lt;
             */
            @Override
            public List<String> getAllOptionTexts() {
                List<String> list = new ArrayList<String>();
                List<WebElement> options = getAllOptions();
                for (WebElement option : options) {
                    list.add(option.getText());
                }
                return list;
            }

            /**
             * get first selected text in web list element
             *
             * @return string
             */
            @Override
            public String getSelectedText() {
                return getAllSelectedOptions().get(0).getText();
            }

            /**
             * get all selected texts in web list element
             *
             * @return List&gt;String&lt;
             */
            @Override
            public List<String> getAllSelectedTexts() {
                List<String> list = new ArrayList<String>();
                List<WebElement> options = getAllSelectedOptions();
                for (WebElement option : options) {
                    list.add(option.getText());
                }
                return list;
            }

            /**
             * trigger an event on such element
             *
             * @param event String, such as "mouseover"
             */
            @Override
            public IWebElementWrapper triggerEvent(String event) {
                logger.debug("try to trigger {} on {}", event, locator);
                JavascriptLibrary javascript = new JavascriptLibrary();
                javascript.callEmbeddedSelenium(driver, "triggerEvent", waitThat(locator).toBePresent(), event);
                waitThat().documentComplete();
                return this;
            }

            /**
             * fire an event on such element
             *
             * @param event String, such as "onchange"
             */
            @Override
            public IWebElementWrapper fireEvent(String event) {
                logger.debug("try to fire {} on {}", event, locator);
                executeScript("arguments[0].fireEvent(arguments[1]);", waitThat(locator).toBePresent(), event);
                waitThat().documentComplete();
                return this;
            }

            /**
             * Scroll page or scrollable element to a specific target element.
             */
            @Override
            public IWebElementWrapper scrollTo() {
                logger.debug("try to scroll to {}", locator);
                WebElement element = waitThat(locator).toBePresent();
                executeScript("window.scrollTo(arguments[0],arguments[1])", element.getLocation().x, element.getLocation().y);
                waitThat().documentComplete();
                return this;
            }

            /**
             * immediately showing the user the result of some action without requiring
             * the user to manually scroll through the document to find the result
             * Scrolls the object so that top of the object is visible at the top of the
             * window.
             */
            @Override
            public IWebElementWrapper scrollIntoView() {
                scrollIntoView(true);
                waitThat().documentComplete();
                return this;
            }

            /**
             * immediately showing the user the result of some action without requiring
             * the user to manually scroll through the document to find the result
             *
             * @param bAlignToTop true Default. Scrolls the object so that top of the object is
             *                    visible at the top of the window. <br/>
             *                    false Scrolls the object so that the bottom of the object is
             *                    visible at the bottom of the window.
             */
            @Override
            public IWebElementWrapper scrollIntoView(boolean bAlignToTop) {
                logger.debug("try to scroll into view on {}, align to top is {}", locator, bAlignToTop);
                executeScript("arguments[0].scrollIntoView(arguments[1])", waitThat(locator).toBePresent(), bAlignToTop);
                waitThat().documentComplete();
                return this;
            }

            /**
             * using java script to set element attributeValueOf
             *
             * @param attribute
             * @param value
             */
            @Override
            public IWebElementWrapper setAttribute(String attribute, String value) {
                logger.debug("try to set attributeValueOf {} on {} to {}", attribute, locator, value);
                executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", waitThat(locator).toBePresent(), attribute, value);
                waitThat().documentComplete();
                return this;
            }

            /**
             * using java script to remove element attributeValueOf
             *
             * @param attribute
             */
            @Override
            public IWebElementWrapper removeAttribute(String attribute) {
                logger.debug("try to remove attributeValueOf {} on {}", attribute, locator);
                executeScript("arguments[0].removeAttribute(arguments[1])", waitThat(locator).toBePresent(), attribute);
                waitThat().documentComplete();
                return this;
            }

            /**
             * using java script to get row number of cell element in web table
             */
            @Override
            public long getCellRow() {
                long ret = -1;
                ret = (long) executeScript("return arguments[0].parentNode.rowIndex", waitThat(locator).toBePresent());
                ret++;// row index starts with zero
                return ret;
            }

            /**
             * using java script to get column number of cell element in web table
             */
            @Override
            public long getCellColumn() {
                long ret = -1;
                ret = (long) executeScript("return arguments[0].cellIndex", waitThat(locator).toBePresent());
                ret++;// column index starts with zero
                return ret;
            }

            /**
             * using java script to get row number of row element in web table
             */
            @Override
            public long getRow() {
                long ret = -1;
                ret = (long) executeScript("return arguments[0].rowIndex", waitThat(locator).toBePresent());
                ret++;// row index starts with zero
                return ret;
            }

            /**
             * using java script to get row count of web table
             *
             * @return long
             */
            @Override
            public long getRowCount() {
                long ret = -1;
                ret = (long) executeScript("return arguments[0].rows.length", waitThat(locator).toBePresent());
                return ret;
            }

            @Override
            public IWebDriverWrapper switchTo() {
                return waitThat(locator).frameToBeAvailableAndSwitchToIt();
            }

            @Override
            public int getNumberOfMatches() {
                return waitThat(locator).toBeAllPresent().size();
            }
        };
    }

    @Override
    public IFluentLocatorWait waitThat(final Locator locator) {
        return new IFluentLocatorWait() {

            @Override
            public IFluentStringWait innerText() {
                return new IFluentStringWait() {

                    @Override
                    public Boolean toBe(String text) {
                        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator.by(), text));
                    }

                    @Override
                    public Boolean toBeEmpty() {
                        return toBe("");
                    }

                    @Override
                    public Boolean notToBe(String text) {
                        return null;
                    }

                    @Override
                    public Boolean contains(String text) {
                        return null;
                    }

                    @Override
                    public Boolean notContains(String text) {
                        return null;
                    }

                    @Override
                    public Boolean startWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean endWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean matches(Pattern pattern) {
                        return wait.until(ExpectedConditions.textMatches(locator.by(), pattern));
                    }
                };
            }

            @Override
            public Boolean toBeInvisible() {
                return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator.by()));
            }

            @Override
            public Boolean toBeAllInvisible() {
                return null;
            }

            @Override
            public WebElement toBePresent() {
                return wait.until(ExpectedConditions.presenceOfElementLocated(locator.by()));
            }

            @Override
            public List<WebElement> toBeAllPresent() {
                return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator.by()));
            }

            @Override
            public Boolean toBeAbsent() {
                return wait.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(org.openqa.selenium.WebDriver driver) {
                        return !element(locator).isPresent();
                    }

                    @Override
                    public String toString() {
                        return String.format("wait absence of element located by %s", locator);
                    }
                });
            }

            @Override
            public Boolean toBeAllAbsent() {
                return null;
            }

            @Override
            public Boolean toBeSelected() {
                return wait.until(ExpectedConditions.elementToBeSelected(locator.by()));
            }

            @Override
            public Boolean toBeDeselected() {
                return null;
            }

            /**
             * wait the specified locator to be present
             *
             * @param milliseconds timeout
             */
            @Override
            public IFluentLocatorWait toBePresentIn(int milliseconds) {
                long t = System.currentTimeMillis();
                while (System.currentTimeMillis() - t < milliseconds) {
                    if (element(locator).isPresent()) {
                        return this;
                    }
                }
                logger.warn("wait presence of {} timed out in {} milliseconds", locator, milliseconds);
                return this;
            }

            /**
             * wait the specified locator to be absent
             *
             * @param milliseconds timeout
             */
            @Override
            public IFluentLocatorWait toBeAbsentIn(int milliseconds) {
                long t = System.currentTimeMillis();
                while (System.currentTimeMillis() - t < milliseconds) {
                    if (!element(locator).isPresent()) {
                        return this;
                    }
                }
                logger.warn("wait absence of {} timed out in {} milliseconds", locator, milliseconds);
                return this;
            }

            @Override
            public WebElement toBeClickable() {
                return wait.until(ExpectedConditions.elementToBeClickable(locator.by()));
            }

            @Override
            public WebElement toBeVisible() {
                return wait.until(ExpectedConditions.visibilityOfElementLocated(locator.by()));
            }

            @Override
            public List<WebElement> toBeAllVisible() {
                return null;
            }

            @Override
            public IWebDriverWrapper frameToBeAvailableAndSwitchToIt() {
                return new WebDriverWrapper(wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator.by())));
            }

            @Override
            public IFluentStringWait attributeValueOf(final String attribute) {
                return new IFluentStringWait() {

                    @Override
                    public Boolean toBeEmpty() {
                        return toBe("");
                    }

                    @Override
                    public Boolean notToBe(String text) {
                        return null;
                    }

                    @Override
                    public Boolean contains(String text) {
                        return null;
                    }

                    @Override
                    public Boolean notContains(String text) {
                        return null;
                    }

                    @Override
                    public Boolean startWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean endWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean matches(Pattern pattern) {
                        return null;
                    }

                    @Override
                    public Boolean toBe(final String text) {
                        return wait.until(new ExpectedCondition<Boolean>() {
                            @Override
                            public Boolean apply(org.openqa.selenium.WebDriver driver) {
                                return element(locator).getAttribute(attribute).equals(text);
                            }

                            @Override
                            public String toString() {
                                return String.format("wait %s attributeValueOf value of %s to be %s", attribute, locator, text);
                            }
                        });
                    }
                };
            }

            @Override
            public IFluentStringWait cssValueOf(final String cssAttribute) {
                return new IFluentStringWait() {

                    @Override
                    public Boolean toBeEmpty() {
                        return toBe("");
                    }

                    @Override
                    public Boolean notToBe(String text) {
                        return null;
                    }

                    @Override
                    public Boolean contains(String text) {
                        return null;
                    }

                    @Override
                    public Boolean notContains(String text) {
                        return null;
                    }

                    @Override
                    public Boolean startWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean endWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean matches(Pattern pattern) {
                        return null;
                    }

                    @Override
                    public Boolean toBe(final String text) {
                        return wait.until(new ExpectedCondition<Boolean>() {
                            @Override
                            public Boolean apply(org.openqa.selenium.WebDriver driver) {
                                return element(locator).getCssValue(cssAttribute).equals(text);
                            }

                            @Override
                            public String toString() {
                                return String.format("wait %s css value of %s to be %s", cssAttribute, locator, text);
                            }
                        });
                    }
                };
            }

            @Override
            public IFluentNumberWait numberOfElements() {
                return new IFluentNumberWait() {
                    @Override
                    public Boolean equalTo(final int number) {
                        return wait.until(new ExpectedCondition<Boolean>() {
                            @Nullable
                            @Override
                            public Boolean apply(@Nullable WebDriver input) {
                                return driver.findElements(locator.by()).size() == number;
                            }
                        });
                    }

                    @Override
                    public Boolean notEqualTo(final int number) {
                        return wait.until(new ExpectedCondition<Boolean>() {
                            @Nullable
                            @Override
                            public Boolean apply(@Nullable WebDriver input) {
                                return driver.findElements(locator.by()).size() != number;
                            }
                        });
                    }

                    @Override
                    public Boolean lessThan(final int number) {
                        return wait.until(new ExpectedCondition<Boolean>() {
                            @Nullable
                            @Override
                            public Boolean apply(@Nullable WebDriver input) {
                                return driver.findElements(locator.by()).size() < number;
                            }
                        });
                    }

                    @Override
                    public Boolean greaterThan(final int number) {
                        return wait.until(new ExpectedCondition<Boolean>() {
                            @Nullable
                            @Override
                            public Boolean apply(@Nullable WebDriver input) {
                                return driver.findElements(locator.by()).size() > number;
                            }
                        });
                    }

                    @Override
                    public Boolean equalToOrLessThan(final int number) {
                        return wait.until(new ExpectedCondition<Boolean>() {
                            @Nullable
                            @Override
                            public Boolean apply(@Nullable WebDriver input) {
                                return driver.findElements(locator.by()).size() <= number;
                            }
                        });
                    }

                    @Override
                    public Boolean equalToOrGreaterThan(final int number) {
                        return wait.until(new ExpectedCondition<Boolean>() {
                            @Nullable
                            @Override
                            public Boolean apply(@Nullable WebDriver input) {
                                return driver.findElements(locator.by()).size() >= number;
                            }
                        });
                    }
                };
            }

            @Override
            public IFluentLocatorWait nestedElements(Locator locator) {
                return null;
            }
        };
    }

    @Override
    public IFluentWait waitThat() {
        return new IFluentWait() {
            @Override
            public void timeout(int milliseconds) throws InterruptedException {
                logger.debug("force to wait {} milliseconds", milliseconds);
                Thread.sleep(milliseconds);
            }

            @Override
            public Boolean documentComplete() {
                return wait.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(org.openqa.selenium.WebDriver driver) {
                        try {
                            return executeScript("return document.readyState").equals("complete");
                        } catch (WebDriverException e) {
                            logger.warn("javascript error while waiting document complete");
                            return true;
                        }
                    }

                    @Override
                    public String toString() {
                        return "wait document complete";
                    }
                });
            }

            @Override
            public IFluentStringWait pageTitle() {
                return new IFluentStringWait() {

                    @Override
                    public Boolean toBe(String text) {
                        return wait.until(ExpectedConditions.titleIs(text));
                    }

                    @Override
                    public Boolean toBeEmpty() {
                        return null;
                    }

                    @Override
                    public Boolean notToBe(String text) {
                        return null;
                    }

                    @Override
                    public Boolean contains(String text) {
                        return wait.until(ExpectedConditions.titleContains(text));
                    }

                    @Override
                    public Boolean notContains(String text) {
                        return null;
                    }

                    @Override
                    public Boolean startWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean endWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean matches(Pattern pattern) {
                        return null;
                    }
                };
            }

            @Override
            public IFluentStringWait pageSource() {
                return new IFluentStringWait() {

                    @Override
                    public Boolean toBe(String text) {
                        return null;
                    }

                    @Override
                    public Boolean toBeEmpty() {
                        return null;
                    }

                    @Override
                    public Boolean notToBe(String text) {
                        return null;
                    }

                    @Override
                    public Boolean contains(final String text) {
                        return wait.until(new ExpectedCondition<Boolean>() {
                            @Override
                            public Boolean apply(org.openqa.selenium.WebDriver driver) {
                                return isPageSourceContains(text);
                            }

                            @Override
                            public String toString() {
                                return String.format("wait page source contains %s", text);
                            }
                        });
                    }

                    @Override
                    public Boolean notContains(String text) {
                        return null;
                    }

                    @Override
                    public Boolean startWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean endWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean matches(Pattern pattern) {
                        return null;
                    }
                };
            }

            @Override
            public IFluentStringWait url() {
                return new IFluentStringWait() {

                    @Override
                    public Boolean toBe(String text) {
                        return null;
                    }

                    @Override
                    public Boolean toBeEmpty() {
                        return null;
                    }

                    @Override
                    public Boolean notToBe(String text) {
                        return null;
                    }

                    @Override
                    public Boolean contains(String text) {
                        return null;
                    }

                    @Override
                    public Boolean notContains(String text) {
                        return null;
                    }

                    @Override
                    public Boolean startWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean endWith(String text) {
                        return null;
                    }

                    @Override
                    public Boolean matches(Pattern pattern) {
                        return null;
                    }
                };
            }

            @Override
            public IAlertWrapper alertIsPresent() {
                return new AlertWrapper(wait.until(ExpectedConditions.alertIsPresent()));
            }
        };
    }

    @Override
    public IFluentLocatorAssert assertThat(final Locator locator) {
        return new IFluentLocatorAssert() {
            @Override
            public AbstractListAssert<? extends AbstractListAssert, ? extends List, String> allSelectedTexts() {
                return org.assertj.core.api.Assertions.assertThat(element(locator).getAllSelectedTexts()).as("assert %s all selected texts", locator);
            }

            @Override
            public AbstractListAssert<? extends AbstractListAssert, ? extends List, String> allOptionTexts() {
                return org.assertj.core.api.Assertions.assertThat(element(locator).getAllOptionTexts()).as("assert %s all option texts", locator);
            }

            @Override
            public AbstractBooleanAssert<?> present() {
                return org.assertj.core.api.Assertions.assertThat(element(locator).isPresent()).as("assert %s present", locator);
            }

            @Override
            public AbstractBooleanAssert<?> enabled() {
                return org.assertj.core.api.Assertions.assertThat(element(locator).isEnabled()).as("assert %s enabled", locator);
            }

            @Override
            public AbstractBooleanAssert<?> displayed() {
                return org.assertj.core.api.Assertions.assertThat(element(locator).isDisplayed()).as("assert %s displayed", locator);
            }

            @Override
            public AbstractBooleanAssert<?> selected() {
                return org.assertj.core.api.Assertions.assertThat(element(locator).isSelected()).as("assert %s selected", locator);
            }

            @Override
            public AbstractCharSequenceAssert<?, String> innerText() {
                return org.assertj.core.api.Assertions.assertThat(element(locator).getInnerText()).as("assert %s innertText", locator);
            }

            @Override
            public AbstractListAssert<? extends AbstractListAssert, ? extends List, String> allInnerTexts() {
                return org.assertj.core.api.Assertions.assertThat(element(locator).getAllInnerTexts()).as("assert %s all innerTexts", locator);
            }

            @Override
            public AbstractCharSequenceAssert<?, String> attributeValueOf(String attribute) {
                return org.assertj.core.api.Assertions.assertThat(element(locator).getAttribute(attribute)).as("assert %s attribute value of %s", locator, attribute);
            }

            @Override
            public AbstractCharSequenceAssert<?, String> cssValueOf(String cssAttribute) {
                return org.assertj.core.api.Assertions.assertThat(element(locator).getCssValue(cssAttribute)).as("assert %s css value of %s", locator, cssAttribute);
            }

            @Override
            public AbstractIntegerAssert<? extends AbstractIntegerAssert<?>> numberOfElements() {
                return org.assertj.core.api.Assertions.assertThat(driver.findElements(locator.by()).size()).as("assert number of elements %s", locator);
            }

            @Override
            public IFluentLocatorAssert nestedElements(Locator locator) {
                return null;
            }
        };
    }

    @Override
    public IFluentAssert assertThat() {
        return new IFluentAssert() {
            @Override
            public AbstractBooleanAssert<?> alertIsPresent() {
                return org.assertj.core.api.Assertions.assertThat(alert().exists()).as("assert alert exists");
            }

            @Override
            public AbstractCharSequenceAssert<?, String> alertText() {
                return org.assertj.core.api.Assertions.assertThat(alert().getText()).as("assert alert text");
            }

            @Override
            public AbstractCharSequenceAssert<?, String> pageTitle() {
                return org.assertj.core.api.Assertions.assertThat(getPageTitle()).as("assert page title");
            }

            @Override
            public AbstractCharSequenceAssert<?, String> pageSource() {
                return org.assertj.core.api.Assertions.assertThat(getPageSource()).as("assert page source");
            }

            @Override
            public AbstractCharSequenceAssert<?, String> url() {
                return org.assertj.core.api.Assertions.assertThat(getCurrentUrl()).as("assert current url");
            }
        };
    }

    @Override
    public IAlertWrapper alert() {
        return new AlertWrapper();
    }

    private class AlertWrapper implements IAlertWrapper {
        private Alert alert;

        public AlertWrapper() {
        }

        public AlertWrapper(Alert alert) {
            this.alert = alert;
        }

        @Override
        public void dismiss() {
            logger.debug("try to dismiss alert {}", getText());
            if (alert != null) {
                alert.dismiss();
            } else {
                waitThat().alertIsPresent().dismiss();
            }
        }

        @Override
        public void accept() {
            logger.debug("try accept alert {}", getText());
            if (alert != null) {
                alert.accept();
            } else {
                waitThat().alertIsPresent().accept();
            }
        }

        @Override
        public String getText() {
            if (alert != null) {
                return alert.getText();
            } else {
                return waitThat().alertIsPresent().getText();
            }
        }

        @Override
        public boolean exists() {
            try {
                driver.switchTo().alert();
                return true;
            } catch (WebDriverException e) {
                return false;
            }
        }
    }
}
