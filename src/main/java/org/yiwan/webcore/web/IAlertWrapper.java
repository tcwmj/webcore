package org.yiwan.webcore.web;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IAlertWrapper {
    void dismiss();

    void accept();

    String getText();

    boolean exists();
}
