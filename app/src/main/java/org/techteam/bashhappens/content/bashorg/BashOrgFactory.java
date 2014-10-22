package org.techteam.bashhappens.content.bashorg;

import org.techteam.bashhappens.content.IContent;
import org.techteam.bashhappens.content.bashorg.newest.BashOrgNewest;

public class BashOrgFactory {

    private String locale;

    public BashOrgFactory(String locale) {
        this.locale = locale;
    }

    public IContent buildBashOrgNewest() {
        return new BashOrgNewest(locale);
    }
}
