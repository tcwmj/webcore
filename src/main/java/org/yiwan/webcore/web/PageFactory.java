package org.yiwan.webcore.web;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.util.TestBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PageFactory {

    private final static Logger logger = LoggerFactory.getLogger(PageFactory.class);
    private WebDriver driver;

    public PageFactory(TestBase testBase) {
        this.driver = testBase.getDriver();
    }
    
    public PageFactory(WebDriver driver) {
        this.driver = driver;
    }

    @SuppressWarnings("unchecked")
    public <T extends WebDriverWrapper> T newPage(Class<?> clazz) {
        Constructor<?> c = null;
        try {
            c = clazz.getDeclaredConstructor(WebDriver.class);
        } catch (NoSuchMethodException | SecurityException e) {
            logger.error(e.getMessage(), e);
        }
        c.setAccessible(true);
        try {
            return (T) c.newInstance(driver);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
