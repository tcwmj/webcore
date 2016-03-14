package org.yiwan.webcore.perf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestTemplate;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class SampleObserver implements Observer {
    private final static Logger logger = LoggerFactory.getLogger(SampleObserver.class);

    private boolean started = false;

    @Override
    public void start(ITestTemplate testCase) {
        started = true;
    }

    @Override
    public void stop(ITestTemplate testCase) {
        if (!started) {
            throw new RuntimeException(this.getClass() + " must be started");
        }
        started = false;
    }

}
