package org.techteam.bashhappens.content;


import org.techteam.bashhappens.content.bashorg.BashOrgEntry;

public abstract class ContentEntry {
    private ContentType contentType;
    private boolean favorite;

    protected ContentEntry(ContentType contentType) {
        this.contentType = contentType;
    }

    public ContentType getContentType() {
        return contentType;
    }


    public BashOrgEntry toBashOrgEntry() {
        return (BashOrgEntry) this;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public ContentEntry setFavorite(boolean favorite) {
        this.favorite = favorite;
        return this;
    }

}
