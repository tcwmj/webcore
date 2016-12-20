package org.yiwan.webcore.web;

import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kenny Wang on 8/22/2016.
 * for resolving UnreachableBrowserException by retry last operation
 */
public class WebDriverActionExecutor {
    private final Logger logger = LoggerFactory.getLogger(WebDriverActionExecutor.class);
    private int max_retry_times;

    public WebDriverActionExecutor() {
        this(2);
    }

    public WebDriverActionExecutor(int max_retry_times) {
        this.max_retry_times = max_retry_times;
    }

    public void execute(IWebDriverAction action) {
        execute(action, WebDriverException.class);
    }

    /**
     * @param action
     * @param exceptionType any exception such as UnreachableBrowserException, TimeoutException, WebDriverException
     * @param <T>
     */
    public <T extends Exception> void execute(IWebDriverAction action, Class<T> exceptionType) {
        execute(action, 0, exceptionType);
    }

    private <T extends Exception> void execute(IWebDriverAction action, int retries, Class<T> exceptionType) {
        if (retries > max_retry_times) {
            throw new RuntimeException("exceed retry times, max retry times is " + max_retry_times);
        } else {
            try {
                action.execute();
            } catch (Exception e) {
                if (exceptionType.isInstance(e)) {
                    logger.warn(String.format("retry %d time(s) due to %s:\n%s", ++retries, exceptionType.getName(), e.getMessage()));
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        logger.error(ie.getMessage(), ie);
                    }
                    execute(action, retries, exceptionType);
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
