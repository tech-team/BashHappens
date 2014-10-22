package org.techteam.bashhappens.content.bashorg;

import org.techteam.bashhappens.content.ContentSource;

public abstract class BashOrg implements ContentSource {

    protected String locale;

    protected BashOrg(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }
}

