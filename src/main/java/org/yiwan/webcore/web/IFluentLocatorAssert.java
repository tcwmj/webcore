package org.yiwan.webcore.web;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractListAssert;
import org.yiwan.webcore.locator.Locator;

import java.util.List;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IFluentLocatorAssert {
    AbstractListAssert<? extends AbstractListAssert, ? extends List, String> allSelectedTexts();

    AbstractListAssert<? extends AbstractListAssert, ? extends List, String> allOptionTexts();

    AbstractBooleanAssert<?> present();

    AbstractBooleanAssert<?> enabled();

    AbstractBooleanAssert<?> displayed();

    AbstractBooleanAssert<?> selected();

    AbstractCharSequenceAssert<?, String> innerText();

    AbstractListAssert<? extends AbstractListAssert, ? extends List, String> allInnerTexts();

    AbstractCharSequenceAssert<?, String> attributeValueOf(String attribute);

    AbstractCharSequenceAssert<?, String> cssValueOf(String cssAttribute);

    AbstractIntegerAssert<? extends AbstractIntegerAssert<?>> numberOfElements();

    IFluentLocatorAssert nestedElements(Locator locator);
}
