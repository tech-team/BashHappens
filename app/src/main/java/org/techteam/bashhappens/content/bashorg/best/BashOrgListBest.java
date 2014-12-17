package org.techteam.bashhappens.content.bashorg.best;

import android.content.Context;

import org.jsoup.nodes.Element;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;

import java.util.ArrayList;

public class BashOrgListBest extends BashOrgList {
    private static final String LOG_TAG = BashOrgListBest.class.toString();

    public BashOrgListBest(ArrayList<BashOrgEntry> entries) {
        super(entries);
    }

    public static BashOrgListBest fromHtml(Context context, Element html) {
        ArrayList<BashOrgEntry> entries = BashOrgList.listFromHtml(context, html);

        return new BashOrgListBest(entries);
    }

}
