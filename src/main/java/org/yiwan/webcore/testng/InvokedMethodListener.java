package org.yiwan.webcore.testng;

//import java.io.File;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
//import org.apache.log4j.Logger;

//import com.lombardrisk.pojo.DataBean;
//import com.lombardrisk.utils.Property;
//import com.lombardrisk.utils.TestCase;

public class InvokedMethodListener implements IInvokedMethodListener {

	// private Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		// if (!TestCase.getSkipTest() && TestCase.getTestCaseId() != null) {
		// DataBean dataBean = null;
		// File file = null;
		// if (method.getTestMethod().getMethodName()
		// .equals(Property.FIRST_TEST_METHOD)) {
		// logger.info("try to get source test data");
		// file = TestCase.getTestData(TestCase.getTestCaseId(), true);
		// dataBean = TestCase.getTestData(file);
		// } else {
		// logger.info("try to get target test data");
		// file = TestCase.getTestData(TestCase.getTestCaseId(), false);
		// dataBean = TestCase.getTestData(file);
		// if (dataBean == null) {
		// logger.info("try to get source test data");
		// file = TestCase.getTestData(TestCase.getTestCaseId(), true);
		// dataBean = TestCase.getTestData(file);
		// }
		// }
		// if (dataBean == null) {
		// logger.info("try to generate new test data");
		// dataBean = new DataBean();
		// }
		// TestCase.setDb(dataBean);
		// }
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		// if (!TestCase.getSkipTest() && TestCase.getTestCaseId() != null) {
		// logger.info("try to save test data");
		// File file = TestCase.getTestData(TestCase.getTestCaseId(), false);
		// TestCase.setTestData(file, TestCase.getDb());
		// }
	}
}
