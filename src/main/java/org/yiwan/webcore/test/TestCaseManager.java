package org.yiwan.webcore.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.pojo.TestEnvironment;
import org.yiwan.webcore.util.PropHelper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Kenny Wang on 3/28/2016.
 */
public class TestCaseManager {
    private static final Logger logger = LoggerFactory.getLogger(TestCaseManager.class);
    private static final Set<TestEnvironment> TEST_ENVIRONMENTS = getTestEnvironments(PropHelper.SERVER_INFO);
    private static final BlockingQueue<TestEnvironment> TEST_ENVIRONMENT_BLOCKING_QUEUE = getTestEnvironmentBlockingQueue();
    private static ThreadLocal<TestBase> testCase = new ThreadLocal<>();

    private static Set<TestEnvironment> getTestEnvironments(String json) {
        try {
            return (new ObjectMapper()).readValue(json, new TypeReference<Set<TestEnvironment>>() {
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return new HashSet<>();
    }

    private static BlockingQueue<TestEnvironment> getTestEnvironmentBlockingQueue() {
        assert TEST_ENVIRONMENTS != null;
        return new LinkedBlockingDeque<>(TEST_ENVIRONMENTS);
    }

    public static ITestBase getTestCase() {
        return testCase.get();
    }

    public static void setTestCase(TestBase testCase) {
        TestCaseManager.testCase.set(testCase);
    }

    public static Set<TestEnvironment> getTestEnvironments() {
        return TEST_ENVIRONMENTS;
    }

    public static TestEnvironment takeTestEnvironment() throws InterruptedException {
        return TEST_ENVIRONMENT_BLOCKING_QUEUE.take();
    }

    public static void putTestEnvironment(TestEnvironment testEnvironment) throws InterruptedException {
        TEST_ENVIRONMENT_BLOCKING_QUEUE.put(testEnvironment);
    }
}
