package org.techteam.bashhappens.content.bashorg.best;

import android.content.Context;
import android.os.Parcel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.techteam.bashhappens.content.Constants;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.bashorg.BashOrg;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;
import org.techteam.bashhappens.content.exceptions.ContentParseException;
import org.techteam.bashhappens.content.exceptions.FeedOverException;
import org.techteam.bashhappens.net.Headers;
import org.techteam.bashhappens.net.HttpDownloader;

import java.io.IOException;

public class BashOrgBest extends BashOrg {

    public static final String BASH_URL = "http://bash.im/best/";

    private boolean feedOver = false;

    public BashOrgBest(String locale, ContentSection section) {
        super(locale, section);
    }

    private BashOrgList retrieveList(Context context) throws IOException {
        String url = BASH_URL;

        String page = downloadPage(url);

        Document html = Jsoup.parse(page);
        BashOrgListBest list = BashOrgListBest.fromHtml(context, html);

        return list;
    }

    @Override
    public ContentList<BashOrgEntry> retrieveNextList(Context context) throws FeedOverException, ContentParseException {
        if (feedOver) {
            throw new FeedOverException();
        }

        BashOrgList list = null;
        try {
            list = retrieveList(context);
        } catch (Exception e) {
            throw new ContentParseException(e);
        }

        // TODO: I have no idea if there could be more than one page.
        // TODO: So, for now, will set feedOver after first retrieval
        feedOver = true;

        return list;
    }

    @Override
    public String getFootprint() {
        return Boolean.toString(feedOver);
    }

    @Override
    public void loadFootprint(String footprint) {
        if (footprint != null) {
            feedOver = Boolean.valueOf(footprint);
        }
    }




    public BashOrgBest(Parcel in) {
        super(in.readString(), Enum.valueOf(ContentSection.class, in.readString()));

        boolean[] vals = new boolean[1];
        in.readBooleanArray(vals);
        feedOver = vals[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(locale);
        parcel.writeString(getSection().toString());
        parcel.writeBooleanArray(new boolean[] {
                feedOver
        });
    }

    public static final Creator<BashOrgBest> CREATOR
            = new Creator<BashOrgBest>() {
        public BashOrgBest createFromParcel(Parcel in) {
            return new BashOrgBest(in);
        }

        public BashOrgBest[] newArray(int size) {
            return new BashOrgBest[size];
        }
    };
}
