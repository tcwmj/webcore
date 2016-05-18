package org.yiwan.webcore.bmproxy.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestBase;
import org.yiwan.webcore.zaproxy.PenetrationTest;

/**
 * Created by Kenny Wang on 5/18/2016.
 */
public class WebSpiderObserver extends SampleObserver {
    private static final Logger logger = LoggerFactory.getLogger(WebSpiderObserver.class);
    private ITestBase testCase;

    public WebSpiderObserver(ITestBase testCase) {
        this.testCase = testCase;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        PenetrationTest.doActions(testCase.getWebDriverWrapper().getCurrentUrl());
    }
}
