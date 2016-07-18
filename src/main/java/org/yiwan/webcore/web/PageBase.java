package org.yiwan.webcore.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.locator.Locator;
import org.yiwan.webcore.locator.LocatorBean;
import org.yiwan.webcore.util.JaxbHelper;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IWebDriverWrapper.*;

/**
 * Created by Kenny Wang on 4/2/2016.
 */
public class PageBase {
    private final static LocatorBean LOCATOR_BEAN = JaxbHelper.unmarshal(ClassLoader.getSystemResourceAsStream(PropHelper.LOCATORS_FILE), ClassLoader.getSystemResourceAsStream(PropHelper.LOCATOR_SCHEMA), LocatorBean.class);
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private IWebDriverWrapper webDriverWrapper;

    public PageBase(IWebDriverWrapper webDriverWrapper) {
        this.webDriverWrapper = webDriverWrapper;
    }

    public IWebDriverWrapper getWebDriverWrapper() {
        return webDriverWrapper;
    }

    public IBrowseNavigation navigate() {
        return webDriverWrapper.navigate();
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
    protected IWebDriverWrapper switchToWindow(String nameOrHandle) {
        return webDriverWrapper.switchToWindow(nameOrHandle);
    }

    /**
     * Switch to default content from a frame
     */
    protected IWebDriverWrapper switchToDefaultWindow() {
        return webDriverWrapper.switchToDefaultWindow();
    }


    protected IWebDriverWrapper switchToFrame(int index) {
        return webDriverWrapper.switchToFrame(index);
    }

    protected IWebDriverWrapper switchToFrame(String nameOrId) {
        return webDriverWrapper.switchToFrame(nameOrId);
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
    protected IWebDriverWrapper clickSmartly(Locator... locators) {
        return webDriverWrapper.clickSmartly(locators);
    }

    /**
     * input value in the first locator if it exists, otherwise input the next one
     *
     * @param value
     * @param locators
     */
    public IWebDriverWrapper inputSmartly(String value, Locator... locators) {
        return webDriverWrapper.inputSmartly(value, locators);
    }

    protected Object executeScript(String script, Object... args) {
        return webDriverWrapper.executeScript(script, args);
    }

    protected Object executeAsyncScript(String script, Object... args) {
        return webDriverWrapper.executeAsyncScript(script, args);
    }

    protected IActionsWrapper actions() {
        return webDriverWrapper.actions();
    }

    protected Locator locator(String id, String... replacements) throws Exception {
        return LOCATOR_BEAN.locator(id, replacements);
    }

    protected IWebElementWrapper element(String id, String... replacements) throws Exception {
        return element(LOCATOR_BEAN.locator(id, replacements));
    }

    protected IWebElementWrapper element(Locator locator) {
        return webDriverWrapper.element(locator);
    }

    protected IFluentLocatorWait waitThat(String id, String... replacements) throws Exception {
        return waitThat(LOCATOR_BEAN.locator(id, replacements));
    }

    protected IFluentLocatorWait waitThat(Locator locator) {
        return webDriverWrapper.waitThat(locator);
    }

    protected IFluentWait waitThat() {
        return webDriverWrapper.waitThat();
    }

    protected IFluentLocatorAssert assertThat(String id, String... replacements) throws Exception {
        return webDriverWrapper.assertThat(LOCATOR_BEAN.locator(id, replacements));
    }

    protected IFluentLocatorAssert assertThat(Locator locator) {
        return webDriverWrapper.assertThat(locator);
    }

    protected IFluentAssert assertThat() {
        return webDriverWrapper.assertThat();
    }

    protected IFluentLocatorAssert validateThat(Locator locator) {
        return webDriverWrapper.validateThat(locator);
    }

    protected IFluentAssert validateThat() {
        return webDriverWrapper.validateThat();
    }

    protected IAlertWrapper alert() {
        return webDriverWrapper.alert();
    }
}
