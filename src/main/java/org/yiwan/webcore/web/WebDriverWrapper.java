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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
        return new BrowseNavigation();
    }

    @Override
    public IWebDriverWrapper maximize() {
        logger.debug("maximizing browser");
        driver.manage().window().maximize();
        return this;
    }

    @Override
    public IWebDriverWrapper close() {
        logger.debug("closing browser tab with title {}", getPageTitle());
        driver.close();
        return this;
    }

    @Override
    public IWebDriverWrapper closeAll() {
        logger.debug("closing all browser tabs");
        for (String handle : driver.getWindowHandles()) {
            switchToWindow(handle);
            close();
        }
        return this;
    }

    @Override
    public IWebDriverWrapper quit() {
        logger.debug("quiting driver");
        driver.quit();
        return this;
    }

    @Override
    public IWebDriverWrapper deleteAllCookies() {
        logger.debug("deleting all cookies");
        driver.manage().deleteAllCookies();
        return this;
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public Set<Cookie> getCookies() {
        return driver.manage().getCookies();
    }

    @Override
    public IWebDriverWrapper switchToWindow(String nameOrHandle) {
        logger.debug("switching to window {}", nameOrHandle);
        driver.switchTo().window(nameOrHandle);
        return this;
    }

    @Override
    public IWebDriverWrapper switchToDefaultWindow() {
        logger.debug("switching to default content");
        driver.switchTo().defaultContent();
        return this;
    }

    @Override
    public IWebDriverWrapper switchToFrame(int index) {
        logger.debug("switching to frame {}", index);
        driver.switchTo().frame(index);
        return this;
    }

    @Override
    public IWebDriverWrapper switchToFrame(String nameOrId) {
        logger.debug("switching to frame {}", nameOrId);
        driver.switchTo().frame(nameOrId);
        return this;
    }

    @Override
    public String getPageTitle() {
        return driver.getTitle();
    }

    @Override
    public IWebDriverWrapper clickSmartly(Locator... locators) {
        for (Locator locator : locators) {
            if (element(locator).clickSmartly()) {
                break;
            }
        }
        return this;
    }

    @Override
    public IWebDriverWrapper inputSmartly(String value, Locator... locators) {
        for (Locator locator : locators) {
            if (element(locator).inputSmartly(value)) {
                break;
            }
        }
        return this;
    }

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

    @Override
    public IWebDriverWrapper typeKeyEvent(int key) throws AWTException {
        logger.debug("typing key event " + key);
        Robot robot;
        robot = new Robot();
        robot.keyPress(key);
        return this;
    }

    @Override
    public IActionsWrapper actions() {
        return new ActionsWrapper();
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
    public IWebElementWrapper element(Locator locator) {
        return new WebLocatorWrapper(locator);
    }

    private IWebElementWrapper element(WebElement webElement) {
        return new WebElementWrapper(webElement);
    }

    @Override
    public IFluentLocatorWait waitThat(Locator locator) {
        return new FluentLocatorWait(locator);
    }

    @Override
    public IFluentWait waitThat() {
        return new FluentWait();
    }

    @Override
    public IFluentLocatorAssert assertThat(Locator locator) {
        return new FluentLocatorAssert(locator);
    }

    @Override
    public IFluentAssert assertThat() {
        return new FluentAssert();
    }

    @Override
    public IAlertWrapper alert() {
        return new AlertWrapper();
    }

    public IWebDriverWrapper doPostAction() {
//            if (!WebDriverWrapper.this.alert().isPresent()) {
        waitThat().document().toBeComplete();
        waitThat().jQuery().toBeInactive();
//            }
        return this;
    }

    private class WebLocatorWrapper implements IWebElementWrapper {
        private Locator locator;

        public WebLocatorWrapper(Locator locator) {
            this.locator = locator;
        }

        @Override
        public IWebElementWrapper click() {
            logger.debug("clicking {}", locator);
            waitThat(locator).toBeClickable().click();
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper clickSilently() {
            logger.debug("clicking {} silently", locator);
            driver.findElement(locator.by()).click();
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper clickForcedly() {
            try {
                return click();
            } catch (WebDriverException e) {
                return clickSilently();
            }
        }

        @Override
        public boolean clickSmartly() {
            if (isDisplayed()) {
                click();
                return true;
            }
            return false;
        }

        @Override
        public IWebElementWrapper clickByJavaScript() {
            logger.debug("clicking {} by executing javascript", locator);
            executeScript("arguments[0].click()", waitThat(locator).toBePresent());
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper clickCircularly() throws InterruptedException {
            wait.until(new ExpectedCondition<Boolean>() {
                @Nullable
                @Override
                public Boolean apply(@Nullable WebDriver input) {
                    click();
                    return !isDisplayed();
                }
            });
            return this;
        }

        @Override
        public IWebElementWrapper doubleClick() {
            actions().doubleClick(locator).perform();
            return this;
        }

        @Override
        public IWebElementWrapper contextClick() {
            actions().contextClick(locator).perform();
            return this;
        }

        @Override
        public IWebElementWrapper dragAndDrop(Locator target) {
            actions().dragAndDrop(locator, target).perform();
            return this;
        }

        @Override
        public IWebElementWrapper dragAndDrop(int xOffset, int yOffset) {
            actions().dragAndDrop(locator, xOffset, yOffset).perform();
            return this;
        }

        @Override
        public IWebElementWrapper type(CharSequence... value) {
            logger.debug("typing {} on {}", StringUtils.join(value), locator);
            waitThat(locator).toBeEnabled().sendKeys(value);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper clear() {
            logger.debug("clearing value on " + locator);
            waitThat(locator).toBeEnabled().clear();
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper input(String value) {
            return clear().type(value);
        }

        @Override
        public boolean inputSmartly(String value) {
            if (isDisplayed()) {
                input(value);
                return true;
            }
            return false;
        }

        @Override
        public IWebElementWrapper check(boolean checked) {
            logger.debug("checking {} {}", checked ? "on" : "off", locator);
            if (isChecked() != checked) {
                click();
            }
            return this;
        }

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

        @Override
        public boolean tick(boolean checked) {
            logger.debug("ticking {} {}", checked ? "on" : "off", locator);
            if (isChecked() != checked) {
                click();
                return true;
            }
            return false;
        }

        @Override
        public IWebElementWrapper selectAll() {
            Select select = new Select(waitThat(locator).toBeVisible());
            if (!select.isMultiple()) {
                throw new UnsupportedOperationException(String.format("You may only select all options of a multi-select %s", locator));
            }

            for (String text : getAllOptionTexts()) {
                selectByVisibleText(text);
            }
            return this;
        }

        @Override
        public IWebElementWrapper checkByJavaScript(boolean checked) {
            logger.debug("checking {} {} by javascript", checked ? "on" : "off", locator);
            if (checked) {
                setAttribute("checked", "checked");
            } else {
                removeAttribute("checked");
            }
            return this;
        }

        @Override
        public IWebElementWrapper selectByVisibleText(String text) {
            logger.debug("selecting by visible text {} on {}", text, locator);
            new Select(waitThat(locator).toBeVisible()).selectByVisibleText(text);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper selectByVisibleText(String... texts) {
            for (String text : texts) {
                selectByVisibleText(text);
            }
            return this;
        }

        @Override
        public IWebElementWrapper selectByIndex(int index) {
            logger.debug("selecting by index {} on {}", index, locator);
            new Select(waitThat(locator).toBeVisible()).selectByIndex(index);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper selectByValue(String value) {
            logger.debug("selecting by value {} on {}", value, locator);
            new Select(waitThat(locator).toBeVisible()).selectByValue(value);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper deselectAll() {
            logger.debug("deselecting all options on {}", locator);
            new Select(waitThat(locator).toBeVisible()).deselectAll();
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper deselectByVisibleText(String text) {
            logger.debug("deselecting by visible text {} on {}", text, locator);
            new Select(waitThat(locator).toBeVisible()).deselectByVisibleText(text);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper deselectByVisibleText(String... texts) {
            for (String text : texts) {
                deselectByVisibleText(text);
            }
            return this;
        }

        @Override
        public IWebElementWrapper deselectByIndex(int index) {
            logger.debug("deselecting by index {} on {}", index, locator);
            new Select(waitThat(locator).toBeVisible()).deselectByIndex(index);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper deselectByValue(String value) {
            logger.debug("deselecting by value {} on {}", value, locator);
            new Select(waitThat(locator).toBeVisible()).deselectByValue(value);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper moveTo() {
            actions().moveTo(locator).perform();
            return this;
        }

        @Override
        public boolean isPresent() {
            try {
                driver.findElement(locator.by());
                return true;
            } catch (WebDriverException e) {
                return false;
            }
        }

        @Override
        public boolean isEnabled() {
            return driver.findElement(locator.by()).isEnabled();
        }

        @Override
        public boolean isDisplayed() {
            try {
                return driver.findElement(locator.by()).isDisplayed();
            } catch (WebDriverException e) {
                return false;
            }
        }

        @Override
        public boolean isSelected() {
            return waitThat(locator).toBePresent().isSelected();
        }

        @Override
        public String getAttribute(String attribute) {
            return waitThat(locator).toBePresent().getAttribute(attribute);
        }

        @Override
        public String getCssValue(String attribute) {
            return waitThat(locator).toBePresent().getCssValue(attribute);
        }

        @Override
        public String getInnerText() {
            return waitThat(locator).toBeVisible().getText();
        }

        @Override
        public List<String> getAllInnerTexts() {
            List<String> texts = new ArrayList<String>();
            for (WebElement element : waitThat(locator).toBeAllPresent()) {
                texts.add(element.getText());
            }
            return texts;
        }

        @Override
        public IWebElementWrapper setInnerText(String text) {
            logger.debug("setting innertext {} on {}", text, locator);
            executeScript("arguments[0].innerText=arguments[1]", waitThat(locator).toBePresent(), text);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper setValue(String value) {
            logger.debug("setting text {} on {}", value, locator);
            executeScript("arguments[0].value=arguments[1]", waitThat(locator).toBePresent(), value);
            return this;
        }

        @Override
        public List<WebElement> getAllSelectedOptions() {
            return new Select(waitThat(locator).toBeVisible()).getAllSelectedOptions();
        }

        @Override
        public List<WebElement> getAllOptions() {
            return new Select(waitThat(locator).toBeVisible()).getOptions();
        }

        @Override
        public List<String> getAllOptionTexts() {
            List<String> list = new ArrayList<String>();
            List<WebElement> options = getAllOptions();
            for (WebElement option : options) {
                list.add(option.getText());
            }
            return list;
        }

        @Override
        public String getSelectedText() {
            return getAllSelectedOptions().get(0).getText();
        }

        @Override
        public List<String> getAllSelectedTexts() {
            List<String> list = new ArrayList<String>();
            List<WebElement> options = getAllSelectedOptions();
            for (WebElement option : options) {
                list.add(option.getText());
            }
            return list;
        }

        @Override
        public IWebElementWrapper triggerEvent(String event) {
            logger.debug("triggering {} on {}", event, locator);
            JavascriptLibrary javascript = new JavascriptLibrary();
            javascript.callEmbeddedSelenium(driver, "triggerEvent", waitThat(locator).toBePresent(), event);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper fireEvent(String event) {
            logger.debug("firing {} on {}", event, locator);
            try {
                executeScript("arguments[0].fireEvent(arguments[1]);", waitThat(locator).toBePresent(), event);
            } catch (WebDriverException e) {
                String eventType = null;
                switch (event.toLowerCase()) {
                    case "onchange":
                        eventType = "change";
                        break;
                    case "onclick":
                        eventType = "click";
                        break;
                }
                executeScript("var evt = document.createEvent('HTMLEvents'); evt.initEvent(arguments[1], true, true); arguments[0].dispatchEvent(evt);", waitThat(locator).toBePresent(), eventType);
            }
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper scrollTo() {
            logger.debug("scrolling to {}", locator);
            WebElement element = waitThat(locator).toBePresent();
            executeScript("window.scrollTo(arguments[0],arguments[1])", element.getLocation().x, element.getLocation().y);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper scrollIntoView() {
            scrollIntoView(true);
            return this;
        }

        @Override
        public IWebElementWrapper scrollIntoView(boolean bAlignToTop) {
            logger.debug("scrolling into view {}on {}", bAlignToTop ? "by aligning to top " : "", locator);
            executeScript("arguments[0].scrollIntoView(arguments[1])", waitThat(locator).toBePresent(), bAlignToTop);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper setAttribute(String attribute, String value) {
            logger.debug("setting attribute {} value {} on {}", attribute, value, locator);
            executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", waitThat(locator).toBePresent(), attribute, value);
            return this;
        }

        @Override
        public IWebElementWrapper removeAttribute(String attribute) {
            logger.debug("removing attribute {} on {}", attribute, locator);
            executeScript("arguments[0].removeAttribute(arguments[1])", waitThat(locator).toBePresent(), attribute);
            return this;
        }

        @Override
        public long getCellRow() {
            long ret = -1;
            ret = (long) executeScript("return arguments[0].parentNode.rowIndex", waitThat(locator).toBePresent());
            ret++;// row index starts with zero
            return ret;
        }

        @Override
        public long getCellColumn() {
            long ret = -1;
            ret = (long) executeScript("return arguments[0].cellIndex", waitThat(locator).toBePresent());
            ret++;// column index starts with zero
            return ret;
        }

        @Override
        public long getRow() {
            long ret = -1;
            ret = (long) executeScript("return arguments[0].rowIndex", waitThat(locator).toBePresent());
            ret++;// row index starts with zero
            return ret;
        }

        @Override
        public long getRowCount() {
            long ret = -1;
            ret = (long) executeScript("return arguments[0].rows.length", waitThat(locator).toBePresent());
            return ret;
        }

        @Override
        public IWebDriverWrapper switchTo() {
            logger.debug("switching to frame {}", locator);
            return waitThat(locator).frameToBeAvailableAndSwitchToIt();
        }

        @Override
        public int getNumberOfMatches() {
            return waitThat(locator).toBeAllPresent().size();
        }

        @Override
        public List<IWebElementWrapper> getAllMatchedElements() {
            List<WebElement> webElements = waitThat(locator).toBeAllPresent();
            List<IWebElementWrapper> webElementWrappers = new ArrayList<>();
            for (WebElement webElement : webElements) {
                webElementWrappers.add(element(webElement));
            }
            return webElementWrappers;
        }
    }

    private class WebElementWrapper implements IWebElementWrapper {
        private WebElement webElement;

        public WebElementWrapper(WebElement webElement) {
            this.webElement = webElement;
        }

        @Override
        public IWebElementWrapper click() {
            logger.debug("clicking {}", webElement);
            wait.until(ExpectedConditions.visibilityOf(webElement)).click();
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper clickSilently() {
            logger.debug("clicking {} silently", webElement);
            webElement.click();
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper clickForcedly() {
            try {
                return click();
            } catch (WebDriverException e) {
                return clickSilently();
            }
        }

        @Override
        public boolean clickSmartly() {
            if (isDisplayed()) {
                click();
                return true;
            }
            return false;
        }

        @Override
        public IWebElementWrapper clickByJavaScript() {
            logger.debug("clicking {} by executing javascript", webElement.getText());
            executeScript("arguments[0].click()", wait.until(ExpectedConditions.visibilityOf(webElement)));
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper clickCircularly() throws InterruptedException {
            wait.until(new ExpectedCondition<Boolean>() {
                @Nullable
                @Override
                public Boolean apply(@Nullable WebDriver input) {
                    click();
                    return !isDisplayed();
                }
            });
            return this;
        }

        @Override
        public IWebElementWrapper doubleClick() {
            logger.debug("double clicking {}", webElement);
            Actions actions = new Actions(driver);
            actions.doubleClick(wait.until(ExpectedConditions.visibilityOf(webElement))).perform();
            return this;
        }

        @Override
        public IWebElementWrapper contextClick() {
            logger.debug("context clicking {}", webElement);
            Actions actions = new Actions(driver);
            actions.contextClick(wait.until(ExpectedConditions.visibilityOf(webElement))).perform();
            return this;
        }

        @Override
        public IWebElementWrapper dragAndDrop(Locator target) {
            logger.debug("dragging {} and dropping to {}", webElement, target);
            Actions actions = new Actions(driver);
            actions.dragAndDrop(wait.until(ExpectedConditions.visibilityOf(webElement)), waitThat(target).toBeVisible()).perform();
            return this;
        }

        @Override
        public IWebElementWrapper dragAndDrop(int xOffset, int yOffset) {
            logger.debug("dragging {} and dropping to ({},{})", webElement, xOffset, yOffset);
            Actions actions = new Actions(driver);
            actions.dragAndDropBy(wait.until(ExpectedConditions.visibilityOf(webElement)), xOffset, yOffset).perform();
            return this;
        }

        @Override
        public IWebElementWrapper type(CharSequence... value) {
            logger.debug("typing {} on {}", StringUtils.join(value), webElement);
            wait.until(ExpectedConditions.visibilityOf(webElement)).sendKeys(value);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper clear() {
            logger.debug("clearing value on " + webElement);
            wait.until(ExpectedConditions.visibilityOf(webElement)).clear();
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper input(String value) {
            return clear().type(value);
        }

        @Override
        public boolean inputSmartly(String value) {
            if (isDisplayed()) {
                input(value);
                return true;
            }
            return false;
        }

        @Override
        public IWebElementWrapper check(boolean checked) {
            logger.debug("checking {} {}", checked ? "on" : "off", webElement);
            if (isChecked() != checked) {
                click();
            }
            return this;
        }

        @Override
        public boolean isChecked() {
            return isSelected();
        }

        @Override
        public boolean tick(boolean checked) {
            logger.debug("ticking {} {}", checked ? "on" : "off", webElement);
            if (isChecked() != checked) {
                click();
                return true;
            }
            return false;
        }

        @Override
        public IWebElementWrapper selectAll() {
            Select select = new Select(wait.until(ExpectedConditions.visibilityOf(webElement)));
            if (!select.isMultiple()) {
                throw new UnsupportedOperationException(String.format("You may only select all options of a multi-select %s", webElement));
            }

            for (String text : getAllOptionTexts()) {
                selectByVisibleText(text);
            }
            return this;
        }

        @Override
        public IWebElementWrapper checkByJavaScript(boolean checked) {
            logger.debug("checking {} {} by javascript", checked ? "on" : "off", webElement);
            if (checked) {
                setAttribute("checked", "checked");
            } else {
                removeAttribute("checked");
            }
            return this;
        }

        @Override
        public IWebElementWrapper selectByVisibleText(String text) {
            logger.debug("selecting by visible text {} on {}", text, webElement);
            new Select(wait.until(ExpectedConditions.visibilityOf(webElement))).selectByVisibleText(text);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper selectByVisibleText(String... texts) {
            for (String text : texts) {
                selectByVisibleText(text);
            }
            return this;
        }

        @Override
        public IWebElementWrapper selectByIndex(int index) {
            logger.debug("selecting by index {} on {}", index, webElement);
            new Select(wait.until(ExpectedConditions.visibilityOf(webElement))).selectByIndex(index);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper selectByValue(String value) {
            logger.debug("selecting by value {} on {}", value, webElement);
            new Select(wait.until(ExpectedConditions.visibilityOf(webElement))).selectByValue(value);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper deselectAll() {
            logger.debug("deselecting all options on {}", webElement);
            new Select(wait.until(ExpectedConditions.visibilityOf(webElement))).deselectAll();
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper deselectByVisibleText(String text) {
            logger.debug("deselecting by visible text {} on {}", text, webElement);
            new Select(wait.until(ExpectedConditions.visibilityOf(webElement))).deselectByVisibleText(text);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper deselectByVisibleText(String... texts) {
            for (String text : texts) {
                deselectByVisibleText(text);
            }
            return this;
        }

        @Override
        public IWebElementWrapper deselectByIndex(int index) {
            logger.debug("deselecting by index {} on {}", index, webElement);
            new Select(wait.until(ExpectedConditions.visibilityOf(webElement))).deselectByIndex(index);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper deselectByValue(String value) {
            logger.debug("deselecting by value {} on {}", value, webElement);
            new Select(wait.until(ExpectedConditions.visibilityOf(webElement))).deselectByValue(value);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper moveTo() {
            Actions actions = new Actions(driver);
            actions.moveToElement(wait.until(ExpectedConditions.visibilityOf(webElement))).perform();
            return this;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return wait.until(ExpectedConditions.visibilityOf(webElement)).isEnabled();
        }

        @Override
        public boolean isDisplayed() {
            return webElement.isDisplayed();
        }

        @Override
        public boolean isSelected() {
            return wait.until(ExpectedConditions.visibilityOf(webElement)).isSelected();
        }

        @Override
        public String getAttribute(String attribute) {
            return wait.until(ExpectedConditions.visibilityOf(webElement)).getAttribute(attribute);
        }

        @Override
        public String getCssValue(String attribute) {
            return wait.until(ExpectedConditions.visibilityOf(webElement)).getCssValue(attribute);
        }

        @Override
        public String getInnerText() {
            return wait.until(ExpectedConditions.visibilityOf(webElement)).getText();
        }

        @Override
        public List<String> getAllInnerTexts() {
            return Arrays.asList(getInnerText());
        }

        @Override
        public IWebElementWrapper setInnerText(String text) {
            logger.debug("setting innertext {} on {}", text, webElement);
            executeScript("arguments[0].innerText=arguments[1]", wait.until(ExpectedConditions.visibilityOf(webElement)), text);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper setValue(String value) {
            logger.debug("setting text {} on {}", value, webElement);
            executeScript("arguments[0].value=arguments[1]", wait.until(ExpectedConditions.visibilityOf(webElement)), value);
            return this;
        }

        @Override
        public List<WebElement> getAllSelectedOptions() {
            return new Select(wait.until(ExpectedConditions.visibilityOf(webElement))).getAllSelectedOptions();
        }

        @Override
        public List<WebElement> getAllOptions() {
            return new Select(wait.until(ExpectedConditions.visibilityOf(webElement))).getOptions();
        }

        @Override
        public List<String> getAllOptionTexts() {
            List<String> list = new ArrayList<String>();
            List<WebElement> options = getAllOptions();
            for (WebElement option : options) {
                list.add(option.getText());
            }
            return list;
        }

        @Override
        public String getSelectedText() {
            return getAllSelectedOptions().get(0).getText();
        }

        @Override
        public List<String> getAllSelectedTexts() {
            List<String> list = new ArrayList<String>();
            List<WebElement> options = getAllSelectedOptions();
            for (WebElement option : options) {
                list.add(option.getText());
            }
            return list;
        }

        @Override
        public IWebElementWrapper triggerEvent(String event) {
            logger.debug("triggering {} on {}", event, webElement);
            JavascriptLibrary javascript = new JavascriptLibrary();
            javascript.callEmbeddedSelenium(driver, "triggerEvent", wait.until(ExpectedConditions.visibilityOf(webElement)), event);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper fireEvent(String event) {
            logger.debug("firing {} on {}", event, webElement);
            executeScript("arguments[0].fireEvent(arguments[1]);", wait.until(ExpectedConditions.visibilityOf(webElement)), event);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper scrollTo() {
            logger.debug("scrolling to {}", webElement);
            WebElement element = wait.until(ExpectedConditions.visibilityOf(webElement));
            executeScript("window.scrollTo(arguments[0],arguments[1])", element.getLocation().x, element.getLocation().y);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper scrollIntoView() {
            scrollIntoView(true);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper scrollIntoView(boolean bAlignToTop) {
            logger.debug("scrolling into view {}on {}", bAlignToTop ? "by aligning to top " : "", webElement);
            executeScript("arguments[0].scrollIntoView(arguments[1])", wait.until(ExpectedConditions.visibilityOf(webElement)), bAlignToTop);
            doPostAction();
            return this;
        }

        @Override
        public IWebElementWrapper setAttribute(String attribute, String value) {
            logger.debug("setting attribute {} value {} on {}", attribute, value, webElement);
            executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", wait.until(ExpectedConditions.visibilityOf(webElement)), attribute, value);
            return this;
        }

        @Override
        public IWebElementWrapper removeAttribute(String attribute) {
            logger.debug("removing attribute {} on {}", attribute, webElement);
            executeScript("arguments[0].removeAttribute(arguments[1])", wait.until(ExpectedConditions.visibilityOf(webElement)), attribute);
            return this;
        }

        @Override
        public long getCellRow() {
            long ret = -1;
            ret = (long) executeScript("return arguments[0].parentNode.rowIndex", wait.until(ExpectedConditions.visibilityOf(webElement)));
            ret++;// row index starts with zero
            return ret;
        }

        @Override
        public long getCellColumn() {
            long ret = -1;
            ret = (long) executeScript("return arguments[0].cellIndex", wait.until(ExpectedConditions.visibilityOf(webElement)));
            ret++;// column index starts with zero
            return ret;
        }

        @Override
        public long getRow() {
            long ret = -1;
            ret = (long) executeScript("return arguments[0].rowIndex", wait.until(ExpectedConditions.visibilityOf(webElement)));
            ret++;// row index starts with zero
            return ret;
        }

        @Override
        public long getRowCount() {
            long ret = -1;
            ret = (long) executeScript("return arguments[0].rows.length", wait.until(ExpectedConditions.visibilityOf(webElement)));
            return ret;
        }

        @Override
        public IWebDriverWrapper switchTo() {
            logger.debug("switching to frame {}", webElement);
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(webElement));
            return null;
        }

        @Override
        public int getNumberOfMatches() {
            return 1;
        }

        @Override
        public List<IWebElementWrapper> getAllMatchedElements() {
            List<IWebElementWrapper> webElementWrappers = new ArrayList<>();
            webElementWrappers.add(this);
            return webElementWrappers;
        }
    }

    private class FluentLocatorWait implements IFluentLocatorWait {
        private Locator locator;

        public FluentLocatorWait(Locator locator) {
            this.locator = locator;
        }

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
                public Boolean notToBe(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Nullable
                        @Override
                        public Boolean apply(@Nullable WebDriver input) {
                            currentValue = element(locator).getInnerText();
                            return !currentValue.equals(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait innerText not to be %s, current value is %s, on %s", text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean contains(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Nullable
                        @Override
                        public Boolean apply(@Nullable WebDriver input) {
                            currentValue = element(locator).getInnerText();
                            return currentValue.contains(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait innerText contains %s, current value is %s, on %s", text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean notContains(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Nullable
                        @Override
                        public Boolean apply(@Nullable WebDriver input) {
                            currentValue = element(locator).getInnerText();
                            return !currentValue.contains(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait innerText not contains %s, current value is %s, on %s", text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean startsWith(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Nullable
                        @Override
                        public Boolean apply(@Nullable WebDriver input) {
                            currentValue = element(locator).getInnerText();
                            return currentValue.startsWith(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait innerText starts with %s, current value is %s, on %s", text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean endsWith(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Nullable
                        @Override
                        public Boolean apply(@Nullable WebDriver input) {
                            currentValue = element(locator).getInnerText();
                            return currentValue.endsWith(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait innerText ends with %s, current value is %s, on %s", text, currentValue, locator);
                        }
                    });
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
        public WebElement toBePresent() {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator.by()));
        }

        @Override
        public WebElement toBeEnabled() {
            return wait.until(new ExpectedCondition<WebElement>() {
                @Override
                public WebElement apply(WebDriver driver) {
                    try {
                        WebElement element = driver.findElement(locator.by());
                        return (element.isDisplayed() && element.isEnabled()) ? element : null;
                    } catch (NoSuchElementException | InvalidElementStateException e) {
                        return null;
                    }
                }

                @Override
                public String toString() {
                    return "visibility and feasibility of element located by " + locator;
                }
            });
        }

        @Override
        public List<WebElement> toBeAllPresent() {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator.by()));
        }

        @Override
        public Boolean toBeAbsent() {
            return wait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return !element(locator).isPresent();
                }

                @Override
                public String toString() {
                    return String.format("wait absence of %s", locator);
                }
            });
        }

        @Override
        public Boolean toBeSelected() {
            return wait.until(ExpectedConditions.elementToBeSelected(locator.by()));
        }

        @Override
        public Boolean toBeDeselected() {
            return wait.until(ExpectedConditions.elementSelectionStateToBe(locator.by(), false));
        }

        @Override
        public IFluentLocatorWait toBePresentIn(long milliseconds) {
            long t = System.currentTimeMillis();
            while (System.currentTimeMillis() - t < milliseconds) {
                if (element(locator).isPresent()) {
                    return this;
                }
            }
            logger.warn("wait presence of {} in {} milliseconds", locator, milliseconds);
            return this;
        }

        @Override
        public IFluentLocatorWait toBeAbsentIn(long milliseconds) {
            long t = System.currentTimeMillis();
            while (System.currentTimeMillis() - t < milliseconds) {
                if (!element(locator).isPresent()) {
                    return this;
                }
            }
            logger.warn("wait absence of {} in {} milliseconds", locator, milliseconds);
            return this;
        }

        @Override
        public IFluentLocatorWait toBeAppearedIn(long milliseconds) {
            long t = System.currentTimeMillis();
            while (System.currentTimeMillis() - t < milliseconds) {
                if (element(locator).isDisplayed()) {
                    return this;
                }
            }
            logger.warn("wait visibility of {} in {} milliseconds", locator, milliseconds);
            return this;
        }

        @Override
        public IFluentLocatorWait toBeDisappearedIn(long milliseconds) {
            long t = System.currentTimeMillis();
            while (System.currentTimeMillis() - t < milliseconds) {
                if (!element(locator).isDisplayed()) {
                    return this;
                }
            }
            logger.warn("wait invisibility of {} in {} milliseconds", locator, milliseconds);
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
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator.by()));
        }

        @Override
        public IWebDriverWrapper frameToBeAvailableAndSwitchToIt() {
            return new WebDriverWrapper(wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator.by())));
        }

        @Override
        public IFluentStringWait attributeValueOf(final String attribute) {
            return new IFluentStringWait() {
                @Override
                public Boolean toBe(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getAttribute(attribute);
                            return currentValue.equals(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait attribute %s value to be %s, current value is %s, on %s", attribute, text.isEmpty() ? "empty" : text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean toBeEmpty() {
                    return toBe("");
                }

                @Override
                public Boolean notToBe(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getAttribute(attribute);
                            return !currentValue.equals(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait attribute %s value not to be %s, current value is %s, on %s", attribute, text.isEmpty() ? "empty" : text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean contains(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getAttribute(attribute);
                            return currentValue.contains(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait attribute %s value contains %s, current value is %s, on %s", attribute, text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean notContains(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getAttribute(attribute);
                            return !currentValue.contains(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait attribute %s value not contains %s, current value is %s, on %s", attribute, text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean startsWith(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getAttribute(attribute);
                            return currentValue.startsWith(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait attribute %s value starts with %s, current value is %s, on %s", attribute, text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean endsWith(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getAttribute(attribute);
                            return currentValue.endsWith(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait attribute %s value ends with %s, current value is %s, on %s", attribute, text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean matches(final Pattern pattern) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            try {
                                currentValue = element(locator).getAttribute(attribute);
                                return pattern.matcher(currentValue).find();
                            } catch (Exception e) {
                                return false;
                            }
                        }

                        @Override
                        public String toString() {
                            return String.format("wait attribute %s value matches pattern %s, current value is %s, on %s", attribute, pattern.pattern(), currentValue, locator);
                        }
                    });
                }
            };
        }

        @Override
        public IFluentStringWait cssValueOf(final String cssAttribute) {
            return new IFluentStringWait() {

                @Override
                public Boolean toBe(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getCssValue(cssAttribute);
                            return currentValue.equals(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait css attribute %s value to be %s, current value is %s, on %s", cssAttribute, text.isEmpty() ? "empty" : text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean toBeEmpty() {
                    return toBe("");
                }

                @Override
                public Boolean notToBe(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getCssValue(cssAttribute);
                            return !currentValue.equals(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait css attribute %s value not to be %s, current value is %s, on %s", cssAttribute, text.isEmpty() ? "empty" : text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean contains(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getCssValue(cssAttribute);
                            return currentValue.contains(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait css attribute %s value contains %s, current value is %s, on %s", cssAttribute, text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean notContains(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getCssValue(cssAttribute);
                            return !currentValue.contains(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait css attribute %s value not contains %s, current value is %s, on %s", cssAttribute, text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean startsWith(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getCssValue(cssAttribute);
                            return currentValue.startsWith(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait css attribute %s value starts with %s, current value is %s, on %s", cssAttribute, text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean endsWith(final String text) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            currentValue = element(locator).getCssValue(cssAttribute);
                            return currentValue.endsWith(text);
                        }

                        @Override
                        public String toString() {
                            return String.format("wait css attribute %s value ends with %s, current value is %s, on %s", cssAttribute, text, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean matches(final Pattern pattern) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            try {
                                currentValue = element(locator).getCssValue(cssAttribute);
                                return pattern.matcher(currentValue).find();
                            } catch (Exception e) {
                                return false;
                            }
                        }

                        @Override
                        public String toString() {
                            return String.format("wait css attribute %s value matches pattern %s, current value is %s, on %s", cssAttribute, pattern.pattern(), currentValue, locator);
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
                        private int currentValue = 0;

                        @Nullable
                        @Override
                        public Boolean apply(@Nullable WebDriver input) {
                            currentValue = driver.findElements(locator.by()).size();
                            return currentValue == number;
                        }

                        @Override
                        public String toString() {
                            return String.format("wait number of elements to be equal to %d, current value is %d, located by %s", number, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean notEqualTo(final int number) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private int currentValue = 0;

                        @Nullable
                        @Override
                        public Boolean apply(@Nullable WebDriver input) {
                            currentValue = driver.findElements(locator.by()).size();
                            return currentValue != number;
                        }

                        @Override
                        public String toString() {
                            return String.format("wait number of elements to be not equal to %d, current value is %d, located by %s", number, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean lessThan(final int number) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private int currentValue = 0;

                        @Nullable
                        @Override
                        public Boolean apply(@Nullable WebDriver input) {
                            currentValue = driver.findElements(locator.by()).size();
                            return currentValue < number;
                        }

                        @Override
                        public String toString() {
                            return String.format("wait number of elements to be less than %d, current value is %d, located by %s", number, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean greaterThan(final int number) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private int currentValue = 0;

                        @Nullable
                        @Override
                        public Boolean apply(@Nullable WebDriver input) {
                            currentValue = driver.findElements(locator.by()).size();
                            return currentValue > number;
                        }

                        @Override
                        public String toString() {
                            return String.format("wait number of elements to be greater than %d, current value is %d, located by %s", number, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean equalToOrLessThan(final int number) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private int currentValue = 0;

                        @Nullable
                        @Override
                        public Boolean apply(@Nullable WebDriver input) {
                            currentValue = driver.findElements(locator.by()).size();
                            return currentValue <= number;
                        }

                        @Override
                        public String toString() {
                            return String.format("wait number of elements to be equal to or less than %d, current value is %d, located by %s", number, currentValue, locator);
                        }
                    });
                }

                @Override
                public Boolean equalToOrGreaterThan(final int number) {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private int currentValue = 0;

                        @Nullable
                        @Override
                        public Boolean apply(@Nullable WebDriver input) {
                            currentValue = driver.findElements(locator.by()).size();
                            return currentValue >= number;
                        }

                        @Override
                        public String toString() {
                            return String.format("wait number of elements to be equal to or greater than %d, current value is %d, located by %s", number, currentValue, locator);
                        }
                    });
                }
            };
        }

        @Override
        public IFluentLocatorWait nestedElements(Locator locator) {
            return null;
        }
    }

    private class FluentWait implements IFluentWait {
        @Override
        public IFluentWait timeout(long milliseconds) throws InterruptedException {
            logger.debug("wait {} milliseconds", milliseconds);
            Thread.sleep(milliseconds);
            return this;
        }

        @Override
        public IFluentDocumentWait document() {
            return new IFluentDocumentWait() {
                @Override
                public Boolean toBeComplete() {
                    return wait.until(new ExpectedCondition<Boolean>() {
                        private String currentValue = null;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            try {
                                currentValue = (String) executeScript("return document.readyState");
                                return currentValue.equals("complete");
                            } catch (WebDriverException e) {
                                logger.warn("javascript error while waiting document to be complete");
                                return true;
                            }
                        }

                        @Override
                        public String toString() {
                            return String.format("wait document to be complete, current value is %s", currentValue);
                        }
                    });
                }
            };
        }

        @Override
        public IFluentJQueryWait jQuery() {
            return new IFluentJQueryWait() {
                @Override
                public boolean isJQuerySupported() {
                    try {
                        return (boolean) executeScript("return jQuery()!=null");
                    } catch (WebDriverException e) {
                        return false;
                    }
                }

                private void injectjQuery() {
                    executeScript(" var headID = "
                            + "document.getElementsByTagName(\"head\")[0];"
                            + "var newScript = document.createElement('script');"
                            + "newScript.type = 'text/javascript';" + "newScript.src = "
                            + "'http://ajax.googleapis.com/ajax/"
                            + "libs/jquery/1.7.2/jquery.min.js';"
                            + "headID.appendChild(newScript);");
                }

                @Override
                public Boolean toBeInactive() {
                    if (isJQuerySupported()) {
                        return wait.until(new ExpectedCondition<Boolean>() {
                            private long currentValue = 0L;

                            @Override
                            public Boolean apply(WebDriver driver) {
                                try {
                                    currentValue = (long) executeScript("return jQuery.active");
                                    return 0L == currentValue;
                                } catch (WebDriverException e) {
                                    logger.warn("javascript error while waiting jQuery to be inactive");
                                    return true;
                                }
                            }

                            @Override
                            public String toString() {
                                return String.format("wait jQuery to be inactive, current value is %d", currentValue);
                            }
                        });
                    } else {
                        return true;
                    }
                }
            };
        }

        @Override
        public IFluentAlertWait alert() {
            return new IFluentAlertWait() {
                @Override
                public IAlertWrapper toBePresent() {
                    return new AlertWrapper(wait.until(ExpectedConditions.alertIsPresent()));
                }
            };
        }

        @Override
        public IFluentPageWait page() {
            return new IFluentPageWait() {
                @Override
                public IFluentStringWait title() {
                    return new IFluentStringWait() {

                        @Override
                        public Boolean toBe(String text) {
                            return wait.until(ExpectedConditions.titleIs(text));
                        }

                        @Override
                        public Boolean toBeEmpty() {
                            return toBe("");
                        }

                        @Override
                        public Boolean notToBe(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getPageTitle();
                                    return !currentValue.equals(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page title not to be %s, current value is %s", text.isEmpty() ? "empty" : text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean contains(String text) {
                            return wait.until(ExpectedConditions.titleContains(text));
                        }

                        @Override
                        public Boolean notContains(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getPageTitle();
                                    return !currentValue.contains(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page title not contains %s, current value is %s", text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean startsWith(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getPageTitle();
                                    return currentValue.startsWith(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page title starts with %s, current value is %s", text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean endsWith(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getPageTitle();
                                    return currentValue.endsWith(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page title ends with %s, current value is %s", text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean matches(final Pattern pattern) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    try {
                                        currentValue = getPageTitle();
                                        return pattern.matcher(currentValue).find();
                                    } catch (Exception e) {
                                        return false;
                                    }
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page titie to match pattern %s, current value is %s", pattern.pattern(), currentValue);
                                }
                            });
                        }
                    }

                            ;
                }

                @Override
                public IFluentStringWait source() {
                    return new IFluentStringWait() {

                        @Override
                        public Boolean toBe(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getPageSource();
                                    return currentValue.equals(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page source to be %s, current value is %s", text.isEmpty() ? "empty" : text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean toBeEmpty() {
                            return toBe("");
                        }

                        @Override
                        public Boolean notToBe(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getPageSource();
                                    return !currentValue.equals(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page source not to be %s, current value is %s", text.isEmpty() ? "empty" : text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean contains(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getPageSource();
                                    return currentValue.contains(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page source contains %s, current value is %s", text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean notContains(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getPageSource();
                                    return !currentValue.contains(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page source not contains %s, current value is %s", text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean startsWith(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getPageSource();
                                    return currentValue.startsWith(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page source starts with %s, current value is %s", text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean endsWith(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getPageSource();
                                    return currentValue.endsWith(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page source ends with %s, current value is %s", text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean matches(final Pattern pattern) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    try {
                                        currentValue = getPageSource();
                                        return pattern.matcher(currentValue).find();
                                    } catch (Exception e) {
                                        return false;
                                    }
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait page source matches pattern %s, current value is %s", pattern.pattern(), currentValue);
                                }
                            });
                        }
                    };
                }

                @Override
                public IFluentStringWait url() {
                    return new IFluentStringWait() {

                        @Override
                        public Boolean toBe(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getCurrentUrl();
                                    return currentValue.equals(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait current url to be %s, current value is %s", text.isEmpty() ? "empty" : text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean toBeEmpty() {
                            return toBe("");
                        }

                        @Override
                        public Boolean notToBe(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getCurrentUrl();
                                    return !currentValue.equals(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait current url not to be %s, current value is %s", text.isEmpty() ? "empty" : text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean contains(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getCurrentUrl();
                                    return currentValue.contains(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait current url contains %s, current value is %s", text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean notContains(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getCurrentUrl();
                                    return !currentValue.contains(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait current url not contains %s, current value is %s", text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean startsWith(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getCurrentUrl();
                                    return currentValue.startsWith(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait current url starts with %s, current value is %s", text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean endsWith(final String text) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    currentValue = getCurrentUrl();
                                    return currentValue.endsWith(text);
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait current url ends with %s, current value is %s", text, currentValue);
                                }
                            });
                        }

                        @Override
                        public Boolean matches(final Pattern pattern) {
                            return wait.until(new ExpectedCondition<Boolean>() {
                                private String currentValue = null;

                                @Override
                                public Boolean apply(WebDriver driver) {
                                    try {
                                        currentValue = getCurrentUrl();
                                        return pattern.matcher(currentValue).find();
                                    } catch (Exception e) {
                                        return false;
                                    }
                                }

                                @Override
                                public String toString() {
                                    return String.format("wait current url matches pattern %s, current value is %s", pattern.pattern(), currentValue);
                                }
                            });
                        }
                    };
                }
            };
        }
    }

    private class FluentLocatorAssert implements IFluentLocatorAssert {
        private Locator locator;

        public FluentLocatorAssert(Locator locator) {
            this.locator = locator;
        }

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
    }

    private class FluentAssert implements IFluentAssert {
        @Override
        public IFluentAlertAssert alert() {
            return new IFluentAlertAssert() {
                @Override
                public AbstractBooleanAssert<?> present() {
                    return org.assertj.core.api.Assertions.assertThat(WebDriverWrapper.this.alert().isPresent()).as("assert alert present");
                }

                @Override
                public AbstractCharSequenceAssert<?, String> text() {
                    return org.assertj.core.api.Assertions.assertThat(WebDriverWrapper.this.alert().getText()).as("assert alert text");
                }
            };
        }

        @Override
        public IFluentPageAssert page() {
            return new IFluentPageAssert() {
                @Override
                public AbstractCharSequenceAssert<?, String> title() {
                    return org.assertj.core.api.Assertions.assertThat(getPageTitle()).as("assert page title");
                }

                @Override
                public AbstractCharSequenceAssert<?, String> source() {
                    return org.assertj.core.api.Assertions.assertThat(getPageSource()).as("assert page source");
                }

                @Override
                public AbstractCharSequenceAssert<?, String> url() {
                    return org.assertj.core.api.Assertions.assertThat(getCurrentUrl()).as("assert current url");
                }
            };
        }
    }

    private class BrowseNavigation implements IBrowseNavigation {
        @Override
        public IBrowseNavigation to(String url) {
            logger.debug("navigating to url {}", url);
            driver.navigate().to(url);
            doPostAction();
            return this;
        }

        @Override
        public IBrowseNavigation forward() {
            logger.debug("navigating forward");
            driver.navigate().forward();
            doPostAction();
            return this;
        }

        @Override
        public IBrowseNavigation backward() {
            logger.debug("navigating back");
            driver.navigate().back();
            doPostAction();
            return this;
        }

        @Override
        public IBrowseNavigation refresh() {
            logger.debug("refreshing page");
            driver.navigate().refresh();
            doPostAction();
            return this;
        }
    }

    private class ActionsWrapper implements IActionsWrapper {
        private Actions actions = new Actions(driver);
        private StringBuffer trace = new StringBuffer("try to perform following actions:");

        @Override
        public IActionsWrapper click() {
            trace.append("click anywhere;");
            actions.click();
            return this;
        }

        @Override
        public IActionsWrapper click(Locator locator) {
            trace.append(String.format("click %s;", locator));
            actions.click(waitThat(locator).toBeClickable());
            return this;
        }

        @Override
        public IActionsWrapper clickAndHold() {
            trace.append("click and hold anywhere;");
            actions.clickAndHold();
            return this;
        }

        @Override
        public IActionsWrapper clickAndHold(Locator locator) {
            trace.append(String.format("click and hold %s;", locator));
            actions.clickAndHold(waitThat(locator).toBeClickable());
            return this;
        }

        @Override
        public IActionsWrapper contextClick() {
            trace.append("context click anywhere;");
            actions.contextClick();
            return this;
        }

        @Override
        public IActionsWrapper contextClick(Locator locator) {
            trace.append(String.format("context click %s;", locator));
            actions.contextClick(waitThat(locator).toBeClickable());
            return this;
        }

        @Override
        public IActionsWrapper release() {
            trace.append("release on anywhere;");
            actions.release();
            return this;
        }

        @Override
        public IActionsWrapper release(Locator locator) {
            trace.append(String.format("release on %s;", locator));
            actions.release(waitThat(locator).toBeVisible());
            return this;
        }

        @Override
        public IActionsWrapper doubleClick() {
            trace.append("double click anywhere;");
            actions.doubleClick();
            return this;
        }

        @Override
        public IActionsWrapper doubleClick(Locator locator) {
            trace.append(String.format("double click %s;", locator));
            actions.doubleClick(waitThat(locator).toBeClickable());
            return this;
        }

        @Override
        public IActionsWrapper dragAndDrop(Locator source, Locator target) {
            trace.append(String.format("drag %s and drop on %s;", source, target));
            actions.dragAndDrop(waitThat(source).toBeClickable(), waitThat(target).toBeVisible());
            return this;
        }

        @Override
        public IActionsWrapper dragAndDrop(Locator source, int xOffset, int yOffset) {
            trace.append(String.format("drag %s and drop on (%d, %d);", source, xOffset, yOffset));
            actions.dragAndDropBy(waitThat(source).toBeClickable(), xOffset, yOffset);
            return this;
        }

        @Override
        public IActionsWrapper keyDown(Keys theKey) {
            trace.append(String.format("type key %s down on anywhere;", theKey));
            actions.keyDown(theKey);
            return this;
        }

        @Override
        public IActionsWrapper keyDown(Locator locator, Keys theKey) {
            trace.append(String.format("type key %s down on %s;", theKey, locator));
            actions.keyDown(waitThat(locator).toBeVisible(), theKey);
            return this;
        }

        @Override
        public IActionsWrapper keyUp(Keys theKey) {
            trace.append(String.format("type key %s up on anywhere;", theKey));
            actions.keyUp(theKey);
            return this;
        }

        @Override
        public IActionsWrapper keyUp(Locator locator, Keys theKey) {
            trace.append(String.format("type key %s up on %s;", theKey, locator));
            actions.keyUp(waitThat(locator).toBeVisible(), theKey);
            return this;
        }

        @Override
        public IActionsWrapper sendKeys(Locator locator, CharSequence... keysToSend) {
            trace.append(String.format("send keys %s on %s;", StringUtils.join(keysToSend), locator));
            actions.sendKeys(waitThat(locator).toBeVisible(), keysToSend);
            return this;
        }

        @Override
        public IActionsWrapper sendKeys(CharSequence... keysToSend) {
            trace.append(String.format("send keys %s on anywhere;", StringUtils.join(keysToSend)));
            actions.sendKeys(keysToSend);
            return this;
        }

        @Override
        public IActionsWrapper moveTo(Locator locator) {
            trace.append(String.format("move to %s;", locator));
            actions.moveToElement(waitThat(locator).toBeVisible());
            return this;
        }

        @Override
        public IActionsWrapper moveTo(Locator locator, int xOffset, int yOffset) {
            trace.append(String.format("move to %s offset (%d, %d);", locator, xOffset, yOffset));
            actions.moveToElement(waitThat(locator).toBeVisible(), xOffset, yOffset);
            return this;
        }

        @Override
        public IActionsWrapper moveTo(int xOffset, int yOffset) {
            trace.append(String.format("move to (%d, %d);", xOffset, yOffset));
            actions.moveByOffset(xOffset, yOffset);
            return this;
        }

        @Override
        public Action build() {
            trace.append("build all actions");
            final Action action = actions.build();
            return new Action() {
                @Override
                public void perform() {
                    logger.debug(trace.toString());
                    action.perform();
                    doPostAction();
                }
            };
        }

        @Override
        public void perform() {
            logger.debug(trace.toString());
            actions.perform();
            doPostAction();
        }
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
            logger.debug("dismissing alert {}", getText());
            if (alert != null) {
                alert.dismiss();
            } else {
                wait.until(ExpectedConditions.alertIsPresent()).dismiss();
            }
        }

        @Override
        public void accept() {
            logger.debug("accepting alert {}", getText());
            if (alert != null) {
                alert.accept();
            } else {
                wait.until(ExpectedConditions.alertIsPresent()).accept();
            }
        }

        @Override
        public String getText() {
            if (alert != null) {
                return alert.getText();
            } else {
                return wait.until(ExpectedConditions.alertIsPresent()).getText();
            }
        }

        @Override
        public boolean isPresent() {
            try {
                driver.switchTo().alert();
                return true;
            } catch (WebDriverException e) {
                return false;
            }
        }

        @Override
        public void disable() {
            disable(true);
        }

        @Override
        public void disable(boolean accept) {
            logger.debug("disabling javascript alert by {}ing all of them", accept ? "accept" : "dismiss");
            executeScript(String.format("window.alert = function(msg) {}; window.confirm = function(msg) { return %b; }; window.prompt = function(msg) { return %b; };", accept, accept));
        }

        @Override
        public void enable() {
            logger.debug("enabling javascript alert");
            executeScript("delete window.alert; delete window.confirm; delete window.prompt;");
        }
    }
}
