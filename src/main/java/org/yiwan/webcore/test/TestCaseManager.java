package org.yiwan.webcore.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.util.PropHelper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Kenny Wang on 3/28/2016.
 */
public class TestCaseManager {
    private static final Logger logger = LoggerFactory.getLogger(TestCaseManager.class);
    private static final BlockingQueue<TestEnvironment> TEST_ENVIRONMENTS = getTestEnvironments(PropHelper.SERVER_INFO);
    private static ThreadLocal<TestBase> testCase = new ThreadLocal<TestBase>();

    public static TestBase getTestCase() {
        return testCase.get();
    }

    public static void setTestCase(TestBase testCase) {
        TestCaseManager.testCase.set(testCase);
    }

    private static BlockingQueue<TestEnvironment> getTestEnvironments(String json) {
        try {
            List<TestEnvironment> testEnvironments = (new ObjectMapper()).readValue(json, new TypeReference<List<TestEnvironment>>() {
            });
            return new LinkedBlockingDeque<TestEnvironment>(testEnvironments);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static TestEnvironment takeTestEnvironment() throws InterruptedException {
        return TEST_ENVIRONMENTS.take();
    }

    public static void putTestEnvironment(TestEnvironment testEnvironment) throws InterruptedException {
        TEST_ENVIRONMENTS.put(testEnvironment);
    }
}
