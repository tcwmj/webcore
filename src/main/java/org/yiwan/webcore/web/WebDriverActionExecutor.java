package org.yiwan.webcore.web;

import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kenny Wang on 8/22/2016.
 * for resolving UnreachableBrowserException by retry last operation
 */
public class WebDriverActionExecutor {
    private final Logger logger = LoggerFactory.getLogger(WebDriverActionExecutor.class);
    private static final int MAX_RETRIES = 3;

    private Exception exception;

    public void execute(IWebDriverAction action) {
        execute(action, 0);
    }

    private void execute(IWebDriverAction action, int retries) {
        if (retries > MAX_RETRIES) {
            throw new RuntimeException(exception);
        } else if (retries > 0) {
            logger.warn("UnreachableBrowserException occurred, retry {}", retries);
        }

        try {
            action.execute();
//        } catch (UnreachableBrowserException | TimeoutException t) {
        } catch (UnreachableBrowserException e) {
            exception = e;
            execute(action, ++retries);
        }
    }
}
