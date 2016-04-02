package org.yiwan.webcore.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.locator.Locator;
import org.yiwan.webcore.locator.LocatorBean;
import org.yiwan.webcore.util.JaxbHelper;
import org.yiwan.webcore.util.PropHelper;

/**
 * Created by Kenny Wang on 4/2/2016.
 */
public class PageBase {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final static LocatorBean l = JaxbHelper.unmarshal(ClassLoader.getSystemResourceAsStream(PropHelper.LOCATORS_FILE), ClassLoader.getSystemResourceAsStream(PropHelper.LOCATOR_SCHEMA), LocatorBean.class);
    private WebDriverWrapper webDriverWrapper;

    public PageBase(WebDriverWrapper webDriverWrapper) {
        this.webDriverWrapper = webDriverWrapper;
    }

    public WebDriverWrapper getWebDriverWrapper() {
        return webDriverWrapper;
    }

    public void browse(String url) {
        webDriverWrapper.browse(url);
    }

    public void forward() {
        webDriverWrapper.forward();
    }

    /**
     * navigate back
     */
    public void backward() {
        webDriverWrapper.backward();
    }

    /**
     * is page source contains such text
     *
     * @param text
     * @return boolean
     */
    protected boolean isPageSourceContains(String text) {
        return webDriverWrapper.isPageSourceContains(text);
    }

    /**
     * get page source of current page
     *
     * @return page source string
     */
    protected String getPageSource() {
        return webDriverWrapper.getPageSource();
    }

    /**
     * get current url address
     *
     * @return string value of current url
     */
    protected String getCurrentUrl() {
        return webDriverWrapper.getCurrentUrl();
    }

    /**
     * switch to a window with a specified name or handle
     *
     * @param nameOrHandle
     */
    protected void switchToWindow(String nameOrHandle) {
        webDriverWrapper.switchToWindow(nameOrHandle);
    }

    /**
     * Switch to default content from a frame
     */
    protected void switchToDefaultWindow() {
        webDriverWrapper.switchToDefaultWindow();
    }

    /**
     * get current page title
     *
     * @return string value of title
     */
    protected String getPageTitle() {
        return webDriverWrapper.getPageTitle();
    }

    /**
     * click element if it's displayed, otherwise click the next one
     */
    protected void smartClick(Locator... locators) {
        webDriverWrapper.smartClick(locators);
    }

    /**
     * input value in the first locator if it exists, or the second locator if the first doesn't exist
     *
     * @param locator1
     * @param locator2
     * @param value
     */
    protected void smartInput(Locator locator1, Locator locator2, String value) {
        webDriverWrapper.smartInput(locator1, locator2, value);
    }

    protected Object executeScript(String script, Object... args) {
        return webDriverWrapper.executeScript(script, args);
    }

    protected Object executeAsyncScript(String script, Object... args) {
        return webDriverWrapper.executeAsyncScript(script, args);
    }

    protected WebDriverWrapper.ActionsWrapper actions() {
        return webDriverWrapper.actions();
    }

    protected WebDriverWrapper.WebElementWrapper element(Locator locator) {
        return webDriverWrapper.element(locator);
    }

    protected WebDriverWrapper.FluentLocatorWait waitThat(Locator locator) {
        return webDriverWrapper.waitThat(locator);
    }

    protected WebDriverWrapper.FluentWait waitThat() {
        return webDriverWrapper.waitThat();
    }

    protected WebDriverWrapper.FluentLocatorAssert assertThat(Locator locator) {
        return webDriverWrapper.assertThat(locator);
    }

    protected WebDriverWrapper.FluentAssert assertThat() {
        return webDriverWrapper.assertThat();
    }

    protected WebDriverWrapper.AlertWrapper alert() {
        return webDriverWrapper.alert();
    }
}
