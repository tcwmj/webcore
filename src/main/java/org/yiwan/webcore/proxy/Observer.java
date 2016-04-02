package org.yiwan.webcore.proxy;

import org.yiwan.webcore.test.TestBase;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public interface Observer {
    public void start(TestBase testCase);

    public void stop(TestBase testCase);
}
