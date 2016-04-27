package org.yiwan.webcore.proxy;

import org.yiwan.webcore.test.ITestBase;

/**
 * Created by Kenny Wang on 4/5/2016.
 */
public class ScreenshotObserver extends SampleObserver {
    private ProxyWrapper proxyWrapper;

    public ScreenshotObserver(ProxyWrapper proxyWrapper) {
        this.proxyWrapper = proxyWrapper;
    }

    @Override
    public void start(ITestBase testCase) {
        super.start(testCase);
    }

    @Override
    public void stop(ITestBase testCase) {
        super.stop(testCase);
    }
}
