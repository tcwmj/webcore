package org.yiwan.webcore.proxy.observer;

import org.yiwan.webcore.test.ITestBase;

/**
 * Created by Kenny Wang on 4/5/2016.
 */
public class ScreenshotObserver extends SampleObserver {
    private ITestBase testCase;

    public ScreenshotObserver(ITestBase testCase) {
        this.testCase = testCase;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }
}
