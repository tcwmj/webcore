package org.yiwan.webcore.web;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IFluentAssert {
    AbstractBooleanAssert<?> alertIsPresent();

    AbstractCharSequenceAssert<?, String> alertText();

    AbstractCharSequenceAssert<?, String> pageTitle();

    AbstractCharSequenceAssert<?, String> pageSource();

    AbstractCharSequenceAssert<?, String> url();
}
