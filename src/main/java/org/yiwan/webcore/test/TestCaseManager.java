package org.yiwan.webcore.test;

/**
 * Created by Kenny Wang on 3/28/2016.
 */
public class TestCaseManager {

    private static ThreadLocal<TestBase> testCase = new ThreadLocal<TestBase>();

    public static TestBase getTestCase() {
        return testCase.get();
    }

    public static void setTestCase(TestBase testCase) {
        TestCaseManager.testCase.set(testCase);
    }
}
