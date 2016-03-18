package org.yiwan.webcore.perf;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestTemplate;
import org.yiwan.webcore.util.ProxyWrapper;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class TimestampObserver extends SampleObserver {
    private final static Logger logger = LoggerFactory.getLogger(TimestampObserver.class);
    private ProxyWrapper proxyWrapper;

    private long actionTimestamp;
    private long readyTimestamp;
    private long requestTimestamp;
    private long responseTimestamp;

    public TimestampObserver(ProxyWrapper proxyWrapper) {
        this.proxyWrapper = proxyWrapper;
        supprotRecordTimestamp();
    }

    @Override
    public void start(ITestTemplate testCase) {
        super.start(testCase);
        if (testCase.isRecordTransactionTimestamp()) {
            clearTimestamp();
            actionTimestamp = System.currentTimeMillis();
        }
    }

    @Override
    public void stop(ITestTemplate testCase) {
        super.stop(testCase);
        if (testCase.isRecordTransactionTimestamp()) {
            readyTimestamp = System.currentTimeMillis();
            testCase.setRecordTransactionTimestamp(false);
        }
    }

    private void supprotRecordTimestamp() {
        proxyWrapper.addReqeustFilter(new RequestFilter() {

            @Override
            public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents,
                                              HttpMessageInfo messageInfo) {
                requestTimestamp = System.currentTimeMillis();
                return null;
            }

        });
        proxyWrapper.addResponseFilter(new ResponseFilter() {

            @Override
            public void filterResponse(HttpResponse response, HttpMessageContents contents,
                                       HttpMessageInfo messageInfo) {
                responseTimestamp = System.currentTimeMillis();

            }
        });
    }

    private void clearTimestamp() {
        readyTimestamp = -1;
        requestTimestamp = -1;
        responseTimestamp = -1;
    }
}
