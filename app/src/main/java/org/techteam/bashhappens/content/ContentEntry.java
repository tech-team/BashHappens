package org.techteam.bashhappens.content;


import org.techteam.bashhappens.content.bashorg.BashOrgEntry;

public abstract class ContentEntry {
    private ContentType contentType;

    protected ContentEntry(ContentType contentType) {
        this.contentType = contentType;
    }

    public ContentType getContentType() {
        return contentType;
    }


    public BashOrgEntry toBashOrgEntry() {
        return (BashOrgEntry) this;
    }


}
