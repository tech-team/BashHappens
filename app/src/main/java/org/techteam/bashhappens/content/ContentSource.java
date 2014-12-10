package org.techteam.bashhappens.content;

import android.content.Context;
import android.os.Parcelable;

import org.techteam.bashhappens.content.exceptions.ContentParseException;
import org.techteam.bashhappens.content.exceptions.FeedOverException;

import java.io.IOException;

public abstract class ContentSource<T extends ContentEntry> implements Parcelable {
    protected String locale;
    private ContentSection section;

    protected ContentSource(String locale, ContentSection section) {
        this.locale = locale;
        this.section = section;
    }

    public ContentSection getSection() {
        return section;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public abstract ContentList<T> retrieveNextList(Context context) throws FeedOverException, ContentParseException;

    public abstract String getFootprint();
}
