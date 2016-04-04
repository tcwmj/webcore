package org.yiwan.webcore.web;

import org.openqa.selenium.interactions.Action;
import org.yiwan.webcore.locator.Locator;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IActionsWrapper {
    IActionsWrapper click();

    IActionsWrapper click(Locator locator);

    IActionsWrapper doubleClick(Locator locator);

    IActionsWrapper sendKeys(Locator locator, CharSequence... keysToSend);

    IActionsWrapper sendKeys(CharSequence... keysToSend);

    IActionsWrapper moveToElement(Locator locator);

    Action build();

    void perform();
}
