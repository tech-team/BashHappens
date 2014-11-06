package org.techteam.bashhappens.gui.loaders;

import org.techteam.bashhappens.content.ContentList;

public class ContentLoaderResult {
    private ContentList contentList;
    private Throwable exception;
    private int loadIntention;


    public ContentLoaderResult(ContentList contentList, Throwable exception, int loadIntention) {
        this.contentList = contentList;
        this.exception = exception;
        this.loadIntention = loadIntention;
    }

    public ContentList getContentList() {
        return contentList;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean hasError() {
        return exception != null;
    }

    public int getLoadIntention() {
        return loadIntention;
    }
}
