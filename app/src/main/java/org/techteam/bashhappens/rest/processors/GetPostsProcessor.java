package org.techteam.bashhappens.rest.processors;

import android.content.Context;
import android.os.Bundle;

import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.exceptions.ContentParseException;
import org.techteam.bashhappens.content.exceptions.FeedOverException;
import org.techteam.bashhappens.db.resolvers.AbstractContentResolver;
import org.techteam.bashhappens.db.resolvers.bashorg.BashResolver;
import org.techteam.bashhappens.gui.loaders.LoadIntention;
import org.techteam.bashhappens.rest.OperationType;
import org.techteam.bashhappens.rest.service_helper.ServiceCallback;

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
        } catch (FeedOverException ignored) {

        } catch (ContentParseException e) {
            exc = e;
        }

        if (list == null) {
            if (exc != null) {
                transactionError(operationType, requestId);
                cb.onError(exc.getMessage(), null);
            } else {
                transactionFinished(operationType, requestId);
                Bundle data = getInitialBundle();
                data.putBoolean(ServiceCallback.GetPostsExtras.FEED_FINISHED, true);
                cb.onSuccess(data);
            }

            System.out.println("DONE! list is null");
        } else {
            System.out.println("DONE! + " + list.getEntries().size());

            ContentSection section = contentSource.getSection();
            BashResolver resolver = (BashResolver) AbstractContentResolver.getResolver(section);

            if (loadIntention == LoadIntention.REFRESH) {
                resolver.deleteAllEntries(getContext());
            }

            // writing to db
            int insertedCount = resolver.insertEntries(getContext(), list).size();

            // finishing up a transaction
            transactionFinished(operationType, requestId);

            Bundle data = getInitialBundle();
            data.putInt(ServiceCallback.GetPostsExtras.INSERTED_COUNT, insertedCount);
            cb.onSuccess(data);
        }
    }

    private Bundle getInitialBundle() {
        Bundle data = new Bundle();
        data.putParcelable(ServiceCallback.GetPostsExtras.NEW_CONTENT_SOURCE, contentSource);
        data.putInt(ServiceCallback.GetPostsExtras.LOAD_INTENTION, loadIntention);
        return data;
    }
}
