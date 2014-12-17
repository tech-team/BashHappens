package org.techteam.bashhappens.content.bashorg;

import android.content.Context;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.techteam.bashhappens.content.ContentList;

import java.util.ArrayList;

public class BashOrgList extends ContentList<BashOrgEntry> {
    private static final String LOG_TAG = BashOrgList.class.toString();

    public BashOrgList(ArrayList<BashOrgEntry> entries) {
        super(entries);
    }

    public BashOrgList() {
    }

    protected static ArrayList<BashOrgEntry> listFromHtml(Context context, Element html) {
        Elements items = html.getElementsByClass(BashOrgEntry.DOM.DOM_CLASS_NAME);

        ArrayList<BashOrgEntry> entries = new ArrayList<BashOrgEntry>();
        for (Element item : items) {
            BashOrgEntry entry = BashOrgEntry.fromHtml(item);
            if (entry != null) {
                entries.add(entry);
            }

        }

        return entries;
    }
}
