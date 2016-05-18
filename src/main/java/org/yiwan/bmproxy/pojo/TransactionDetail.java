package org.yiwan.bmproxy.pojo;

/**
 * Created by Kenny Wang on 4/2/2016.
 */
public class TransactionDetail {
    private HttpRequestDetail httpRequestDetail;
    private HttpResponseDetail httpResponseDetail;

    public HttpResponseDetail getHttpResponseDetail() {
        return httpResponseDetail;
    }

    public void setHttpResponseDetail(HttpResponseDetail httpResponseDetail) {
        this.httpResponseDetail = httpResponseDetail;
    }

    public HttpRequestDetail getHttpRequestDetail() {
        return httpRequestDetail;
    }

    public void setHttpRequestDetail(HttpRequestDetail httpRequestDetail) {
        this.httpRequestDetail = httpRequestDetail;
    }
}
