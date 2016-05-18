package org.yiwan.webcore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Created by Kenny Wang on 5/18/2016.
 */
public class PropHelperTest {
    private static final Logger logger = LoggerFactory.getLogger(PropHelperTest.class);

    @Test
    public void testServerInfoProperty() throws Exception {
        logger.info("\n" + PropHelper.SERVER_INFO);
    }
}
