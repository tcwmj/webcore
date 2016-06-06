package org.yiwan.webcore.bmproxy;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.bmproxy.pojo.HttpRequestDetail;
import org.yiwan.webcore.bmproxy.pojo.HttpResponseDetail;
import org.yiwan.webcore.bmproxy.pojo.UserTransactionDetail;
import org.yiwan.webcore.test.pojo.*;
import org.yiwan.webcore.util.PropHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kenny Wang on 5/18/2016.
 */
public class TimestampWriter {
    private static final Logger logger = LoggerFactory.getLogger(TimestampWriter.class);
    private static final File TRANSACTION_TIMESTAMPS_FILE = new File(PropHelper.TRANSACTION_TIMESTAMPS_FILE);

    public static void write(String sql) {
        try {
            FileUtils.writeStringToFile(TRANSACTION_TIMESTAMPS_FILE, sql, "UTF-8", true);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void write(TestCapability testCapability) {
        String sql = String.format("insert into DIM_CLIENT_ENV (client_os,client_os_version,client_browser_type,client_browser_version,client_resolution) values ('%s','%s','%s','%s','%s');\n", testCapability.getOs(), testCapability.getOsVersion(), testCapability.getBrowser(), testCapability.getBrowserVersion(), testCapability.getResolution());
        write(sql);
    }

    public static void write(TestEnvironment testEnvironment) {
        StringBuilder sql = new StringBuilder();
        for (ApplicationServer applicationServer : testEnvironment.getApplicationServers()) {
            HardwareInformation hardwareInformation = applicationServer.getHardwareInformation();
            sql.append(String.format("insert into DIM_APPLICATION (application_name,application_version) values ('%s','%s');\n", applicationServer.getName(), applicationServer.getVersion()));
            if (hardwareInformation != null) {
                sql.append(String.format("insert into DIM_APP_ENV (app_os,app_os_version) values ('%s','%s');\n", hardwareInformation.getOs(), hardwareInformation.getOsVersion()));
            }
        }
        for (DatabaseServer databaseServer : testEnvironment.getDatabaseServers()) {
            HardwareInformation hardwareInformation = databaseServer.getHardwareInformation();
            if (hardwareInformation != null) {
                sql.append(String.format("insert into DIM_DB_ENV (db_type,db_os,db_os_version) values ('%s','%s','%s');\n", databaseServer.getDriver(), hardwareInformation.getOs(), hardwareInformation.getOsVersion()));
            }
        }
        write(sql.toString());
    }

    /**
     * write userTransactionDetail into a file and finally to database after testing
     *
     * @param userTransactionDetail
     */
    public static void write(UserTransactionDetail userTransactionDetail) {
        String sql = String.format("insert into FCT_TEST_RESULT (transaction_id,user_action_time,dom_ready_time) values ('%s',%d,%d);\n", userTransactionDetail.getTransactionName(), userTransactionDetail.getUserActionTimestamp(), userTransactionDetail.getDocumentReadyTimestamp());
        write(sql);
    }

    /**
     * write httpRequestDetail into a file and finally to database after testing
     *
     * @param httpRequestDetail
     */
    public static void write(HttpRequestDetail httpRequestDetail) {
        HttpRequest httpRequest = httpRequestDetail.getHttpRequest();
        HttpMessageContents httpMessageContents = httpRequestDetail.getHttpMessageContents();
        HttpMessageInfo httpMessageInfo = httpRequestDetail.getHttpMessageInfo();
        String sql = String.format("insert into DIM_HTTP_REQUEST (request_time) values (%d);\n", httpRequestDetail.getRequestTimestamp());
        write(sql);
    }

    /**
     * write httpResponseDetail into a file and finally to database after testing
     *
     * @param httpResponseDetail
     */
    public static void write(HttpResponseDetail httpResponseDetail) {
        HttpResponse httpResponse = httpResponseDetail.getHttpResponse();
        HttpMessageContents httpMessageContents = httpResponseDetail.getHttpMessageContents();
        HttpMessageInfo httpMessageInfo = httpResponseDetail.getHttpMessageInfo();
        String sql = String.format("insert into DIM_HTTP_RESPONSE (reponse_time) values (%d);\n", httpResponseDetail.getResponseTimestamp());
        write(sql);
    }
}
