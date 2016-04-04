package org.yiwan.webcore.web;

import org.openqa.selenium.TakesScreenshot;
import org.yiwan.webcore.locator.Locator;

import java.awt.*;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IWebDriverWrapper {
    IWebDriverWrapper browse(String url);

    IWebDriverWrapper forward();

    IWebDriverWrapper backward();

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

    IWebDriverWrapper smartClick(Locator... locators);

    IWebDriverWrapper smartInput(String value, Locator... locators);

    TakesScreenshot getTakesScreenshot();

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
