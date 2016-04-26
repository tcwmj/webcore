package org.yiwan.webcore.web;

import org.assertj.core.api.AbstractCharSequenceAssert;

public interface IFluentPageAssert {
    AbstractCharSequenceAssert<?, String> title();

    AbstractCharSequenceAssert<?, String> source();

    AbstractCharSequenceAssert<?, String> url();
}
