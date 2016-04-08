package org.yiwan.webcore.web;

/**
 * Created by Kenny Wang on 4/8/2016.
 */
public interface IBrowseNavigation {
    IBrowseNavigation to(String url);

    IBrowseNavigation forward();

    IBrowseNavigation backward();
}
