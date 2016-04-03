package org.yiwan.webcore.web;

import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WebDriverWrapper {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private WebDriver driver;
    private JavascriptExecutor js;
    private Wait<org.openqa.selenium.WebDriver> wait;

    public WebDriverWrapper(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.wait = new WebDriverWait(driver, PropHelper.TIMEOUT_INTERVAL, PropHelper.TIMEOUT_POLLING_INTERVAL).ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class).ignoring(UnreachableBrowserException.class);
    }

    public void browse(String url) {
        logger.debug("try to navigate to url {}", url);
        driver.navigate().to(url);
        waitThat().documentComplete();
    }

    public void forward() {
        logger.debug("try to navigate forward");
        driver.navigate().forward();
        waitThat().documentComplete();
    }

    /**
     * navigate back
     */
    public void backward() {
        logger.debug("try to navigate back");
        driver.navigate().back();
        waitThat().documentComplete();
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
        logger.debug("try to close browser tab with title {}", getPageTitle());
        driver.close();
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
        driver.quit();
    }

    /**
     * delete all cookies
     */
    public void deleteAllCookies() {
        logger.debug("try to delete all cookies");
        driver.manage().deleteAllCookies();
    }

    /**
     * is page source contains such text
     *
     * @param text
     * @return boolean
     */
    public boolean isPageSourceContains(String text) {
        return driver.getPageSource().contains(text);
    }

    /**
     * get page source of current page
     *
     * @return page source string
     */
    public String getPageSource() {
        return driver.getPageSource();
    }

    /**
     * get current url address
     *
     * @return string value of current url
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * switch to a window with a specified name or handle
     *
     * @param nameOrHandle
     */
    public WebDriver switchToWindow(String nameOrHandle) {
        logger.debug("try to switch to window {}", nameOrHandle);
        return driver.switchTo().window(nameOrHandle);
    }

    /**
     * Switch to default content from a frame
     */
    public WebDriver switchToDefaultWindow() {
        logger.debug("try to switch to default content");
        return driver.switchTo().defaultContent();
    }

    /**
     * get current page title
     *
     * @return string value of title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * click element if it's displayed, otherwise click the next one
     */
    public void smartClick(Locator... locators) {
        for (Locator locator : locators) {
            if (element(locator).smartClick()) {
                break;
            }
        }
    }

    /**
     * input value in the first locator if it exists, otherwise input the next one
     *
     * @param value
     * @param locators
     */
    public void smartInput(String value, Locator... locators) {
        for (Locator locator : locators) {
            if (element(locator).smartInput(value)) {
                break;
            }
        }
    }

    /**
     * capture screenshot for local or remote testing
     *
     * @return screenshot TakesScreenshot
     */
    public TakesScreenshot getTakesScreenshot() {
        if (PropHelper.REMOTE) {
            // RemoteWebDriver does not implement the TakesScreenshot class if
            // the driver does have the Capabilities to take a screenshot then
            // Augmenter will add the TakesScreenshot methods to the instance
            return (TakesScreenshot) (new Augmenter().augment(driver));
        } else {
            return (TakesScreenshot) driver;
        }
    }

    /**
     * @param key
     */
    public void typeKeyEvent(int key) throws AWTException {
        logger.debug("type key event " + key);
        Robot robot;
        robot = new Robot();
        robot.keyPress(key);
    }

    public ActionsWrapper actions() {
        return new ActionsWrapper();
    }

    public Object executeScript(String script, Object... args) {
        return js.executeScript(script, args);
    }

    public Object executeAsyncScript(String script, Object... args) {
        return js.executeAsyncScript(script, args);
    }

    public WebElementWrapper element(Locator locator) {
        return new WebElementWrapper(locator);
    }

    public FluentLocatorWait waitThat(Locator locator) {
        return new FluentLocatorWait(locator);
    }

    public FluentWait waitThat() {
        return new FluentWait();
    }

    public FluentLocatorAssert assertThat(Locator locator) {
        return new FluentLocatorAssert(locator);
    }

    public FluentAssert assertThat() {
        return new FluentAssert();
    }

    public AlertWrapper alert() {
        return new AlertWrapper();
    }

    public class ActionsWrapper {
        private Actions actions;

        public ActionsWrapper() {
            this.actions = new Actions(driver);
        }

        public ActionsWrapper click() {
            actions.click();
            return this;
        }

        public ActionsWrapper click(Locator locator) {
            actions.click(waitThat(locator).toBeClickable());
            return this;
        }

        public ActionsWrapper sendKeys(Locator locator, CharSequence... keysToSend) {
            actions.sendKeys(waitThat(locator).toBeVisible(), keysToSend);
            return this;
        }

        public ActionsWrapper sendKeys(CharSequence... keysToSend) {
            actions.sendKeys(keysToSend);
            return this;
        }

        public Action build() {
            return actions.build();
        }

        public void perform() {
            actions.perform();
        }
    }

    public class WebElementWrapper {
        private Locator locator;

        public WebElementWrapper(Locator locator) {
            this.locator = locator;
        }

        /**
         * click web element if it's clickable, please use this click method as default
         */
        public void click() {
            logger.debug("try to click {}", locator);
            waitThat(locator).toBeClickable().click();
            waitThat().documentComplete();
        }

        /**
         * click element without considering anything, it may raise unexpected exception
         */
        public void silentClick() {
            logger.debug("try to click {} silently", locator);
            driver.findElement(locator.by()).click();
            waitThat().documentComplete();
        }

        /**
         * forced to click element even if it's not clickable, it may raise unexpected exception, please use method click as default
         */
        public void forcedClick() {
            try {
                click();
            } catch (WebDriverException e) {
                silentClick();
            }
        }

        /**
         * click an element if it's displayed, otherwise skip this action
         *
         * @return boolean
         */
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
        public void jsClick() {
            logger.debug("try to click {} by executing javascript", locator);
            executeScript("arguments[0].click()", waitThat(locator).toBePresent());
            waitThat().documentComplete();
        }

        /**
         * click the first element in a loop while it's displayed
         */
        public void loopClick() throws InterruptedException {
            long now = System.currentTimeMillis();
            while (isDisplayed()) {
                click();
                waitThat().timeout(PropHelper.TIMEOUT_NAVIGATION_INTERVAL);
                if (System.currentTimeMillis() - now > PropHelper.TIMEOUT_INTERVAL * 1000) {
                    logger.warn("time out occurs on loop clicking {}", locator);
                    return;
                }
            }
        }

        /**
         * double click web element if it's clickable
         */
        public void doubleClick() {
            logger.debug("try to double click {}", locator);
            Actions action = new Actions(driver);
            action.doubleClick(waitThat(locator).toBeClickable()).build().perform();
            waitThat().documentComplete();
        }

        /**
         * Type value into the web edit box if it's visible
         *
         * @param value
         */
        public void type(CharSequence... value) {
            logger.debug("try to type {} on {}", StringUtils.join(value), locator);
            waitThat(locator).toBeVisible().sendKeys(value);
            waitThat().documentComplete();
        }

        /**
         * Clear the content of the web edit box if it's visible
         */
        public void clear() {
            logger.debug("try to clear value on " + locator);
            waitThat(locator).toBeVisible().clear();
            waitThat().documentComplete();
        }

        /**
         * clear the web edit box and input the value
         *
         * @param value
         */
        public void input(String value) {
            clear();
            type(value);
        }

        /**
         * input an element if it's displayed, otherwise skip this action
         *
         * @param value
         * @return boolean
         */
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
        public void ajaxInput(String value, Locator ajaxLocator) {
            input(value);
            element(ajaxLocator).click();
        }

        /**
         * tick web check box if it's visible
         *
         * @param value true indicate tick on, false indicate tick off
         */
        public void tick(boolean value) {
            logger.debug("try to tick {} on {}", value, locator);
            if (isTicked() != value) {
                click();
            }
        }

        /**
         * web check box ticked or not
         *
         * @return ticked or not
         */
        public boolean isTicked() {
            String checked = getAttribute("checked");
            if (checked == null || !checked.toLowerCase().equals("true")) {
                return false;
            } else {
                return true;
            }
        }

        /**
         * using java script to tick web check box
         *
         * @param value true indicate tick on, false indicate tick off
         */
        public void alteredTick(boolean value) {
            logger.debug("try tick {} on {} alternately", value, locator);
            if (value) {
                setAttribute("checked", "checked");
            } else {
                removeAttribute("checked");
            }
        }

        /**
         * Select all options that display text matching the argument. That is, when
         * given "Bar" this would select an option like:
         * <p/>
         * &lt;option value="foo"&gt;Bar&lt;/option&gt;
         *
         * @param text The visible text to match against
         */
        public void selectByVisibleText(String text) {
            logger.debug("try to select {} on {}", text, locator);
            new Select(waitThat(locator).toBeVisible()).selectByVisibleText(text);
            waitThat().documentComplete();
        }

        /**
         * Clear all selected entries. This is only valid when the SELECT supports
         * multiple selections.
         *
         * @throws UnsupportedOperationException If the SELECT does not support multiple selections
         */
        public void deselectAll() {
            logger.debug("try to deselect all options on {}", locator);
            new Select(waitThat(locator).toBeVisible()).deselectAll();
            waitThat().documentComplete();
        }

        /**
         * Select all options that display text matching the argument. That is, when
         * given "Bar" this would select an option like:
         * <p/>
         * &lt;option value="foo"&gt;Bar&lt;/option&gt;
         *
         * @param texts The visible text to match against
         */
        public void selectByVisibleText(List<String> texts) {
            for (String text : texts) {
                selectByVisibleText(text);
            }
        }

        /**
         * Select the option at the given index. This is done locator examing the
         * "index" attribute of an element, and not merely locator counting.
         *
         * @param index The option at this index will be selected
         */
        public void selectByIndex(int index) {
            logger.debug("try to select index {} on {}", index, locator);
            new Select(waitThat(locator).toBeVisible()).selectByIndex(index);
            waitThat().documentComplete();
        }

        /**
         * Select all options that have a value matching the argument. That is, when
         * given "foo" this would select an option like:
         * <p/>
         * &lt;option value="foo"&gt;Bar&lt;/option&gt;
         *
         * @param value The value to match against
         */
        public void selectByValue(String value) {
            logger.debug("try to select value {} on {}", value, locator);
            new Select(waitThat(locator).toBeVisible()).selectByValue(value);
            waitThat().documentComplete();
        }

        /**
         * @param text
         * @return whether text is selectable or not
         */
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
        public void moveTo() {
            logger.debug("move mouse to {}", locator);
            Actions action = new Actions(driver);
            action.moveToElement(waitThat(locator).toBeVisible()).build().perform();
            waitThat().documentComplete();
        }

        /**
         * whether locator is present or not
         *
         * @return whether locator is present or not
         */
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
        public boolean isEnabled() {
            return waitThat(locator).toBePresent().isEnabled();
        }

        /**
         * whether locator is displayed or not
         *
         * @return boolean
         */
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
        public boolean isSelected() {
            return waitThat(locator).toBePresent().isSelected();
        }

        /**
         * get value of specified attribute
         *
         * @param attribute
         * @return attribute value
         */
        public String getAttribute(String attribute) {
            return waitThat(locator).toBePresent().getAttribute(attribute);
        }

        /**
         * get css attribute value
         *
         * @param attribute
         * @return string
         */
        public String getCssValue(String attribute) {
            return waitThat(locator).toBePresent().getCssValue(attribute);
        }

        /**
         * get text on such web element
         *
         * @return string
         */
        public String getInnerText() {
            return waitThat(locator).toBeVisible().getText();
        }

        /**
         * get all text on found locators
         *
         * @return text list
         */
        public List<String> getAllTexts() {
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
        public void setText(String text) {
            logger.debug("try to set innertext of {} to {}", locator, text);
            executeScript("arguments[0].innerText=arguments[1]", waitThat(locator).toBePresent(), text);
            waitThat().documentComplete();
        }

        /**
         * set value on such web element, an alternative approach for method input
         *
         * @param value
         */
        public void setValue(String value) {
            logger.debug("try to set text of {} to {}", locator, value);
            executeScript("arguments[0].value=arguments[1]", waitThat(locator).toBePresent(), value);
            waitThat().documentComplete();
        }

        /**
         * get all selected options in web list element
         *
         * @return List&gt;WebElement&lt;
         */
        public List<WebElement> getAllSelectedOptions() {
            return new Select(waitThat(locator).toBeVisible()).getAllSelectedOptions();
        }

        /**
         * get all options in web list element
         *
         * @return List&gt;WebElement&lt;
         */
        public List<WebElement> getAllOptions() {
            return new Select(waitThat(locator).toBeVisible()).getOptions();
        }

        /**
         * get all options text in web list element
         *
         * @return List&gt;String&lt;
         */
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
        public String getSelectedText() {
            return getAllSelectedOptions().get(0).getText();
        }

        /**
         * get all selected texts in web list element
         *
         * @return List&gt;String&lt;
         */
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
        public void triggerEvent(String event) {
            logger.debug("try to trigger {} on {}", event, locator);
            JavascriptLibrary javascript = new JavascriptLibrary();
            javascript.callEmbeddedSelenium(driver, "triggerEvent", waitThat(locator).toBePresent(), event);
            waitThat().documentComplete();
        }

        /**
         * fire an event on such element
         *
         * @param event String, such as "onchange"
         */
        public void fireEvent(String event) {
            logger.debug("try to fire {} on {}", event, locator);
            executeScript("arguments[0].fireEvent(arguments[1]);", waitThat(locator).toBePresent(), event);
            waitThat().documentComplete();
        }

        /**
         * Scroll page or scrollable element to a specific target element.
         */
        public void scrollTo() {
            logger.debug("try to scroll to {}", locator);
            WebElement element = waitThat(locator).toBePresent();
            executeScript("window.scrollTo(arguments[0],arguments[1])", element.getLocation().x, element.getLocation().y);
            waitThat().documentComplete();
        }

        /**
         * immediately showing the user the result of some action without requiring
         * the user to manually scroll through the document to find the result
         * Scrolls the object so that top of the object is visible at the top of the
         * window.
         */
        public void scrollIntoView() {
            scrollIntoView(true);
            waitThat().documentComplete();
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
        public void scrollIntoView(boolean bAlignToTop) {
            logger.debug("try to scroll into view on {}, align to top is {}", locator, bAlignToTop);
            executeScript("arguments[0].scrollIntoView(arguments[1])", waitThat(locator).toBePresent(), bAlignToTop);
            waitThat().documentComplete();
        }

        /**
         * using java script to set element attribute
         *
         * @param attribute
         * @param value
         */
        public void setAttribute(String attribute, String value) {
            logger.debug("try to set attribute {} on {} to {}", attribute, locator, value);
            executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", waitThat(locator).toBePresent(), attribute, value);
            waitThat().documentComplete();
        }

        /**
         * using java script to remove element attribute
         *
         * @param attribute
         */
        public void removeAttribute(String attribute) {
            logger.debug("try to remove attribute {} on {}", attribute, locator);
            executeScript("arguments[0].removeAttribute(arguments[1])", waitThat(locator).toBePresent(), attribute);
            waitThat().documentComplete();
        }

        /**
         * using java script to get row number of cell element in web table
         */
        public long getCellRow() {
            long ret = -1;
            ret = (long) executeScript("return arguments[0].parentNode.rowIndex", waitThat(locator).toBePresent());
            ret++;// row index starts with zero
            return ret;
        }

        /**
         * using java script to get column number of cell element in web table
         */
        public long getCellColumn() {
            long ret = -1;
            ret = (long) executeScript("return arguments[0].cellIndex", waitThat(locator).toBePresent());
            ret++;// column index starts with zero
            return ret;
        }

        /**
         * using java script to get row number of row element in web table
         */
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
        public long getRowCount() {
            long ret = -1;
            ret = (long) executeScript("return arguments[0].rows.length", waitThat(locator).toBePresent());
            return ret;
        }
    }

    public class AlertWrapper {
        public void dismiss() {
            logger.debug("try to dismiss alert {}", getText());
            waitThat().alertIsPresent().dismiss();
        }

        public void accept() {
            logger.debug("try accept alert {}", getText());
            waitThat().alertIsPresent().accept();
        }

        public String getText() {
            return waitThat().alertIsPresent().getText();
        }

        public boolean exists() {
            try {
                driver.switchTo().alert();
                return true;
            } catch (WebDriverException e) {
                return false;
            }
        }
    }

    public class FluentAssert {
        public AbstractBooleanAssert<?> alertExists() {
            return org.assertj.core.api.Assertions.assertThat(alert().exists()).as("assert alert exists");
        }

        public AbstractCharSequenceAssert<?, String> alertText() {
            return org.assertj.core.api.Assertions.assertThat(alert().getText()).as("assert alert text");
        }

        public AbstractCharSequenceAssert<?, String> pageTitle() {
            return org.assertj.core.api.Assertions.assertThat(getPageTitle()).as("assert page title");
        }

        public AbstractBooleanAssert<?> pageSourceContains(String text) {
            return org.assertj.core.api.Assertions.assertThat(isPageSourceContains(text)).as("assert page source contains text %s", text);
        }
    }

    public class FluentLocatorAssert {
        private Locator locator;

        public FluentLocatorAssert(Locator locator) {
            this.locator = locator;
        }

        public AbstractBooleanAssert<?> hasSelectableText(String text) {
            return org.assertj.core.api.Assertions.assertThat(element(locator).isTextSelectable(text)).as("assert %s has selectable text %s", locator, text);
        }

        public AbstractListAssert<? extends AbstractListAssert, ? extends List, String> selectedTexts() {
            return org.assertj.core.api.Assertions.assertThat(element(locator).getAllSelectedTexts()).as("assert %s has selected texts", locator);
        }

        public AbstractBooleanAssert<?> isEnabled() {
            return org.assertj.core.api.Assertions.assertThat(element(locator).isEnabled()).as("assert %s is enabled", locator);
        }

        public AbstractBooleanAssert<?> isDisplayed() {
            return org.assertj.core.api.Assertions.assertThat(element(locator).isDisplayed()).as("assert %s is displayed", locator);
        }

        public AbstractBooleanAssert<?> isSelected() {
            return org.assertj.core.api.Assertions.assertThat(element(locator).isSelected()).as("assert %s is selected", locator);
        }

        public AbstractCharSequenceAssert<?, String> innerText() {
            return org.assertj.core.api.Assertions.assertThat(element(locator).getInnerText()).as("assert %s text", locator);
        }

        public AbstractCharSequenceAssert<?, String> attribute(String attribute) {
            return org.assertj.core.api.Assertions.assertThat(element(locator).getAttribute(attribute)).as("assert %s attribute %s", locator, attribute);
        }

        public AbstractCharSequenceAssert<?, String> cssValue(String css) {
            return org.assertj.core.api.Assertions.assertThat(element(locator).getCssValue(css)).as("assert %s css %s", locator, css);
        }
    }

    public class FluentWait {
        public void timeout(int milliseconds) throws InterruptedException {
            logger.debug("force to wait {} milliseconds", milliseconds);
            Thread.sleep(milliseconds);
        }

        Boolean documentComplete() {
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

        public Alert alertIsPresent() {
            return wait.until(ExpectedConditions.alertIsPresent());
        }

        public Boolean pageTitleIs(String title) {
            return wait.until(ExpectedConditions.titleIs(title));
        }

        public Boolean pageTitleContains(String title) {
            return wait.until(ExpectedConditions.titleContains(title));
        }

        public Boolean pageSourceContains(final String text) {
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
    }

    public class FluentLocatorWait {
        private Locator locator;

        public FluentLocatorWait(Locator locator) {
            this.locator = locator;
        }

        public Boolean hasInnerText(String text) {
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator.by(), text));
        }

        public Boolean toBeInvisible() {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator.by()));
        }

        public WebElement toBePresent() {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator.by()));
        }

        public List<WebElement> toBeAllPresent() {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator.by()));
        }

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

        /**
         * wait the specified locator to be present
         *
         * @param milliseconds timeout
         */
        public void toBePresentIn(int milliseconds) {
            long t = System.currentTimeMillis();
            while (System.currentTimeMillis() - t < milliseconds) {
                if (element(locator).isPresent()) {
                    return;
                }
            }
            logger.warn("wait presence of {} timed out in {} milliseconds", locator, milliseconds);
        }

        /**
         * wait the specified locator to be absent
         *
         * @param milliseconds timeout
         */
        public void toBeAbsentIn(int milliseconds) {
            long t = System.currentTimeMillis();
            while (System.currentTimeMillis() - t < milliseconds) {
                if (!element(locator).isPresent()) {
                    return;
                }
            }
            logger.warn("wait absence of {} timed out in {} milliseconds", locator, milliseconds);
        }

        public WebElement toBeClickable() {
            return wait.until(ExpectedConditions.elementToBeClickable(locator.by()));
        }

        public WebElement toBeVisible() {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator.by()));
        }

        public void frameToBeAvailableAndSwitchToIt() {
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator.by()));
        }

        public FluentLocatorAttributeWait attribute(String attribute) {
            return new FluentLocatorAttributeWait(attribute);
        }

        public FluentLocatorCssAttributeWait cssAttribute(String cssAttribute) {
            return new FluentLocatorCssAttributeWait(cssAttribute);
        }

        public class FluentLocatorAttributeWait {
            private String attribute;

            public FluentLocatorAttributeWait(String attribute) {
                this.attribute = attribute;
            }

            public Boolean valueToBeEmpty() {
                return valueToBe("");
            }

            public Boolean valueToBe(final String value) {
                return wait.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(org.openqa.selenium.WebDriver driver) {
                        return element(locator).getAttribute(attribute).equals(value);
                    }

                    @Override
                    public String toString() {
                        return String.format("wait %s attribute value of %s to be %s", attribute, locator, value);
                    }
                });
            }
        }

        public class FluentLocatorCssAttributeWait {
            private String cssAttribute;

            public FluentLocatorCssAttributeWait(String cssAttribute) {
                this.cssAttribute = cssAttribute;
            }

            public Boolean valueToBeEmpty() {
                return valueToBe("");
            }

            public Boolean valueToBe(final String value) {
                return wait.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(org.openqa.selenium.WebDriver driver) {
                        return element(locator).getCssValue(cssAttribute).equals(value);
                    }

                    @Override
                    public String toString() {
                        return String.format("wait %s css value of %s to be %s", cssAttribute, locator, value);
                    }
                });
            }
        }
    }
}
