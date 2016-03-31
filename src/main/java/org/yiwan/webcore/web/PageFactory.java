package org.yiwan.webcore.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.TestBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PageFactory {

    private final static Logger logger = LoggerFactory.getLogger(PageFactory.class);

    /*private WebDriver driver;

    public PageFactory(TestBase testCase) {
        this.driver = testCase.getWebDriver();
    }
    
    public PageFactory(WebDriver driver) {
        this.driver = driver;
    }

    @SuppressWarnings("unchecked")
    public <T extends WebDriverWrapper> T createPage(Class<?> clazz) {
        Constructor<?> c = null;
        c = clazz.getDeclaredConstructor(WebDriver.class);
        c.setAccessible(true);
        return (T) c.newInstance(driver);
    }*/

    private TestBase testCase;

    public PageFactory(TestBase testCase) {
        this.testCase = testCase;
    }

    public <T extends WebDriverWrapper> T createPage(Class<T> clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Constructor<T> c = null;
        c = clazz.getDeclaredConstructor(TestBase.class);
        assert c != null;
        c.setAccessible(true);
        return (T) c.newInstance(testCase);
    }
}
