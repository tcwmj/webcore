package org.yiwan.webcore.testng;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.yiwan.webcore.util.PropHelper;

/**
 * @author Kenny Wang
 * 
 */
public class RetryAnalyzer implements IRetryAnalyzer {
	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory
			.getLogger(RetryAnalyzer.class);

	private static final int MAX_RETRY_COUNT = PropHelper.TEST_RETRY_COUNT;
	private int retryCount = 0;

	@Override
	public synchronized boolean retry(ITestResult testResult) {
		if (retryCount < MAX_RETRY_COUNT) {
			retryCount++;
			return true;
		}

		// set the skip test to true on such test case instance
		Method method;
		try {
			method = testResult.getInstance().getClass()
					.getMethod("setSkipTest", Boolean.class);
			method.invoke(testResult.getInstance(), true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return false;
	}
}
