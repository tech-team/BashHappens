package org.techteam.bashhappens.content.bashorg.newest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.FeedOverflowException;
import org.techteam.bashhappens.content.bashorg.BashOrg;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;
import org.techteam.bashhappens.net.Headers;
import org.techteam.bashhappens.net.HttpDownloader;

import java.io.IOException;

public class BashOrgNewest extends BashOrg {

    public static final String BASH_URL = "http://bash.im/index/";
    public static final String ENCODING = "cp1251";

    private static final int NO_PAGE = -1;

    private int minPage = NO_PAGE;
    private int maxPage = NO_PAGE;
    private int currentPage = NO_PAGE;

    public BashOrgNewest(String locale) {
        super(locale);
    }

    private BashOrgList retrieveList(int pageNum) throws IOException {
        String url = BASH_URL;
        if (pageNum != NO_PAGE) {
            url += pageNum;
        }

        Headers headers = new Headers().add("Accept-Language", locale);
        String page = HttpDownloader.httpGet(new HttpDownloader.Request(url, null, headers, ENCODING));

        Document html = Jsoup.parse(page);
        BashOrgListNewest list = BashOrgListNewest.fromHtml(html);
        minPage = list.getMinPageNum();
        maxPage = list.getMaxPageNum();
        currentPage = list.getPageNum();

        return list;
    }

    @Override
    public ContentList<BashOrgEntry> retrieveNextList() throws IOException, FeedOverflowException {
        BashOrgList list = retrieveList(currentPage);
        if (currentPage <= minPage && currentPage >= maxPage) {
            throw new FeedOverflowException("Feed is out of bound");
        }
        --currentPage;
        return list;
    }
}
