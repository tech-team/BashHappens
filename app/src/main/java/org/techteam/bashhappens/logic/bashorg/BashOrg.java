package org.techteam.bashhappens.logic.bashorg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.techteam.bashhappens.logic.FeedOverflowException;
import org.techteam.bashhappens.logic.Parsable;
import org.techteam.bashhappens.net.Header;
import org.techteam.bashhappens.net.HttpDownloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BashOrg implements Parsable {

    public static final String BASH_URL = "http://bash.im/index/";
    public static final String ENCODING = "cp1251";

    private static int NO_PAGE = -1;

    private static BashOrg instance = null;
    private static final Object instanceLock = new Object();

    private int minPage = NO_PAGE;
    private int maxPage = NO_PAGE;
    private int currentPage = NO_PAGE;

    public static BashOrg getInstance() {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new BashOrg();
                }
            }
        }
        return instance;
    }

    private BashOrgList retrieveList(String locale, int pageNum) throws IOException {
        String url = BASH_URL;
        if (pageNum != NO_PAGE) {
            url += pageNum;
        }
        List<Header> headers = new ArrayList<Header>();
        headers.add(new Header("Accept-Language", locale));
        String page = HttpDownloader.httpGet(new HttpDownloader.Request(url, null, headers, ENCODING));

        Document html = Jsoup.parse(page);
        BashOrgList list = BashOrgList.fromHtml(html.body());
        minPage = list.getMinPageNum();
        maxPage = list.getMaxPageNum();
        currentPage = list.getPageNum();

        return list;
    }

    public BashOrgList retrieveNextList(String locale) throws IOException, FeedOverflowException {
        BashOrgList list = retrieveList(locale, currentPage);
        if (currentPage <= minPage && currentPage >= maxPage) {
            throw new FeedOverflowException("Feed is out of bound");
        }
        --currentPage;
        return list;
    }

}
