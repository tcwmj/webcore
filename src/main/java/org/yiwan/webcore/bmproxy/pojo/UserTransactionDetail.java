package org.yiwan.webcore.bmproxy.pojo;

import java.util.List;

/**
 * Created by Kenny Wang on 4/2/2016.
 */
public class UserTransactionDetail {
    private long userActionTimestamp;
    private long documentReadyTimestamp;
    private List<HttpTransactionDetail> httpTransactionDetails;

    public List<HttpTransactionDetail> getHttpTransactionDetails() {
        return httpTransactionDetails;
    }

    public void setHttpTransactionDetails(List<HttpTransactionDetail> httpTransactionDetails) {
        this.httpTransactionDetails = httpTransactionDetails;
    }

    public long getDocumentReadyTimestamp() {
        return documentReadyTimestamp;
    }

    public void setDocumentReadyTimestamp(long documentReadyTimestamp) {
        this.documentReadyTimestamp = documentReadyTimestamp;
    }

    public long getUserActionTimestamp() {
        return userActionTimestamp;
    }

    public void setUserActionTimestamp(long userActionTimestamp) {
        this.userActionTimestamp = userActionTimestamp;
    }
}
