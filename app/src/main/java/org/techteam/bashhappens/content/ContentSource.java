package org.techteam.bashhappens.content;

import java.io.IOException;

public abstract class ContentSource<T extends ContentEntry> {
    protected String locale;

    protected ContentSource(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    public abstract ContentList<T> retrieveNextList() throws IOException, FeedOverflowException;
}
