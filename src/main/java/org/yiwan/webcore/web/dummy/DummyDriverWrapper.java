package org.yiwan.webcore.web.dummy;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractListAssert;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.yiwan.webcore.locator.Locator;
import org.yiwan.webcore.web.*;

import java.awt.*;
import java.util.List;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public class DummyDriverWrapper implements IWebDriverWrapper {
    @Override
    public IWebDriverWrapper browse(String url) {
        return this;
    }

    @Override
    public IWebDriverWrapper forward() {
        return this;
    }

    @Override
    public IWebDriverWrapper backward() {
        return this;
    }

    @Override
    public IWebDriverWrapper maximize() {
        return this;
    }

    @Override
    public IWebDriverWrapper close() {
        return this;
    }

    @Override
    public IWebDriverWrapper closeAll() {
        return this;
    }

    @Override
    public IWebDriverWrapper quit() {
        return this;
    }

    @Override
    public IWebDriverWrapper deleteAllCookies() {
        return this;
    }

    @Override
    public boolean isPageSourceContains(String text) {
        return false;
    }

    @Override
    public String getPageSource() {
        return null;
    }

    @Override
    public String getCurrentUrl() {
        return null;
    }

    @Override
    public IWebDriverWrapper switchToWindow(String nameOrHandle) {
        return this;
    }

    @Override
    public IWebDriverWrapper switchToDefaultWindow() {
        return this;
    }

    @Override
    public IWebDriverWrapper switchToFrame(int index) {
        return null;
    }

    @Override
    public IWebDriverWrapper switchToFrame(String nameOrId) {
        return null;
    }

    @Override
    public String getPageTitle() {
        return null;
    }

    @Override
    public IWebDriverWrapper smartClick(Locator... locators) {
        return this;
    }

    @Override
    public IWebDriverWrapper smartInput(String value, Locator... locators) {
        return this;
    }

    @Override
    public TakesScreenshot getTakesScreenshot() {
        return null;
    }

    @Override
    public IWebDriverWrapper typeKeyEvent(int key) throws AWTException {
        return this;
    }

    @Override
    public IActionsWrapper actions() {
        return new ActionsWrapper();
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return null;
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return null;
    }

    @Override
    public IWebElementWrapper element(Locator locator) {
        return new WebElementWrapper();
    }

    @Override
    public IFluentLocatorWait waitThat(Locator locator) {
        return new FluentLocatorWait();
    }

    @Override
    public IFluentWait waitThat() {
        return new FluentWait();
    }

    @Override
    public IFluentLocatorAssert assertThat(Locator locator) {
        return new FluentLocatorAssert();
    }

    @Override
    public IFluentAssert assertThat() {
        return new FluentAssert();
    }

    @Override
    public IAlertWrapper alert() {
        return new AlertWrapper();
    }

    public class FluentLocatorAssert implements IFluentLocatorAssert {

        @Override
        public AbstractBooleanAssert<?> hasSelectableText(String text) {
            return null;
        }

        @Override
        public AbstractListAssert<? extends AbstractListAssert, ? extends List, String> selectedTexts() {
            return null;
        }

        @Override
        public AbstractBooleanAssert<?> isEnabled() {
            return null;
        }

        @Override
        public AbstractBooleanAssert<?> isDisplayed() {
            return null;
        }

        @Override
        public AbstractBooleanAssert<?> isSelected() {
            return null;
        }

        @Override
        public AbstractCharSequenceAssert<?, String> innerText() {
            return null;
        }

        @Override
        public AbstractCharSequenceAssert<?, String> attribute(String attribute) {
            return null;
        }

        @Override
        public AbstractCharSequenceAssert<?, String> cssValue(String css) {
            return null;
        }
    }

    public class FluentWait implements IFluentWait {

        @Override
        public void timeout(int milliseconds) throws InterruptedException {

        }

        @Override
        public IAlertWrapper alertIsPresent() {
            return null;
        }

        @Override
        public Boolean documentComplete() {
            return null;
        }

        @Override
        public Boolean pageTitleIs(String title) {
            return null;
        }

        @Override
        public Boolean pageTitleContains(String title) {
            return null;
        }

        @Override
        public Boolean pageSourceContains(String text) {
            return null;
        }
    }

    public class FluentLocatorWait implements IFluentLocatorWait {

        @Override
        public Boolean hasInnerText(String text) {
            return null;
        }

        @Override
        public Boolean toBeInvisible() {
            return null;
        }

        @Override
        public WebElement toBePresent() {
            return null;
        }

        @Override
        public List<WebElement> toBeAllPresent() {
            return null;
        }

        @Override
        public Boolean toBeAbsent() {
            return null;
        }

        @Override
        public IFluentLocatorWait toBePresentIn(int milliseconds) {
            return null;
        }

        @Override
        public IFluentLocatorWait toBeAbsentIn(int milliseconds) {
            return null;
        }

        @Override
        public WebElement toBeClickable() {
            return null;
        }

        @Override
        public WebElement toBeVisible() {
            return null;
        }

        @Override
        public IWebDriverWrapper frameToBeAvailableAndSwitchToIt() {
            return null;
        }

        @Override
        public IFluentLocatorAttributeWait attribute(String attribute) {
            return null;
        }

        @Override
        public IFluentLocatorCssAttributeWait cssAttribute(String cssAttribute) {
            return null;
        }
    }

    public class WebElementWrapper implements IWebElementWrapper {

        @Override
        public IWebElementWrapper click() {
            return null;
        }

        @Override
        public IWebElementWrapper silentClick() {
            return null;
        }

        @Override
        public IWebElementWrapper forcedClick() {
            return null;
        }

        @Override
        public boolean smartClick() {
            return false;
        }

        @Override
        public IWebElementWrapper jsClick() {
            return null;
        }

        @Override
        public IWebElementWrapper loopClick() throws InterruptedException {
            return null;
        }

        @Override
        public IWebElementWrapper doubleClick() {
            return null;
        }

        @Override
        public IWebElementWrapper type(CharSequence... value) {
            return null;
        }

        @Override
        public IWebElementWrapper clear() {
            return null;
        }

        @Override
        public IWebElementWrapper input(String value) {
            return null;
        }

        @Override
        public boolean smartInput(String value) {
            return false;
        }

        @Override
        public IWebElementWrapper ajaxInput(String value, Locator ajaxLocator) {
            return null;
        }

        @Override
        public IWebElementWrapper tick(boolean value) {
            return null;
        }

        @Override
        public boolean isTicked() {
            return false;
        }

        @Override
        public IWebElementWrapper alteredTick(boolean value) {
            return null;
        }

        @Override
        public IWebElementWrapper selectByVisibleText(String text) {
            return null;
        }

        @Override
        public IWebElementWrapper deselectAll() {
            return null;
        }

        @Override
        public IWebElementWrapper selectByVisibleText(List<String> texts) {
            return null;
        }

        @Override
        public IWebElementWrapper selectByIndex(int index) {
            return null;
        }

        @Override
        public IWebElementWrapper selectByValue(String value) {
            return null;
        }

        @Override
        public boolean isTextSelectable(String text) {
            return false;
        }

        @Override
        public IWebElementWrapper moveTo() {
            return null;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean isDisplayed() {
            return false;
        }

        @Override
        public boolean isSelected() {
            return false;
        }

        @Override
        public String getAttribute(String attribute) {
            return null;
        }

        @Override
        public String getCssValue(String attribute) {
            return null;
        }

        @Override
        public String getInnerText() {
            return null;
        }

        @Override
        public List<String> getAllTexts() {
            return null;
        }

        @Override
        public IWebElementWrapper setText(String text) {
            return null;
        }

        @Override
        public IWebElementWrapper setValue(String value) {
            return null;
        }

        @Override
        public List<WebElement> getAllSelectedOptions() {
            return null;
        }

        @Override
        public List<WebElement> getAllOptions() {
            return null;
        }

        @Override
        public List<String> getAllOptionsText() {
            return null;
        }

        @Override
        public String getSelectedText() {
            return null;
        }

        @Override
        public List<String> getAllSelectedText() {
            return null;
        }

        @Override
        public IWebElementWrapper triggerEvent(String event) {
            return null;
        }

        @Override
        public IWebElementWrapper fireEvent(String event) {
            return null;
        }

        @Override
        public IWebElementWrapper scrollTo() {
            return null;
        }

        @Override
        public IWebElementWrapper scrollIntoView() {
            return null;
        }

        @Override
        public IWebElementWrapper scrollIntoView(boolean bAlignToTop) {
            return null;
        }

        @Override
        public IWebElementWrapper setAttribute(String attribute, String value) {
            return null;
        }

        @Override
        public IWebElementWrapper removeAttribute(String attribute) {
            return null;
        }

        @Override
        public long getCellRow() {
            return 0;
        }

        @Override
        public long getCellColumn() {
            return 0;
        }

        @Override
        public long getRow() {
            return 0;
        }

        @Override
        public long getRowCount() {
            return 0;
        }

        @Override
        public IWebDriverWrapper switchTo() {
            return null;
        }
    }

    public class FluentAssert implements IFluentAssert {

        @Override
        public AbstractBooleanAssert<?> alertExists() {
            return null;
        }

        @Override
        public AbstractCharSequenceAssert<?, String> alertText() {
            return null;
        }

        @Override
        public AbstractCharSequenceAssert<?, String> pageTitle() {
            return null;
        }

        @Override
        public AbstractBooleanAssert<?> pageSourceContains(String text) {
            return null;
        }
    }

    public class AlertWrapper implements IAlertWrapper {
        @Override
        public void dismiss() {

        }

        @Override
        public void accept() {

        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public boolean exists() {
            return false;
        }
    }

    public class ActionsWrapper implements IActionsWrapper {
        @Override
        public IActionsWrapper click() {
            return this;
        }

        @Override
        public IActionsWrapper click(Locator locator) {
            return this;
        }

        @Override
        public IActionsWrapper doubleClick(Locator locator) {
            return this;
        }

        @Override
        public IActionsWrapper sendKeys(Locator locator, CharSequence... keysToSend) {
            return this;
        }

        @Override
        public IActionsWrapper sendKeys(CharSequence... keysToSend) {
            return this;
        }

        @Override
        public IActionsWrapper moveToElement(Locator locator) {
            return this;
        }

        @Override
        public Action build() {
            return new Action() {

                @Override
                public void perform() {

                }
            };
        }

        @Override
        public void perform() {

        }
    }
}