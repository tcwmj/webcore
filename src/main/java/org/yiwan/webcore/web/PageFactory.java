package org.yiwan.webcore.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public class PageFactory {
    private final static Logger logger = LoggerFactory.getLogger(PageFactory.class);
    private IWebDriverWrapper webDriverWrapper;

    public PageFactory(IWebDriverWrapper webDriverWrapper) {
        this.webDriverWrapper = webDriverWrapper;
    }

    public <T extends PageBase> T create(Class<T> clazz) throws Exception {
        Constructor<T> c = clazz.getDeclaredConstructor(IWebDriverWrapper.class);
        c.setAccessible(true);
        return c.newInstance(webDriverWrapper);
    }
}
