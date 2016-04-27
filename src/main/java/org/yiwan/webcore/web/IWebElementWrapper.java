package org.yiwan.webcore.web;

import org.openqa.selenium.WebElement;
import org.yiwan.webcore.locator.Locator;

import java.util.List;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IWebElementWrapper {
    /**
     * click web element if it's clickable, please use this click method as default
     */
    IWebElementWrapper click();

    /**
     * click element without considering anything, it may raise unexpected exception
     */
    IWebElementWrapper clickSilently();

    /**
     * forced to click element even if it's not clickable, it may raise unexpected exception, please use method click as default
     */
    IWebElementWrapper clickForcedly();

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
     * select all options
     *
     * @return
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
    List<String> getAllInnerTexts();

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
    List<WebElement> getAllSelectedOptions();

    /**
     * get all options in web list element
     *
     * @return List&gt;WebElement&lt;
     */
    List<WebElement> getAllOptions();

    /**
     * get all options text in web list element
     *
     * @return List&gt;String&lt;
     */
    List<String> getAllOptionTexts();

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
    List<String> getAllSelectedTexts();

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
}
