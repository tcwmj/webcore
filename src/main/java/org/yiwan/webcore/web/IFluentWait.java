package org.yiwan.webcore.web;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IFluentWait {
    void timeout(int milliseconds) throws InterruptedException;

    Boolean documentComplete();

    IAlertWrapper alertIsPresent();

    IFluentStringWait pageTitle();

    IFluentStringWait pageSource();

    IFluentStringWait url();
}
