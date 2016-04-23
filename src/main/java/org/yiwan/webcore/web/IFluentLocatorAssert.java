package org.yiwan.webcore.web;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractListAssert;

import java.util.List;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IFluentLocatorAssert {
    AbstractBooleanAssert<?> hasSelectableText(String text);

    AbstractListAssert<? extends AbstractListAssert, ? extends List, String> allSelectedTexts();

    AbstractBooleanAssert<?> isEnabled();

    AbstractBooleanAssert<?> isDisplayed();

    AbstractBooleanAssert<?> isSelected();

    AbstractCharSequenceAssert<?, String> innerText();

    AbstractCharSequenceAssert<?, String> attributeValueOf(String attribute);

    AbstractCharSequenceAssert<?, String> cssValueOf(String cssAttribute);
}
