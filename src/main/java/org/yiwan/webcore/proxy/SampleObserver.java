package org.yiwan.webcore.proxy;

import org.yiwan.webcore.test.ITestBase;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class SampleObserver implements Observer {
    private boolean started = false;

    @Override
    public void start(ITestBase testCase) {
        started = true;
    }

    @Override
    public void stop(ITestBase testCase) {
        if (!started) {
            throw new RuntimeException(this.getClass() + " must be started");
        }
        started = false;
    }

}
