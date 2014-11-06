package org.techteam.bashhappens.gui.loaders;

import android.content.Context;

import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.exceptions.ContentParseException;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.exceptions.FeedOverException;

public class ContentAsyncLoader extends MyAsyncLoader<ContentLoaderResult> {

    public static class BundleKeys {
        public static final String LOAD_INTENT = "loadIntent";
    }

    private final ContentSource contentSource;
    private final int loadIntention;

    public ContentAsyncLoader(Context context, ContentSource contentSource, int loadIntention) {
        super(context);
        this.contentSource = contentSource;
        this.loadIntention = loadIntention;
    }

    @Override
    public ContentLoaderResult loadInBackground() {
        ContentList list = null;
        Throwable exc = null;
        try {
            list = contentSource.retrieveNextList(getContext());
        } catch (FeedOverException e) {
            exc = e;
        } catch (ContentParseException e) {
            exc = e;
        }
        return new ContentLoaderResult(list, exc, loadIntention);
    }
}
