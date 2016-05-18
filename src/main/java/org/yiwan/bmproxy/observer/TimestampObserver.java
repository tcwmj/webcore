package org.yiwan.bmproxy.observer;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.bmproxy.ProxyWrapper;
import org.yiwan.bmproxy.pojo.HttpRequestDetail;
import org.yiwan.bmproxy.pojo.HttpResponseDetail;
import org.yiwan.bmproxy.pojo.UserTransactionDetail;
import org.yiwan.bmproxy.pojo.TransactionDetail;
import org.yiwan.webcore.test.ITestBase;
import org.yiwan.webcore.util.PropHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class TimestampObserver extends SampleObserver {
    private static final Logger logger = LoggerFactory.getLogger(TimestampObserver.class);
    private static final File TRANSACTION_TIMESTAMPS_FILE = new File(PropHelper.TRANSACTION_TIMESTAMPS_FILE);
    private ITestBase testcase;
    private ProxyWrapper proxyWrapper;
    private UserTransactionDetail userTransactionDetail;
    private TransactionDetail transactionDetail;

    public TimestampObserver(ITestBase testCase) {
        this.testcase = testCase;
        this.proxyWrapper = testCase.getProxyWrapper();
        supprotRecordTimestamp();
    }

    @Override
    public void start() {
        super.start();
        userTransactionDetail = new UserTransactionDetail();
        userTransactionDetail.setUserActionTimestamp(System.currentTimeMillis());
        userTransactionDetail.setTransactionDetails(new ArrayList<TransactionDetail>());
    }

    @Override
    public void stop() {
        super.stop();
        userTransactionDetail.setDocumentReadyTimestamp(System.currentTimeMillis());
        //write userTransactionDetail into a file and finally to database after testing
        StringBuilder sql = new StringBuilder();
        sql.append(String.format("insert into xTable (Transaciton_Name,...) values (%s,....)", testcase.getTransactionName()));
        sql.append(String.format("insert into yTable (Transaciton_Name,...) values (%s,....)", testcase.getTransactionName()));
        try {
            FileUtils.writeStringToFile(TRANSACTION_TIMESTAMPS_FILE, sql.toString(), "UTF-8", true);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void supprotRecordTimestamp() {
        proxyWrapper.addReqeustFilter(new RequestFilter() {
            @Override
            public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo) {
                transactionDetail = new TransactionDetail();
                userTransactionDetail.getTransactionDetails().add(transactionDetail);
                transactionDetail.setHttpRequestDetail(new HttpRequestDetail(System.currentTimeMillis(), request, contents, messageInfo));
                return null;
            }
        });
        proxyWrapper.addResponseFilter(new ResponseFilter() {
            @Override
            public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
                transactionDetail.setHttpResponseDetail(new HttpResponseDetail(System.currentTimeMillis(), response, contents, messageInfo));
            }
        });
    }
}
