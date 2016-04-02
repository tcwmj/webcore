package org.yiwan.webcore.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.TestBase;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class SampleObserver implements Observer {
    private final static Logger logger = LoggerFactory.getLogger(SampleObserver.class);

    private boolean started = false;

    @Override
    public void start(TestBase testCase) {
        started = true;
    }

    @Override
    public void stop(TestBase testCase) {
        if (!started) {
            throw new RuntimeException(this.getClass() + " must be started");
        }
        started = false;
    }

}
