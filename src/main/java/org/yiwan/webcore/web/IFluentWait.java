package org.yiwan.webcore.web;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IFluentWait {
    void timeout(int milliseconds) throws InterruptedException;

    IAlertWrapper alertIsPresent();

    Boolean documentComplete();

    Boolean pageTitleIs(String title);

    Boolean pageTitleContains(String title);

    Boolean pageSourceContains(String text);
}
