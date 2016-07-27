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
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IWebDriverWrapper {
    IBrowseNavigation navigate();

    /**
     * maximize browser window
     */
    IWebDriverWrapper maximize();

    /**
     * close current browser tab
     */
    IWebDriverWrapper close();

    /**
     * close all browser tabs<br/>
     * this method will cause network connection can't be reset after closing
     */
    IWebDriverWrapper closeAll();

    /**
     * quit driver
     */
    IWebDriverWrapper quit();

    /**
     * delete all cookies
     */
    IWebDriverWrapper deleteAllCookies();

    /**
     * get page source of current page
     *
     * @return page source string
     */
    String getPageSource();

    /**
     * get current url address
     *
     * @return string value of current url
     */
    String getCurrentUrl();

    Set<Cookie> getCookies();

    /**
     * switch to a window with a specified name or handle
     *
     * @param nameOrHandle
     */
    IWebDriverWrapper switchToWindow(String nameOrHandle);

    /**
     * Switch to default content from a frame
     */
    IWebDriverWrapper switchToDefaultWindow();

    IWebDriverWrapper switchToFrame(int index);

    IWebDriverWrapper switchToFrame(String nameOrId);

    /**
     * get current page title
     *
     * @return title string
     */
    String getPageTitle();

    /**
     * click element if it's displayed, otherwise click the next one
     *
     * @param locators
     */
    IWebDriverWrapper clickSmartly(Locator... locators);

    /**
     * input value in the first locator if it exists, otherwise input the next one
     *
     * @param value
     * @param locators
     */
    IWebDriverWrapper inputSmartly(String value, Locator... locators);

    /**
     * capture screenshot for local or remote testing
     *
     * @return screenshot
     */
    <X> X getScreenshotAs(OutputType<X> target);

    /**
     * @param key
     */
    IWebDriverWrapper typeKeyEvent(int key) throws AWTException;

    IActionsWrapper actions();

    Object executeScript(String script, Object... args);

    Object executeAsyncScript(String script, Object... args);

    IWebElementWrapper element(Locator locator);

    IFluentLocatorWait waitThat(Locator locator);

    IFluentWait waitThat();

    IFluentLocatorAssert assertThat(Locator locator);

    IFluentAssert assertThat();

    IFluentLocatorAssert validateThat(Locator locator);

    IFluentAssert validateThat();

    void validateAll();

    IAlertWrapper alert();

    interface IActionsWrapper {
        IActionsWrapper click();

        IActionsWrapper click(Locator locator);

        IActionsWrapper clickAndHold();

        IActionsWrapper clickAndHold(Locator locator);

        IActionsWrapper contextClick();

        IActionsWrapper contextClick(Locator locator);

        IActionsWrapper release();

        IActionsWrapper release(Locator locator);

        IActionsWrapper doubleClick();

        IActionsWrapper doubleClick(Locator locator);

        IActionsWrapper dragAndDrop(Locator source, Locator target);

        IActionsWrapper dragAndDrop(Locator source, int xOffset, int yOffset);

        IActionsWrapper keyDown(Keys theKey);

        IActionsWrapper keyDown(Locator locator, Keys theKey);

        IActionsWrapper keyUp(Keys theKey);

        IActionsWrapper keyUp(Locator locator, Keys theKey);

        IActionsWrapper sendKeys(Locator locator, CharSequence... keysToSend);

        IActionsWrapper sendKeys(CharSequence... keysToSend);

        IActionsWrapper moveTo(Locator locator);

        IActionsWrapper moveTo(Locator locator, int xOffset, int yOffset);

        IActionsWrapper moveTo(int xOffset, int yOffset);

        Action build();

        void perform();
    }

    interface IAlertWrapper {
        void dismiss();

        void accept();

        String getText();

        boolean isPresent();

        /**
         * disable javascript alert by accepting all of them
         */
        void disable();

        /**
         * disable javascript alert
         *
         * @param accept true indicates accept all
         */
        void disable(boolean accept);

        /**
         * enable javascript alert
         */
        void enable();
    }

    interface IBrowseNavigation {
        IBrowseNavigation to(String url);

        IBrowseNavigation forward();

        IBrowseNavigation backward();

        IBrowseNavigation refresh();
    }

    interface IFluentAlertAssert {
        AbstractBooleanAssert<?> present();

        AbstractCharSequenceAssert<?, String> text();

    }

    interface IFluentAlertWait {
        IAlertWrapper toBePresent();
    }

    interface IFluentAssert {
        IFluentAlertAssert alert();

        IFluentPageAssert page();
    }

    interface IFluentDocumentWait {
        Boolean toBeComplete();
    }

    interface IFluentJQueryWait {
        boolean isJQuerySupported();

        Boolean toBeInactive();
    }

    interface IFluentLocatorAssert {
        AbstractListAssert<? extends AbstractListAssert, ? extends java.util.List, String> allSelectedTexts();

        AbstractCharSequenceAssert<?, String> selectedText();

        AbstractListAssert<? extends AbstractListAssert, ? extends java.util.List, String> allOptionTexts();

        AbstractBooleanAssert<?> present();

        AbstractBooleanAssert<?> enabled();

        AbstractBooleanAssert<?> displayed();

        AbstractBooleanAssert<?> selected();

        AbstractCharSequenceAssert<?, String> innerText();

        AbstractListAssert<? extends AbstractListAssert, ? extends java.util.List, String> allInnerTexts();

        AbstractCharSequenceAssert<?, String> attributeValueOf(String attribute);

        AbstractCharSequenceAssert<?, String> cssValueOf(String cssAttribute);

        AbstractIntegerAssert<? extends AbstractIntegerAssert<?>> numberOfElements();

        IFluentLocatorAssert nestedElements(Locator locator);
    }

    interface IFluentLocatorWait {
        /**
         * wait the specified locator to be present
         *
         * @param milliseconds timeout
         */
        IFluentLocatorWait toBePresentIn(long milliseconds);

        /**
         * wait the specified locator to be absent
         *
         * @param milliseconds timeout
         */
        IFluentLocatorWait toBeAbsentIn(long milliseconds);

        IFluentLocatorWait toBeAppearedIn(long milliseconds);

        IFluentLocatorWait toBeDisappearedIn(long milliseconds);

        java.util.List<WebElement> toBeAllPresent();

        WebElement toBePresent();

        WebElement toBeEnabled();

        WebElement toBeClickable();

        WebElement toBeVisible();

        java.util.List<WebElement> toBeAllVisible();

        Boolean toBeAbsent();

        Boolean toBeInvisible();

        Boolean toBeSelected();

        Boolean toBeDeselected();

        IWebDriverWrapper frameToBeAvailableAndSwitchToIt();

        IFluentStringWait innerText();

        IFluentStringWait attributeValueOf(String attribute);

        IFluentStringWait cssValueOf(String cssAttribute);

        IFluentNumberWait numberOfElements();

        IFluentLocatorWait nestedElements(Locator locator);
    }

    interface IFluentNumberWait {

        Boolean equalTo(int number);

        Boolean notEqualTo(int number);

        Boolean lessThan(int number);

        Boolean greaterThan(int number);

        Boolean equalToOrLessThan(int number);

        Boolean equalToOrGreaterThan(int number);
    }

    interface IFluentPageAssert {
        AbstractCharSequenceAssert<?, String> title();

        AbstractCharSequenceAssert<?, String> source();

        AbstractCharSequenceAssert<?, String> url();
    }

    interface IFluentPageWait {
        IFluentStringWait title();

        IFluentStringWait source();

        IFluentStringWait url();
    }

    interface IFluentStringWait {

        Boolean toBe(String text);

        Boolean toBeEmpty();

        Boolean notToBe(String text);

        Boolean contains(String text);

        Boolean notContains(String text);

        Boolean startsWith(String text);

        Boolean endsWith(String text);

        Boolean matches(Pattern pattern);
    }

    interface IFluentWait {
        IFluentWait timeout(long milliseconds) throws InterruptedException;

        IFluentDocumentWait document();

        IFluentJQueryWait jQuery();

        IFluentAlertWait alert();

        IFluentPageWait page();
    }

    interface IWebElementWrapper {
        /**
         * click web element if it's clickable, please use this click method as default
         */
        IWebElementWrapper click();

        /**
         * click an element if it's displayed, otherwise skip this action
         *
         * @return boolean
         */
        boolean clickSmartly();

        /**
         * click a locator by javascript
         */
        IWebElementWrapper clickByJavaScript();

        /**
         * click a locator in a loop until it isn't displayed
         */
        IWebElementWrapper clickCircularly() throws InterruptedException;

        /**
         * double click web element if it's clickable
         */
        IWebElementWrapper doubleClick();

        IWebElementWrapper contextClick();

        IWebElementWrapper dragAndDrop(Locator target);

        IWebElementWrapper dragAndDrop(int xOffset, int yOffset);

        /**
         * Type value into the web edit box if it's visible
         *
         * @param value
         */
        IWebElementWrapper type(CharSequence... value);

        /**
         * Clear the content of the web edit box if it's visible
         */
        IWebElementWrapper clear();

        /**
         * clear the web edit box and input the value
         *
         * @param value
         */
        IWebElementWrapper input(String value);

        /**
         * input an element if it's displayed, otherwise skip this action
         *
         * @param value
         * @return boolean
         */
        boolean inputSmartly(String value);

        /**
         * check web check box on or off if it's visible
         *
         * @param checked on or off
         */
        IWebElementWrapper check(boolean checked);

        /**
         * using java script to check web check box on or off
         *
         * @param checked on or off
         */
        IWebElementWrapper checkByJavaScript(boolean checked);

        /**
         * web check box checked or not
         *
         * @return checked or not
         */
        boolean isChecked();

        /**
         * the same fucntion to method check, return true if it takes action
         *
         * @param checked on or off
         */
        boolean tick(boolean checked);

        /**
         * select all options
         *
         * @return the web element
         */
        IWebElementWrapper selectAll();

        /**
         * Select all options that display text matching the argument. That is, when
         * given "Bar" this would select an option like:
         * <p/>
         * &lt;option value="foo"&gt;Bar&lt;/option&gt;
         *
         * @param text The visible text to match against
         */
        IWebElementWrapper selectByVisibleText(String text);

        /**
         * Select all options that display text matching the argument. That is, when
         * given "Bar" this would select an option like:
         * <p/>
         * &lt;option value="foo"&gt;Bar&lt;/option&gt;
         *
         * @param texts The visible text to match against
         */
        IWebElementWrapper selectByVisibleText(String... texts);

        /**
         * Select the option at the given index. This is done locator examing the
         * "index" attributeValueOf of an element, and not merely locator counting.
         *
         * @param index The option at this index will be selected
         */
        IWebElementWrapper selectByIndex(int index);

        /**
         * Select all options that have a value matching the argument. That is, when
         * given "foo" this would select an option like:
         * <p/>
         * &lt;option value="foo"&gt;Bar&lt;/option&gt;
         *
         * @param value The value to match against
         */
        IWebElementWrapper selectByValue(String value);

        /**
         * Clear all selected entries. This is only valid when the SELECT supports
         * multiple selections.
         *
         * @throws UnsupportedOperationException If the SELECT does not support multiple selections
         */
        IWebElementWrapper deselectAll();

        IWebElementWrapper deselectByVisibleText(String text);

        IWebElementWrapper deselectByVisibleText(String... texts);

        IWebElementWrapper deselectByIndex(int index);

        IWebElementWrapper deselectByValue(String value);

        IWebElementWrapper moveTo();

        /**
         * whether locator is present or not
         *
         * @return whether locator is present or not
         */
        boolean isPresent();

        /**
         * whether locator is enabled or not
         *
         * @return boolean
         */
        boolean isEnabled();

        /**
         * whether locator is displayed or not
         *
         * @return boolean
         */
        boolean isDisplayed();

        /**
         * whether locator is selected or not
         *
         * @return boolean
         */
        boolean isSelected();

        /**
         * get value of specified attributeValueOf
         *
         * @param attribute
         * @return attributeValueOf value
         */
        String getAttribute(String attribute);

        /**
         * get css attributeValueOf value
         *
         * @param attribute
         * @return string
         */
        String getCssValue(String attribute);

        /**
         * get text on such web element
         *
         * @return string
         */
        String getInnerText();

        /**
         * set innert text on such web element by javascript
         *
         * @param text
         */
        IWebElementWrapper setInnerText(String text);

        /**
         * get all text on found locators
         *
         * @return text list
         */
        java.util.List<String> getAllInnerTexts();

        /**
         * set value on such web element by javascript, an alternative approach for method input
         *
         * @param value
         */
        IWebElementWrapper setValue(String value);

        /**
         * get all selected options in web list element
         *
         * @return List&gt;WebElement&lt;
         */
        java.util.List<WebElement> getAllSelectedOptions();

        /**
         * get all options in web list element
         *
         * @return List&gt;WebElement&lt;
         */
        java.util.List<WebElement> getAllOptions();

        /**
         * get all options text in web list element
         *
         * @return List&gt;String&lt;
         */
        java.util.List<String> getAllOptionTexts();

        /**
         * get first selected option's text in web list element
         *
         * @return string
         */
        String getSelectedText();

        /**
         * get all selected options' text in web list element
         *
         * @return List&gt;String&lt;
         */
        java.util.List<String> getAllSelectedTexts();

        /**
         * trigger an event on such element
         *
         * @param event String, such as "mouseover"
         */
        IWebElementWrapper triggerEvent(String event);

        /**
         * fire an event on such element
         *
         * @param event String, such as "onchange"
         */
        IWebElementWrapper fireEvent(String event);

        /**
         * Scroll page or scrollable element to a specific target element.
         */
        IWebElementWrapper scrollTo();

        /**
         * immediately showing the user the result of some action without requiring
         * the user to manually scroll through the document to find the result
         * Scrolls the object so that top of the object is visible at the top of the
         * window.
         */
        IWebElementWrapper scrollIntoView();

        /**
         * immediately showing the user the result of some action without requiring
         * the user to manually scroll through the document to find the result
         *
         * @param bAlignToTop true Default. Scrolls the object so that top of the object is
         *                    visible at the top of the window. <br/>
         *                    false Scrolls the object so that the bottom of the object is
         *                    visible at the bottom of the window.
         */
        IWebElementWrapper scrollIntoView(boolean bAlignToTop);

        /**
         * using java script to set element attributeValueOf
         *
         * @param attribute
         * @param value
         */
        IWebElementWrapper setAttribute(String attribute, String value);

        /**
         * using java script to remove element attributeValueOf
         *
         * @param attribute
         */
        IWebElementWrapper removeAttribute(String attribute);

        /**
         * using java script to get row number of cell element "/td" in web table
         */
        long getCellRow();

        /**
         * using java script to get column number of cell element "/td" in web table
         */
        long getCellColumn();

        /**
         * using java script to get row number of row element "/tr" in web table
         */
        long getRow();

        /**
         * using java script to get row count of web table "/table"
         *
         * @return long
         */
        long getRowCount();

        IWebDriverWrapper switchTo();

        int getNumberOfMatches();

        /**
         * get all matched web elements
         *
         * @return List&gt;WebElement&lt;
         */
        java.util.List<IWebElementWrapper> getAllMatchedElements();
    }

}
