package org.techteam.bashhappens.rest.processors;

import android.content.Context;

import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.bashorg.BashTransactionsEntry;
import org.techteam.bashhappens.content.resolvers.AbstractContentResolver;
import org.techteam.bashhappens.content.resolvers.BashTransactionsResolver;
import org.techteam.bashhappens.db.TransactionStatus;
import org.techteam.bashhappens.rest.OperationType;

public abstract class Processor {

    private final Context context;
    private final BashTransactionsResolver transactioner;

    public Processor(Context context) {
        this.context = context;
        transactioner = (BashTransactionsResolver) AbstractContentResolver.getResolver(ContentSection.BASH_ORG_TRANSACTIONS);
    }

    public Context getContext() {
        return context;
    }

    public abstract void start(OperationType operationType, String requestId, ProcessorCallback cb);

    protected void transactionStarted(OperationType operationType, String requestId) {
        BashTransactionsEntry trx = new BashTransactionsEntry().setId(requestId)
                                                               .setOperationType(operationType)
                                                               .setStatus(TransactionStatus.STARTED);
        transactioner.insertEntry(getContext(), trx);
    }

    protected void transactionFinished(OperationType operationType, String requestId) {
        BashTransactionsEntry trx = new BashTransactionsEntry().setId(requestId)
                                                               .setOperationType(operationType)
                                                               .setStatus(TransactionStatus.FINISHED);
        transactioner.updateEntry(getContext(), trx);
    }

    protected void transactionError(OperationType operationType, String requestId) {
        BashTransactionsEntry trx = new BashTransactionsEntry().setId(requestId)
                                                               .setOperationType(operationType)
                                                               .setStatus(TransactionStatus.ERROR);
        transactioner.updateEntry(getContext(), trx);
    }
}
