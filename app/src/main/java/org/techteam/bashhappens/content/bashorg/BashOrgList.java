package org.techteam.bashhappens.content.bashorg;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.techteam.bashhappens.content.ContentList;

import java.util.ArrayList;

public abstract class BashOrgList extends ContentList<BashOrgEntry> {

    public BashOrgList(ArrayList<BashOrgEntry> entries) {
        super(entries);
    }

    protected static ArrayList<BashOrgEntry> listFromHtml(Element html) {
        Elements items = html.getElementsByClass(BashOrgEntry.DOM_CLASS_NAME);

        ArrayList<BashOrgEntry> entries = new ArrayList<BashOrgEntry>();
        for (Element item : items) {
            BashOrgEntry entry = BashOrgEntry.fromHtml(item);
            if (entry != null)
                entries.add(entry);
        }

        return entries;
    }
}
