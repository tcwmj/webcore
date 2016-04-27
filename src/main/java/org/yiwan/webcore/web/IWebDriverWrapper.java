package org.yiwan.webcore.web;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.yiwan.webcore.locator.Locator;

import java.awt.*;
import java.util.Set;

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
     * close all browser tabs
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

    IAlertWrapper alert();
}
