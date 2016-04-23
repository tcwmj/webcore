package org.yiwan.webcore.web;

import java.util.regex.Pattern;

/**
 * Created by Kenny Wang on 4/23/2016.
 */
public interface IFluentStringWait {

    Boolean toBe(String text);

    Boolean toBeEmpty();

    Boolean notToBe(String text);

    Boolean contains(String text);

    Boolean notContains(String text);

    Boolean startWith(String text);

    Boolean endWith(String text);

    Boolean matches(Pattern pattern);
}
