package org.techteam.bashhappens.content.bashorg;

import org.techteam.bashhappens.content.IContent;

public abstract class BashOrg implements IContent {

    protected String locale;

    protected BashOrg(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }
}

