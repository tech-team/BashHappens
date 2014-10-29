package org.techteam.bashhappens.gui.loaders;

import android.content.Context;

import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.exceptions.ContentParseException;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.exceptions.FeedOverException;

import java.io.IOException;

public class ContentAsyncLoader extends MyAsyncLoader<ContentList> {
    private final ContentSource contentSource;

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
        } catch (FeedOverException e) {
            // TODO
            e.printStackTrace();
        } catch (ContentParseException e) {
            // TODO: display a parse error; so nothing cannot be displayed
            e.printStackTrace();
        }
        return list;
    }
}
