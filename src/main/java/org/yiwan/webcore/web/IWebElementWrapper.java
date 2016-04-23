package org.yiwan.webcore.web;

import org.openqa.selenium.WebElement;
import org.yiwan.webcore.locator.Locator;

import java.util.List;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IWebElementWrapper {
    IWebElementWrapper click();

    IWebElementWrapper silentClick();

    IWebElementWrapper forcedClick();

    boolean smartClick();

    IWebElementWrapper jsClick();

    IWebElementWrapper loopClick() throws InterruptedException;

    IWebElementWrapper doubleClick();

    IWebElementWrapper type(CharSequence... value);

    IWebElementWrapper clear();

    IWebElementWrapper input(String value);

    boolean smartInput(String value);

    IWebElementWrapper ajaxInput(String value, Locator ajaxLocator);

    IWebElementWrapper tick(boolean value);

    boolean isTicked();

    IWebElementWrapper alteredTick(boolean value);

    IWebElementWrapper selectByVisibleText(String text);

    IWebElementWrapper deselectAll();

    IWebElementWrapper selectByVisibleText(List<String> texts);

    IWebElementWrapper selectByIndex(int index);

    IWebElementWrapper selectByValue(String value);

    boolean isTextSelectable(String text);

    IWebElementWrapper moveTo();

    boolean isPresent();

    boolean isEnabled();

    boolean isDisplayed();

    boolean isSelected();

    String getAttribute(String attribute);

    String getCssValue(String attribute);

    String getInnerText();

    List<String> getAllTexts();

    IWebElementWrapper setText(String text);

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
