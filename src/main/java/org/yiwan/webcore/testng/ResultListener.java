package org.yiwan.webcore.testng;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;
import org.yiwan.webcore.util.Helper;
import org.yiwan.webcore.util.PropHelper;

/**
 * @author Kenny Wang
 * 
 */
public class ResultListener extends TestListenerAdapter {

	private final static Logger logger = LoggerFactory.getLogger(ResultListener.class);
	private XLSRuntimeReporter runtimeReporter;

	@Override
	public void onTestFailure(ITestResult testResult) {
		logger.info(testResult.getTestClass().getName() + "." + testResult.getName() + " failed");
		super.onTestFailure(testResult);

		Method method;
		try {
			method = testResult.getInstance().getClass().getMethod("getDriver");
			WebDriver driver = (WebDriver) method.invoke(testResult.getInstance());
			method = testResult.getInstance().getClass().getMethod("getScreenshotFolder");
			String saveTo = ((String) method.invoke(testResult.getInstance())) + testResult.getTestClass().getName()
					+ "." + testResult.getName() + ".png";
			captureScreenShot(driver, testResult, saveTo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		runtimeReporter.updateReport(testResult);
	}

	@Override
	public void onTestSkipped(ITestResult testResult) {
		super.onTestSkipped(testResult);
		runtimeReporter.updateReport(testResult);
	}

	@Override
	public void onTestSuccess(ITestResult testResult) {
		logger.info(testResult.getTestClass().getName() + "." + testResult.getName() + " passed");
		super.onTestSuccess(testResult);
		runtimeReporter.updateReport(testResult);
	}

	@Override
	public void onTestStart(ITestResult testResult) {
		super.onTestStart(testResult);

		// commented as there is too many log in the test reports
		// try {
		// Method method = testResult.getInstance().getClass()
		// .getMethod("report", String.class);
		// method.invoke(testResult.getInstance(), testResult.getName() + " "
		// + testResult.getMethod().getDescription());
		// } catch (Exception e) {
		// logger.error(e.getMessage(), e);
		// }

		runtimeReporter.updateReport(testResult);
	}

	@Override
	public void onStart(ITestContext testContext) {
		super.onStart(testContext);
		String fileName = testContext.getCurrentXmlTest().getName() + ".xls";
		runtimeReporter = new XLSRuntimeReporter(fileName);
		runtimeReporter.generateReport();
	}

	@Override
	public void onFinish(ITestContext testContext) {
		super.onFinish(testContext);

		// List of test results which we will delete later
		ArrayList<ITestResult> testsToBeRemoved = new ArrayList<ITestResult>();
		// collect all id's from passed test
		Set<Integer> passedTestIds = new HashSet<Integer>();
		for (ITestResult passedTest : testContext.getPassedTests().getAllResults()) {
			passedTestIds.add(getId(passedTest));
		}

		// Eliminate the repeat methods
		Set<Integer> skipTestIds = new HashSet<Integer>();
		for (ITestResult skipTest : testContext.getSkippedTests().getAllResults()) {
			// id = class + method + dataprovider
			int skipTestId = getId(skipTest);

			if (skipTestIds.contains(skipTestId) || passedTestIds.contains(skipTestId)) {
				testsToBeRemoved.add(skipTest);
			} else {
				skipTestIds.add(skipTestId);
			}
		}

		// Eliminate the repeat failed methods
		Set<Integer> failedTestIds = new HashSet<Integer>();
		for (ITestResult failedTest : testContext.getFailedTests().getAllResults()) {
			// id = class + method + dataprovider
			int failedTestId = getId(failedTest);

			// if we saw this test as a failed test before we mark as to be
			// deleted
			// or delete this failed test if there is at least one passed
			// version
			if (failedTestIds.contains(failedTestId) || passedTestIds.contains(failedTestId)
					|| skipTestIds.contains(failedTestId)) {
				testsToBeRemoved.add(failedTest);
			} else {
				failedTestIds.add(failedTestId);
			}
		}

		// finally delete all tests that are marked
		for (Iterator<ITestResult> iterator = testContext.getFailedTests().getAllResults().iterator(); iterator
				.hasNext();) {
			ITestResult testResult = iterator.next();
			if (testsToBeRemoved.contains(testResult)) {
				logger.info("try to remove retried failed iterator: " + testResult.getTestClass().getName() + "."
						+ testResult.getName());
				iterator.remove();
			}
		}
	}

	private int getId(ITestResult testResult) {
		int id = testResult.getTestClass().getName().hashCode();
		id = id + testResult.getMethod().getMethodName().hashCode();
		id = id + (testResult.getParameters() != null ? Arrays.hashCode(testResult.getParameters()) : 0);

		return id;
	}

	/**
	 * capture screenshot for local or remote testing
	 * 
	 * @param driver
	 * @param testResult
	 * @param saveTo
	 */
	private void captureScreenShot(WebDriver driver, ITestResult testResult, String saveTo) {
		TakesScreenshot ts;
		if (PropHelper.REMOTE)
			// RemoteWebDriver does not implement the TakesScreenshot class
			// if the driver does have the Capabilities to take a screenshot
			// then Augmenter will add the TakesScreenshot methods to the
			// instance
			ts = (TakesScreenshot) (new Augmenter().augment(driver));
		else
			ts = (TakesScreenshot) driver;

		try {
			File screenshot = ts.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot, new File(saveTo));
			Reporter.setCurrentTestResult(testResult);
			saveTo = saveTo.replaceAll("\\\\", "/");
			Method method = testResult.getInstance().getClass().getMethod("report", String.class);
			method.invoke(testResult.getInstance(), Helper.getTestReportStyle("../../../" + saveTo,
					"<img src=\"../../../" + saveTo + "\" width=\"400\" height=\"300\"/>"));
		} catch (Exception e) {
			logger.error(testResult.getTestClass().getName() + "." + testResult.getName() + " saveScreentshot failed "
					+ e.getMessage(), e);
		}
	}
}
