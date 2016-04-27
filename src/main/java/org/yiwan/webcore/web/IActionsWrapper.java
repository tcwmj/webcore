package org.yiwan.webcore.web;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Action;
import org.yiwan.webcore.locator.Locator;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IActionsWrapper {
    IActionsWrapper click();

    IActionsWrapper click(Locator locator);

    IActionsWrapper clickAndHold();

    IActionsWrapper clickAndHold(Locator locator);

    IActionsWrapper contextClick();

    IActionsWrapper contextClick(Locator locator);

    IActionsWrapper release();

    IActionsWrapper release(Locator locator);

    IActionsWrapper doubleClick();

    IActionsWrapper doubleClick(Locator locator);

    IActionsWrapper dragAndDrop(Locator source, Locator target);

    IActionsWrapper dragAndDrop(Locator source, int xOffset, int yOffset);

    IActionsWrapper keyDown(Keys theKey);

    IActionsWrapper keyDown(Locator locator, Keys theKey);

    IActionsWrapper keyUp(Keys theKey);

    IActionsWrapper keyUp(Locator locator, Keys theKey);

    IActionsWrapper sendKeys(Locator locator, CharSequence... keysToSend);

    IActionsWrapper sendKeys(CharSequence... keysToSend);

    IActionsWrapper moveTo(Locator locator);

    IActionsWrapper moveTo(Locator locator, int xOffset, int yOffset);

    IActionsWrapper moveTo(int xOffset, int yOffset);

    Action build();

    void perform();
}
