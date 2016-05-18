package org.yiwan.webcore.bmproxy;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.bmproxy.pojo.UserTransactionDetail;
import org.yiwan.webcore.test.pojo.TestCapability;
import org.yiwan.webcore.test.pojo.TestEnvironment;
import org.yiwan.webcore.util.PropHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kenny Wang on 5/18/2016.
 */
public class TransactionTimestampRecorder {
    private static final Logger logger = LoggerFactory.getLogger(TransactionTimestampRecorder.class);
    private static final File TRANSACTION_TIMESTAMPS_FILE = new File(PropHelper.TRANSACTION_TIMESTAMPS_FILE);

    public static void write(String sql) {
        try {
            FileUtils.writeStringToFile(TRANSACTION_TIMESTAMPS_FILE, sql, "UTF-8", true);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void write(TestCapability testCapability) {
        StringBuilder sql = new StringBuilder();
        //might be only record once for test capability
        sql.append(String.format("insert into xTable (Test_Capability,...) values (%s,....)", testCapability));
    }

    public static void write(TestEnvironment testEnvironment) {
        StringBuilder sql = new StringBuilder();
        //might be only record once for test environment
        sql.append(String.format("insert into xTable (Test_ENVIRONMENT,...) values (%s,....)", testEnvironment));
    }


    /**
     * write userTransactionDetail into a file and finally to database after testing
     *
     * @param userTransactionDetail
     */
    public static void write(UserTransactionDetail userTransactionDetail) {
        StringBuilder sql = new StringBuilder();
        //might be only record once for test environment
        sql.append(String.format("insert into yTable (Transaciton_Name,...) values (%s,....)", userTransactionDetail));
    }
}
