package org.yiwan.webcore.web;

import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IFluentLocatorWait {
    Boolean hasInnerText(String text);

    Boolean toBeInvisible();

    WebElement toBePresent();

    List<WebElement> toBeAllPresent();

    Boolean toBeAbsent();

    IFluentLocatorWait toBePresentIn(int milliseconds);

    IFluentLocatorWait toBeAbsentIn(int milliseconds);

    WebElement toBeClickable();

    WebElement toBeVisible();

    IWebDriverWrapper frameToBeAvailableAndSwitchToIt();

    IFluentLocatorAttributeWait attribute(String attribute);

    IFluentLocatorCssAttributeWait cssAttribute(String cssAttribute);
}
