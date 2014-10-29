package org.techteam.bashhappens.content;

import android.content.Context;
import android.os.Parcelable;

import org.techteam.bashhappens.content.exceptions.ContentParseException;
import org.techteam.bashhappens.content.exceptions.FeedOverException;

import java.io.IOException;

public abstract class ContentSource<T extends ContentEntry> implements Parcelable {
    protected String locale;

    protected ContentSource(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public abstract ContentList<T> retrieveNextList(Context context) throws IOException, FeedOverException, ContentParseException;
}
