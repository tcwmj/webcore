package org.yiwan.webcore.proxy;

import org.yiwan.webcore.test.ITestBase;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public interface Observer {
    public void start(ITestBase testCase);

    public void stop(ITestBase testCase);
}
