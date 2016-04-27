package org.yiwan.webcore.web;

import org.openqa.selenium.WebElement;
import org.yiwan.webcore.locator.Locator;

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

    IWebElementWrapper contextClick();

    IWebElementWrapper dragAndDrop(Locator target);

    IWebElementWrapper dragAndDrop(int xOffset, int yOffset);

    IWebElementWrapper type(CharSequence... value);

    IWebElementWrapper clear();

    IWebElementWrapper input(String value);

    boolean inputSmartly(String value);

    IWebElementWrapper check(boolean checked);

    IWebElementWrapper checkByJavaScript(boolean checked);

    boolean isChecked();

    IWebElementWrapper selectAll();

    IWebElementWrapper selectByVisibleText(String text);

    IWebElementWrapper selectByVisibleText(String... texts);

    IWebElementWrapper selectByIndex(int index);

    IWebElementWrapper selectByValue(String value);

    IWebElementWrapper deselectAll();

    IWebElementWrapper deselectByVisibleText(String text);

    IWebElementWrapper deselectByVisibleText(String... texts);

    IWebElementWrapper deselectByIndex(int index);

    IWebElementWrapper deselectByValue(String value);

    IWebElementWrapper moveTo();

    boolean isPresent();

    boolean isEnabled();

    boolean isDisplayed();

    boolean isSelected();

    String getAttribute(String attribute);

    String getCssValue(String attribute);

    String getInnerText();

    IWebElementWrapper setInnerText(String text);

    List<String> getAllInnerTexts();

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
