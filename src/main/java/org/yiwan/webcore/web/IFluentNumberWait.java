package org.yiwan.webcore.web;

/**
 * Created by Kenny Wang on 4/23/2016.
 */
public interface IFluentNumberWait {

    Boolean equalTo(int number);

    Boolean notEqualTo(int number);

    Boolean lessThan(int number);

    Boolean greaterThan(int number);

    Boolean equalToOrLessThan(int number);

    Boolean equalToOrGreaterThan(int number);
}
