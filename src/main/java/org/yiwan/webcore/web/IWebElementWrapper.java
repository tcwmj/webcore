package org.yiwan.webcore.web;

import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IWebElementWrapper {
    IWebElementWrapper click();

    IWebElementWrapper clickSilently();

    IWebElementWrapper clickForcedly();

    boolean clickSmartly();

    IWebElementWrapper clickByJavaScript();

    IWebElementWrapper clickCircularly() throws InterruptedException;

    IWebElementWrapper doubleClick();

    IWebElementWrapper type(CharSequence... value);

    IWebElementWrapper clear();

    IWebElementWrapper input(String value);

    boolean inputSmartly(String value);

    IWebElementWrapper check(boolean checked);

    IWebElementWrapper checkByJavaScript(boolean checked);

    boolean isChecked();

    IWebElementWrapper selectByVisibleText(String text);

    IWebElementWrapper deselectAll();

    IWebElementWrapper deselectByVisibleText(String text);

    IWebElementWrapper deselectByVisibleText(String... texts);

    IWebElementWrapper deselectByIndex(int index);

    IWebElementWrapper deselectByValue(String value);

    IWebElementWrapper selectByVisibleText(String... texts);

    IWebElementWrapper selectByIndex(int index);

    IWebElementWrapper selectByValue(String value);

    boolean isSelectable(String text);

    IWebElementWrapper moveTo();

    boolean isPresent();

    boolean isEnabled();

    boolean isDisplayed();

    boolean isSelected();

    String getAttribute(String attribute);

    String getCssValue(String attribute);

    String getInnerText();

    List<String> getAllInnerTexts();

    IWebElementWrapper setInnerText(String text);

    IWebElementWrapper setValue(String value);

    List<WebElement> getAllSelectedOptions();

    List<WebElement> getAllOptions();

    List<String> getAllOptionTexts();

    String getSelectedText();

    List<String> getAllSelectedTexts();

    IWebElementWrapper triggerEvent(String event);

    IWebElementWrapper fireEvent(String event);

    IWebElementWrapper scrollTo();

    IWebElementWrapper scrollIntoView();

    IWebElementWrapper scrollIntoView(boolean bAlignToTop);

    IWebElementWrapper setAttribute(String attribute, String value);

    IWebElementWrapper removeAttribute(String attribute);

    long getCellRow();

    long getCellColumn();

    long getRow();

    long getRowCount();

    IWebDriverWrapper switchTo();

    int getNumberOfMatches();
}
