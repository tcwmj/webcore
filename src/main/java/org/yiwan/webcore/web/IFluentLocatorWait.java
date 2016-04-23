package org.yiwan.webcore.web;

import org.openqa.selenium.WebElement;
import org.yiwan.webcore.locator.Locator;

import java.util.List;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IFluentLocatorWait {
    IFluentLocatorWait toBePresentIn(int milliseconds);

    IFluentLocatorWait toBeAbsentIn(int milliseconds);

    List<WebElement> toBeAllPresent();

    WebElement toBePresent();

    WebElement toBeClickable();

    WebElement toBeVisible();

    List<WebElement> toBeAllVisible();

    Boolean toBeAbsent();

    Boolean toBeAllAbsent();

    Boolean toBeInvisible();

    Boolean toBeAllInvisible();

    Boolean toBeSelected();

    Boolean toBeNotSelected();

    IWebDriverWrapper frameToBeAvailableAndSwitchToIt();

    IFluentStringWait innerText();

    IFluentStringWait attributeValueOf(String attribute);

    IFluentStringWait cssValueOf(String cssAttribute);

    IFluentNumberWait numberOfElements();

    IFluentLocatorWait nestedElements(Locator locator);
}
