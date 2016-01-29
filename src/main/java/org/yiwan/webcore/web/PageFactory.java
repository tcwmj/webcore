package org.yiwan.webcore.web;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.util.TestBase;

public class PageFactory {

	private final static Logger logger = LoggerFactory.getLogger(PageFactory.class);
	private TestBase testcase;

	public PageFactory(TestBase testcase) {
		this.testcase = testcase;
	}

	public <T> Object newPage(Class<?> clazz) {
		Constructor<?> c = null;
		try {
			c = clazz.getDeclaredConstructor(TestBase.class);
		} catch (NoSuchMethodException | SecurityException e) {
			logger.error(e.getMessage(), e);
		}
		c.setAccessible(true);
		try {
			return c.newInstance(testcase);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
