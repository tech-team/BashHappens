package org.techteam.bashhappens.rest.processors;

import android.content.Context;

import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.bashorg.TransactionEntry;
import org.techteam.bashhappens.content.resolvers.AbstractContentResolver;
import org.techteam.bashhappens.content.resolvers.TransactionsResolver;
import org.techteam.bashhappens.db.TransactionStatus;
import org.techteam.bashhappens.rest.OperationType;

public abstract class Processor {

    private final Context context;
    private final TransactionsResolver transactioner;

    public Processor(Context context) {
        this.context = context;
        transactioner = (TransactionsResolver) AbstractContentResolver.getResolver(ContentSection.TRANSACTIONS);
    }

    public Context getContext() {
        return context;
    }

    public abstract void start(OperationType operationType, String requestId, ProcessorCallback cb);

    protected void transactionStarted(OperationType operationType, String requestId) {
        TransactionEntry trx = new TransactionEntry().setId(requestId)
                                                     .setOperationType(operationType)
                                                     .setStatus(TransactionStatus.STARTED);
        transactioner.insert(getContext(), trx);
    }

    protected void transactionFinished(OperationType operationType, String requestId) {
        TransactionEntry trx = new TransactionEntry().setId(requestId)
                                                     .setOperationType(operationType)
                                                     .setStatus(TransactionStatus.FINISHED);
        transactioner.insert(getContext(), trx);
    }

    protected void transactionError(OperationType operationType, String requestId) {
        TransactionEntry trx = new TransactionEntry().setId(requestId)
                                                     .setOperationType(operationType)
                                                     .setStatus(TransactionStatus.ERROR);
        transactioner.insert(getContext(), trx);
    }
}
