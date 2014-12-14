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

            //TODO: likes are temporary unavailable. Availabilate them

            if (entry != null) {

//                AbstractContentResolver resolver = new BashLikesResolver();
//                ContentList tempList
//                        = resolver.getEntries(context,
//                                             null,
//                                             BashLikes.ARTICLE_ID,
//                                             new String[] {entry.getId()},
//                                             null);
//
//                if (tempList.getEntries().size() != 0) {
//                    BashOrgEntry tempEntry = (BashOrgEntry) tempList.getEntries().get(0);
//                    entry.setDirection(tempEntry.getDirection());
//                }

                entries.add(entry);
            }

        }

        return entries;
    }
}
