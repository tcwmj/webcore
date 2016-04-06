package org.yiwan.webcore.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.TestBase;

/**
 * Created by Kenny Wang on 4/5/2016.
 */
public class ScreenshotObserver extends SampleObserver {
    private final static Logger logger = LoggerFactory.getLogger(ScreenshotObserver.class);
    private ProxyWrapper proxyWrapper;

    public ScreenshotObserver(ProxyWrapper proxyWrapper) {
        this.proxyWrapper = proxyWrapper;
    }

    @Override
    public void start(TestBase testCase) {
        super.start(testCase);
    }

    @Override
    public void stop(TestBase testCase) {
        super.stop(testCase);
    }
}
