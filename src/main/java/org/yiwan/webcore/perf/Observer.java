package org.yiwan.webcore.perf;

import org.yiwan.webcore.test.ITestTemplate;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public interface Observer {
    public void start(ITestTemplate testCase);

    public void stop(ITestTemplate testCase);
}
