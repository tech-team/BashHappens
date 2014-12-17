package org.techteam.bashhappens.content.bashorg.newest;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.techteam.bashhappens.content.Constants;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.exceptions.ContentParseException;
import org.techteam.bashhappens.content.exceptions.FeedOverException;
import org.techteam.bashhappens.content.bashorg.BashOrg;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;
import org.techteam.bashhappens.net.Headers;
import org.techteam.bashhappens.net.HttpDownloader;

import java.io.IOException;

public class BashOrgNewest extends BashOrg {

    public static final String BASH_URL = "http://bash.im/index/";

    private static final int NO_PAGE = -1;

    private int minPage = NO_PAGE;
    private int maxPage = NO_PAGE;
    private int currentPage = NO_PAGE;
    private boolean feedOver = false;

    public BashOrgNewest(String locale, ContentSection section) {
        super(locale, section);
    }

    private BashOrgList retrieveList(Context context, int pageNum) throws IOException {
        String url = BASH_URL;
        if (pageNum != NO_PAGE) {
            url += pageNum;
        }

        String page = downloadPage(url);

        Document html = Jsoup.parse(page);
        BashOrgListNewest list = BashOrgListNewest.fromHtml(context, html);

        if (list != null) {
            minPage = list.getMinPageNum();
            maxPage = list.getMaxPageNum();
            currentPage = list.getPageNum();
        }

        return list;
    }

    @Override
    public ContentList<BashOrgEntry> retrieveNextList(Context context) throws FeedOverException, ContentParseException {
        if (feedOver) {
            throw new FeedOverException("Feed is over");
        }

        BashOrgList list = null;
        try {
            list = retrieveList(context, currentPage);
        } catch (Exception e) {
            throw new ContentParseException(e);
        }

        if (checkFeedOver()) {
            feedOver = true;
        } else {
            --currentPage;
        }

        return list;
    }

    @Override
    public String getFootprint() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(minPage));
        sb.append(FOOTPRINT_DELIM);
        sb.append(Integer.toString(currentPage));
        sb.append(FOOTPRINT_DELIM);
        sb.append(Integer.toString(maxPage));
        return sb.toString();
    }

    @Override
    public void loadFootprint(String footprint) {
        if (footprint != null) {
            String[] parts = footprint.split(FOOTPRINT_DELIM);
            try {
                int newMinPage = Integer.parseInt(parts[0]);
                int newCurrentPage = Integer.parseInt(parts[1]);
                int newMaxPage = Integer.parseInt(parts[2]);

                minPage = newMinPage;
                currentPage = newCurrentPage;
                maxPage = newMaxPage;
            } catch (NumberFormatException ignored) {
            }

        }
    }

    private boolean checkFeedOver() {
        if (currentPage == minPage && minPage == maxPage && maxPage == NO_PAGE) {
            return feedOver;
        } else {
            return currentPage <= minPage && currentPage >= maxPage;
        }
    }




    public BashOrgNewest(Parcel in) {
        super(in.readString(), Enum.valueOf(ContentSection.class, in.readString())); // TODO: review the order

        int[] vals = new int[3];
        in.readIntArray(vals);
        minPage = vals[0];
        maxPage = vals[1];
        currentPage = vals[2];
        feedOver = checkFeedOver();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(locale);
        parcel.writeString(getSection().toString());
        parcel.writeIntArray(new int[]{
                minPage,
                maxPage,
                currentPage
        });
    }

    public static final Parcelable.Creator<BashOrgNewest> CREATOR
            = new Parcelable.Creator<BashOrgNewest>() {
        public BashOrgNewest createFromParcel(Parcel in) {
            return new BashOrgNewest(in);
        }

        public BashOrgNewest[] newArray(int size) {
            return new BashOrgNewest[size];
        }
    };
}
