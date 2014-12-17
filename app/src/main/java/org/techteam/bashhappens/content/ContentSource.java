package org.techteam.bashhappens.content;

import android.content.Context;
import android.os.Parcelable;

import org.techteam.bashhappens.content.exceptions.ContentParseException;
import org.techteam.bashhappens.content.exceptions.FeedOverException;
import org.techteam.bashhappens.net.Headers;
import org.techteam.bashhappens.net.HttpDownloader;

import java.io.IOException;

public abstract class ContentSource<T extends ContentEntry> implements Parcelable {
    protected String locale;
    private ContentSection section;
    protected static final String FOOTPRINT_DELIM = "_";

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
    public abstract void loadFootprint(String footprint);
    protected String downloadPage(String url) throws IOException {
        Headers headers = new Headers().add("Accept-Language", locale);
        String page = HttpDownloader.httpGet(new HttpDownloader.Request(url, null, headers, Constants.ENCODING));
        return page;
    }
}
