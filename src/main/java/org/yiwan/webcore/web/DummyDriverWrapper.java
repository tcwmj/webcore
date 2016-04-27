package org.yiwan.webcore.web;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractListAssert;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.yiwan.webcore.locator.Locator;

import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public class DummyDriverWrapper implements IWebDriverWrapper {
    @Override
    public IBrowseNavigation navigate() {
        return new IBrowseNavigation() {
            @Override
            public IBrowseNavigation to(String url) {
                return null;
            }

            @Override
            public IBrowseNavigation forward() {
                return null;
            }

            @Override
            public IBrowseNavigation backward() {
                return null;
            }
        };
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
    public String getPageSource() {
        return null;
    }

    @Override
    public String getCurrentUrl() {
        return null;
    }

    @Override
    public Set<Cookie> getCookies() {
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
    public IWebDriverWrapper clickSmartly(Locator... locators) {
        return this;
    }

    @Override
    public IWebDriverWrapper inputSmartly(String value, Locator... locators) {
        return this;
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) {
        return null;
    }

    @Override
    public IWebDriverWrapper typeKeyEvent(int key) throws AWTException {
        return this;
    }

    @Override
    public IActionsWrapper actions() {
        return new IActionsWrapper() {
            @Override
            public IActionsWrapper click() {
                return this;
            }

            @Override
            public IActionsWrapper click(Locator locator) {
                return this;
            }

            @Override
            public IActionsWrapper clickAndHold() {
                return null;
            }

            @Override
            public IActionsWrapper clickAndHold(Locator locator) {
                return null;
            }

            @Override
            public IActionsWrapper contextClick() {
                return null;
            }

            @Override
            public IActionsWrapper contextClick(Locator locator) {
                return null;
            }

            @Override
            public IActionsWrapper release() {
                return null;
            }

            @Override
            public IActionsWrapper release(Locator locator) {
                return null;
            }

            @Override
            public IActionsWrapper doubleClick() {
                return null;
            }

            @Override
            public IActionsWrapper doubleClick(Locator locator) {
                return this;
            }

            @Override
            public IActionsWrapper dragAndDrop(Locator source, Locator target) {
                return null;
            }

            @Override
            public IActionsWrapper dragAndDrop(Locator source, int xOffset, int yOffset) {
                return null;
            }

            @Override
            public IActionsWrapper keyDown(Keys theKey) {
                return null;
            }

            @Override
            public IActionsWrapper keyDown(Locator locator, Keys theKey) {
                return null;
            }

            @Override
            public IActionsWrapper keyUp(Keys theKey) {
                return null;
            }

            @Override
            public IActionsWrapper keyUp(Locator locator, Keys theKey) {
                return null;
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
            public IActionsWrapper moveTo(Locator locator) {
                return this;
            }

            @Override
            public IActionsWrapper moveTo(Locator locator, int xOffset, int yOffset) {
                return null;
            }

            @Override
            public IActionsWrapper moveTo(int xOffset, int yOffset) {
                return null;
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
        };
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
    public IWebElementWrapper element(final Locator locator) {
        return new IWebElementWrapper() {

            @Override
            public IWebElementWrapper click() {
                return null;
            }

            @Override
            public IWebElementWrapper clickSilently() {
                return null;
            }

            @Override
            public IWebElementWrapper clickForcedly() {
                return null;
            }

            @Override
            public boolean clickSmartly() {
                return false;
            }

            @Override
            public IWebElementWrapper clickByJavaScript() {
                return null;
            }

            @Override
            public IWebElementWrapper clickCircularly() throws InterruptedException {
                return null;
            }

            @Override
            public IWebElementWrapper doubleClick() {
                return null;
            }

            @Override
            public IWebElementWrapper contextClick() {
                return null;
            }

            @Override
            public IWebElementWrapper dragAndDrop(Locator target) {
                return null;
            }

            @Override
            public IWebElementWrapper dragAndDrop(int xOffset, int yOffset) {
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
            public boolean inputSmartly(String value) {
                return false;
            }

            @Override
            public IWebElementWrapper check(boolean checked) {
                return null;
            }


            @Override
            public boolean isChecked() {
                return false;
            }

            @Override
            public IWebElementWrapper selectAll() {
                return null;
            }

            @Override
            public IWebElementWrapper checkByJavaScript(boolean checked) {
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
            public IWebElementWrapper deselectByVisibleText(String text) {
                return null;
            }

            @Override
            public IWebElementWrapper deselectByVisibleText(String... texts) {
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

            @Override
            public IWebElementWrapper selectByVisibleText(String... texts) {
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
            public List<String> getAllInnerTexts() {
                return null;
            }

            @Override
            public IWebElementWrapper setInnerText(String text) {
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
            public List<String> getAllOptionTexts() {
                return null;
            }

            @Override
            public String getSelectedText() {
                return null;
            }

            @Override
            public List<String> getAllSelectedTexts() {
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

            @Override
            public int getNumberOfMatches() {
                return 0;
            }
        };
    }

    @Override
    public IFluentLocatorWait waitThat(final Locator locator) {
        return new IFluentLocatorWait() {

            @Override
            public IFluentStringWait innerText() {
                return null;
            }

            @Override
            public Boolean toBeInvisible() {
                return null;
            }

            @Override
            public Boolean toBeSelected() {
                return null;
            }

            @Override
            public Boolean toBeDeselected() {
                return null;
            }

            @Override
            public WebElement toBePresent() {
                return null;
            }

            @Override
            public WebElement toBeEnable() {
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
            public List<WebElement> toBeAllVisible() {
                return null;
            }

            @Override
            public IWebDriverWrapper frameToBeAvailableAndSwitchToIt() {
                return null;
            }

            @Override
            public IFluentStringWait attributeValueOf(String attribute) {
                return null;
            }

            @Override
            public IFluentStringWait cssValueOf(String cssAttribute) {
                return null;
            }

            @Override
            public IFluentNumberWait numberOfElements() {
                return null;
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

            }

            @Override
            public IFluentDocumentWait document() {
                return null;
            }

            @Override
            public IFluentAlertWait alert() {
                return null;
            }

            @Override
            public IFluentPageWait page() {
                return null;
            }


        };
    }

    @Override
    public IFluentLocatorAssert assertThat(final Locator locator) {
        return new IFluentLocatorAssert() {
            @Override
            public AbstractListAssert<? extends AbstractListAssert, ? extends List, String> allSelectedTexts() {
                return null;
            }

            @Override
            public AbstractListAssert<? extends AbstractListAssert, ? extends List, String> allOptionTexts() {
                return null;
            }

            @Override
            public AbstractBooleanAssert<?> present() {
                return null;
            }

            @Override
            public AbstractBooleanAssert<?> enabled() {
                return null;
            }

            @Override
            public AbstractBooleanAssert<?> displayed() {
                return null;
            }

            @Override
            public AbstractBooleanAssert<?> selected() {
                return null;
            }

            @Override
            public AbstractCharSequenceAssert<?, String> innerText() {
                return null;
            }

            @Override
            public AbstractListAssert<? extends AbstractListAssert, ? extends List, String> allInnerTexts() {
                return null;
            }

            @Override
            public AbstractCharSequenceAssert<?, String> attributeValueOf(String attribute) {
                return null;
            }

            @Override
            public AbstractCharSequenceAssert<?, String> cssValueOf(String cssAttribute) {
                return null;
            }

            @Override
            public AbstractIntegerAssert<? extends AbstractIntegerAssert<?>> numberOfElements() {
                return null;
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
            public IFluentAlertAssert alert() {
                return null;
            }

            @Override
            public IFluentPageAssert page() {
                return null;
            }
        };
    }

    @Override
    public IAlertWrapper alert() {
        return new IAlertWrapper() {
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
            public boolean present() {
                return false;
            }
        };
    }
}
