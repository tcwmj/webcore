package org.yiwan.webcore.testng;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import org.testng.SkipException;

public class Hook implements IHookable {

    private final static Logger logger = LoggerFactory.getLogger(Hook.class);

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        // Preferably initialized in a @Configuration method

        // get the skip test value to determine whether to skip next methods on
        // such test case
        Method method;
        Boolean skipTest = false;

        try {
            method = testResult.getInstance().getClass()
                    .getMethod("getSkipTest");
            skipTest = (Boolean) method.invoke(testResult.getInstance());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (skipTest)
            throw new SkipException("skip method "
                    + testResult.getTestClass().getName() + "."
                    + testResult.getName());

        callBack.runTestMethod(testResult);
    }

}
