package org.yiwan.webcore.page;

import org.yiwan.webcore.locator.LocatorBean;
import org.yiwan.webcore.util.JaxbHelper;
import org.yiwan.webcore.util.PropHelper;

public interface IPage {
	public final static LocatorBean l = JaxbHelper.unmarshal(
			ClassLoader.getSystemResourceAsStream(PropHelper.LOCATORS_FILE),
			ClassLoader.getSystemResourceAsStream(PropHelper.LOCATOR_SCHEMA), LocatorBean.class);
}
