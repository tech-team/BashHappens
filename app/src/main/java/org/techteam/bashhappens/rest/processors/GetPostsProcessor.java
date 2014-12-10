package org.techteam.bashhappens.rest.processors;

import android.content.Context;

import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.bashorg.BashTransactionsEntry;
import org.techteam.bashhappens.content.exceptions.ContentParseException;
import org.techteam.bashhappens.content.exceptions.FeedOverException;
import org.techteam.bashhappens.content.resolvers.AbstractContentResolver;
import org.techteam.bashhappens.content.resolvers.BashResolver;
import org.techteam.bashhappens.content.resolvers.BashTransactionsResolver;
import org.techteam.bashhappens.db.TransactionStatus;
import org.techteam.bashhappens.rest.OperationType;

public class GetPostsProcessor extends Processor {
    private final ContentSource contentSource;
    private final int loadIntention;

    public GetPostsProcessor(Context context, ContentSource contentSource, int loadIntention) {
        super(context);
        this.contentSource = contentSource;
        this.loadIntention = loadIntention;
    }

    @Override
    public void start(OperationType operationType, String requestId, ProcessorCallback cb) {

        transactionStarted(operationType, requestId);

        ContentList list = null;
        Throwable exc = null;
        try {
            list = contentSource.retrieveNextList(getContext());
        } catch (FeedOverException e) {
            exc = e;
        } catch (ContentParseException e) {
            exc = e;
        }
        if (list != null) {
            System.out.println("DONE! + " + list.getEntries().size());
        } else {
            if (exc != null) {
                cb.onError(exc.getMessage());
            } else {
                cb.onError("Unexpected error");
            }
            transactionError(operationType, requestId);

            System.out.println("DONE! list is null");
        }

        // writing to db
        ContentSection section = contentSource.getSection();
        BashResolver resolver = (BashResolver) AbstractContentResolver.getResolver(section);
        resolver.insertEntries(getContext(), list);

        // finishing up a transaction
        transactionFinished(operationType, requestId);

        cb.onSuccess();
    }
}
