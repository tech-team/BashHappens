package org.techteam.bashhappens.content.bashorg;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentType;
import org.techteam.bashhappens.db.TransactionStatus;
import org.techteam.bashhappens.rest.OperationType;

public class BashTransactionsEntry extends ContentEntry {
    private static final String LOG_TAG = BashTransactionsEntry.class.toString();

    public static final ContentType CONTENT_TYPE = ContentType.BASH_ORG;

    private String id;
    private OperationType operationType;
    private TransactionStatus status;

    public BashTransactionsEntry() {
        super(CONTENT_TYPE);
        this.operationType = OperationType.GET_POSTS;
        this.status = TransactionStatus.STARTED;
    }

    public String getId() {
        return id;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public BashTransactionsEntry setId(String id) {
        this.id = id;
        return this;
    }

    public BashTransactionsEntry setOperationType(int operationType) {
        this.operationType = OperationType.toType(operationType);
        return this;
    }
    public BashTransactionsEntry setOperationType(OperationType operationType) {
        this.operationType = operationType;
        return this;
    }

    public BashTransactionsEntry setStatus(int status) {
        this.status = TransactionStatus.toStatus(status);
        return this;
    }
    public BashTransactionsEntry setStatus(TransactionStatus status) {
        this.status = status;
        return this;
    }
}
