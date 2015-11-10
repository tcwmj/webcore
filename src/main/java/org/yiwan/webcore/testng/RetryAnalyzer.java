package org.yiwan.webcore.testng;

import java.lang.reflect.Method;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.yiwan.webcore.util.Property;

/**
 * @author Kenny Wang
 * 
 */
public class RetryAnalyzer implements IRetryAnalyzer {
	private static final int MAX_RETRY_COUNT = Property.RETRY_COUNT;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
}
