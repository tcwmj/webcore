package org.yiwan.webcore.proxy;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.proxy.pojo.HttpRequestDetail;
import org.yiwan.webcore.proxy.pojo.HttpResponseDetail;
import org.yiwan.webcore.proxy.pojo.TransactionDetail;
import org.yiwan.webcore.proxy.pojo.UserTransactionDetail;
import org.yiwan.webcore.test.ITestBase;

import java.util.ArrayList;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class TimestampObserver extends SampleObserver {
    private final static Logger logger = LoggerFactory.getLogger(TimestampObserver.class);
    private ProxyWrapper proxyWrapper;
    private UserTransactionDetail userTransactionDetail;
    private TransactionDetail transactionDetail;

    public TimestampObserver(ProxyWrapper proxyWrapper) {
        this.proxyWrapper = proxyWrapper;
        supprotRecordTimestamp();
    }

    @Override
    public void start(ITestBase testCase) {
        super.start(testCase);
        userTransactionDetail = new UserTransactionDetail();
        userTransactionDetail.setUserActionTimestamp(System.currentTimeMillis());
        userTransactionDetail.setTransactionDetails(new ArrayList<TransactionDetail>());
    }

    @Override
    public void stop(ITestBase testCase) {
        super.stop(testCase);
        userTransactionDetail.setDocumentReadyTimestamp(System.currentTimeMillis());
        //TODO write userTransactionDetail into database
//        try {
//            Class.forName("").newInstance();
//        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
//            logger.error(e.getMessage(), e);
//        }
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
