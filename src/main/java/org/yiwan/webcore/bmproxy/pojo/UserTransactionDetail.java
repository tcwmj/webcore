package org.yiwan.webcore.bmproxy.pojo;

import java.util.List;

/**
 * Created by Kenny Wang on 4/2/2016.
 */
public class UserTransactionDetail {
    private long userActionTimestamp;
    private long documentReadyTimestamp;
    private List<TransactionDetail> transactionDetails;

    public List<TransactionDetail> getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(List<TransactionDetail> transactionDetails) {
        this.transactionDetails = transactionDetails;
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
