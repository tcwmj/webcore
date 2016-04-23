package org.yiwan.webcore.web;

import org.openqa.selenium.WebElement;

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

    Boolean toBeAbsent();

    Boolean toBeInvisible();

    Boolean toBeSelected();

    IWebDriverWrapper frameToBeAvailableAndSwitchToIt();

    IFluentStringWait innerText();

    IFluentStringWait attributeValueOf(String attribute);

    IFluentStringWait cssValueOf(String cssAttribute);
}
