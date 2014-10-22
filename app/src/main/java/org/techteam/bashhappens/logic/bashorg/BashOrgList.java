package org.techteam.bashhappens.logic.bashorg;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class BashOrgList {
    private static final String PAGER_CLASS= "page";

    private int minPageNum;
    private int maxPageNum;
    private int pageNum;
    private ArrayList<BashOrgEntry> entries;

    public BashOrgList(int minPageNum, int maxPageNum, int pageNum, ArrayList<BashOrgEntry> entries) {
        this.minPageNum = minPageNum;
        this.maxPageNum = maxPageNum;
        this.pageNum = pageNum;
        this.entries = entries;
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

    public ArrayList<BashOrgEntry> getEntries() {
        return entries;
    }

    public static BashOrgList fromHtml(Element html) {
        // TODO: handle all jsoup exceptions
        Element pager = html.getElementsByClass(PAGER_CLASS).get(0);

        int minPageNum = Integer.parseInt(pager.attr("min"));
        int maxPageNum = Integer.parseInt(pager.attr("max"));
        int pageNum = Integer.parseInt(pager.attr("value"));


        Elements items = html.getElementsByClass(BashOrgEntry.DOM_CLASS_NAME);

        ArrayList<BashOrgEntry> entries = new ArrayList<BashOrgEntry>();
        for (Element item : items) {
            BashOrgEntry entry = BashOrgEntry.fromHtml(item);
            if (entry != null)
                entries.add(entry);
        }

        return new BashOrgList(minPageNum, maxPageNum, pageNum, entries);
    }
}
