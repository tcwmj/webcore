package org.yiwan.webcore.web;

import org.openqa.selenium.OutputType;
import org.yiwan.webcore.locator.Locator;

import java.awt.*;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IWebDriverWrapper {
    IBrowseNavigation navigate();

    IWebDriverWrapper maximize();

    IWebDriverWrapper close();

    IWebDriverWrapper closeAll();

    IWebDriverWrapper quit();

    IWebDriverWrapper deleteAllCookies();

    boolean isPageSourceContains(String text);

    String getPageSource();

    String getCurrentUrl();

    IWebDriverWrapper switchToWindow(String nameOrHandle);

    IWebDriverWrapper switchToDefaultWindow();

    IWebDriverWrapper switchToFrame(int index);

    IWebDriverWrapper switchToFrame(String nameOrId);

    String getPageTitle();

    IWebDriverWrapper clickSmartly(Locator... locators);

    IWebDriverWrapper inputSmartly(String value, Locator... locators);

    <X> X getScreenshotAs(OutputType<X> target);

    IWebDriverWrapper typeKeyEvent(int key) throws AWTException;

    IActionsWrapper actions();

    Object executeScript(String script, Object... args);

    Object executeAsyncScript(String script, Object... args);

    IWebElementWrapper element(Locator locator);

    IFluentLocatorWait waitThat(Locator locator);

    IFluentWait waitThat();

    IFluentLocatorAssert assertThat(Locator locator);

    IFluentAssert assertThat();

    IAlertWrapper alert();
}
