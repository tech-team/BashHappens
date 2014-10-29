package org.techteam.bashhappens.gui.loaders;

import android.content.Context;

import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.exceptions.ContentParseException;
import org.techteam.bashhappens.content.exceptions.FeedOverException;

import java.io.IOException;

public class VoteBashAsyncLoader extends MyAsyncLoader<Void> {

    public VoteBashAsyncLoader(Context context) {
        super(context);
    }

    @Override
    public Void loadInBackground() {

        return null;
    }
}
