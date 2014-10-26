package org.techteam.bashhappens.fragments.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentParseException;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.FeedOverflowException;

import java.io.IOException;

public class ContentAsyncLoader extends AsyncTaskLoader<ContentList> {
    private final ContentSource contentSource;
    private ContentList mData = null;

    public ContentAsyncLoader(Context context, ContentSource contentSource) {
        super(context);
        this.contentSource = contentSource;
    }

    @Override
    public ContentList loadInBackground() {
        ContentList list = null;
        try {
            list = contentSource.retrieveNextList();
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        } catch (FeedOverflowException e) {
            // TODO
            e.printStackTrace();
        } catch (ContentParseException e) {
            // TODO: display a parse error; so nothing cannot be displayed
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void deliverResult(ContentList data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        ContentList oldData = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }
    }

    @Override
    public void onCanceled(ContentList data) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    private void releaseResources(ContentList data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }
}
