package org.techteam.bashhappens.content;

import org.techteam.bashhappens.content.bashorg.newest.BashOrgNewest;

public class ContentFactory {
    public enum ContentSection {
        BASH_ORG_NEWEST,

        IT_HAPPENS_NEWEST
    }

    private String locale;

    public ContentFactory(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    public ContentSource buildContent(ContentSection section) {
        switch (section) {
            case BASH_ORG_NEWEST:
                return new BashOrgNewest(locale);
            case IT_HAPPENS_NEWEST:
                break;
        }
        return null;
    }
}
