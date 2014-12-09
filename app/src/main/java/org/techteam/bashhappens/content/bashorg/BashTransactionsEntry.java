package org.techteam.bashhappens.content.bashorg;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentType;
import org.techteam.bashhappens.db.TransactionStatus;

public class BashTransactionsEntry extends ContentEntry {
    private static final String LOG_TAG = BashTransactionsEntry.class.toString();

    public static final ContentType CONTENT_TYPE = ContentType.BASH_ORG;

    private String id;
    private TransactionStatus status = null;

    public BashTransactionsEntry() {
        super(CONTENT_TYPE);
    }

    public String getId() {
        return id;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public BashTransactionsEntry setId(String id) {
        this.id = id;
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
