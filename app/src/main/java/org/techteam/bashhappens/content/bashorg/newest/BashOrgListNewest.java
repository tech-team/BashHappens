package org.techteam.bashhappens.content.bashorg.newest;

import android.util.Log;

import org.jsoup.nodes.Element;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;

import java.util.ArrayList;

public class BashOrgListNewest extends BashOrgList {
    private static final String LOG_TAG = BashOrgListNewest.class.toString();

    private static final String PAGER_CLASS= "page";

    private int minPageNum;
    private int maxPageNum;
    private int pageNum;


    public BashOrgListNewest(int minPageNum, int maxPageNum, int pageNum, ArrayList<BashOrgEntry> entries) {
        super(entries);
        this.minPageNum = minPageNum;
        this.maxPageNum = maxPageNum;
        this.pageNum = pageNum;
    }

    public int getMinPageNum() {
        return minPageNum;
    }

    public int getMaxPageNum() {
        return maxPageNum;
    }

    public int getPageNum() {
        return pageNum;
    }

    public static BashOrgListNewest fromHtml(Element html) {
        try {
            Element pager = html.getElementsByClass(PAGER_CLASS).get(0);

            int minPageNum = Integer.parseInt(pager.attr("min"));
            int maxPageNum = Integer.parseInt(pager.attr("max"));
            int pageNum = Integer.parseInt(pager.attr("value"));

            ArrayList<BashOrgEntry> entries = BashOrgList.listFromHtml(html);

            return new BashOrgListNewest(minPageNum, maxPageNum, pageNum, entries);
        } catch (Exception e) {
            Log.w(LOG_TAG, e);
            return null;
        }
    }

}
