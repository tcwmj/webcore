package org.yiwan.webcore.web;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;

public interface IFluentAlertAssert {
    AbstractBooleanAssert<?> present();

    AbstractCharSequenceAssert<?, String> text();

}
