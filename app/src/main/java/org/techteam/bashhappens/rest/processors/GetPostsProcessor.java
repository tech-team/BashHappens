package org.techteam.bashhappens.rest.processors;

import android.content.Context;

import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.exceptions.ContentParseException;
import org.techteam.bashhappens.content.exceptions.FeedOverException;

public class GetPostsProcessor extends Processor {
    private final ContentSource contentSource;
    private final int loadIntention;

    public GetPostsProcessor(Context context, ContentSource contentSource, int loadIntention) {
        super(context);
        this.contentSource = contentSource;
        this.loadIntention = loadIntention;
    }

    @Override
    public void start(ProcessorCallback cb) {
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

            System.out.println("DONE! list is null");
        }

        // TODO: basing on loadIntention decide how to operate with data
        // TODO: write to db

        // TODO: make a table for each ContentSection
        ContentSection section = contentSource.getSection();
        switch (section) {
            case BASH_ORG_NEWEST:
                break;
            case IT_HAPPENS_NEWEST:
                break;
        }

        cb.onSuccess();
    }
}
