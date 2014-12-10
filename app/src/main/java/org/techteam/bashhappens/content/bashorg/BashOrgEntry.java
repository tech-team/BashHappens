package org.techteam.bashhappens.content.bashorg;

import android.content.Context;

import org.jsoup.nodes.Element;
import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentType;

public class BashOrgEntry extends ContentEntry {
    private static final String LOG_TAG = BashOrgEntry.class.toString();
    private String link = "http://bash.im/quote/"; //TODO: fixme

    public static final class DOM {
        public static final String DOM_CLASS_NAME = "quote";
        private static final String ACTIONS_BAR_CLASS = "actions";
        private static final String TEXT_CLASS = "text";
        private static final String RATING_CLASS = "rating";
    }

    public enum VoteDirection {
        UP,
        DOWN,
        BAYAN;

        public int toDirection() {
            if (this == UP) {
                return +1;
            } else if (this == DOWN) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public static final ContentType CONTENT_TYPE = ContentType.BASH_ORG;

    private String id;
    private String creationDate;
    private String text;
    private String rating;
    private int direction = 0;
    private boolean isBayan;

    public BashOrgEntry(String id, String creationDate, String text, String rating) {
        super(CONTENT_TYPE);
        this.id = id;
        this.creationDate = creationDate;
        this.text = text;
        this.rating = rating;
    }
    public BashOrgEntry(String id, String creationDate, String text, String rating, int direction, boolean isBayan) {
        super(CONTENT_TYPE);
        this.id = id;
        this.creationDate = creationDate;
        this.text = text;
        this.rating = rating;
        this.direction = direction;
        this.isBayan = isBayan;
    }

    public BashOrgEntry() {
        super(CONTENT_TYPE);
    }

    public String getId() {
        return id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getText() {
        return text;
    }

    public String getRating() {
        return rating;
    }

    public String getLink() {
        return link + getId();
    }

    public BashOrgEntry setRating(String rating) {
        this.rating = rating;
        return this;
    }

    public int getDirection() {
        return direction;
    }

    public boolean getIsBayan() {
        return isBayan;
    }

    public BashOrgEntry setId(String id) {
        this.id = id;
        return this;
    }

    public BashOrgEntry setCreationDate(String creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public BashOrgEntry setText(String text) {
        this.text = text;
        return this;
    }

    public BashOrgEntry setDirection(int direction) {
        this.direction = direction;
        return this;
    }

    public BashOrgEntry setBayan(boolean isBayan) {
        this.isBayan = isBayan;
        return this;
    }

    public static BashOrgEntry fromHtml(Element element) {
        Element actionsBar;
        String text;
        try {
            actionsBar = element.getElementsByClass(DOM.ACTIONS_BAR_CLASS).get(0);
            text = element.getElementsByClass(DOM.TEXT_CLASS).get(0).html();
            text = text.replaceAll("<br.*>", "");
        } catch (Exception ignored) {
            return null;
        }

        String id = actionsBar.getElementsByClass("id").get(0).text().substring(1);
        String creationDate = actionsBar.getElementsByClass("date").text();
        String rating = actionsBar.getElementsByClass(DOM.RATING_CLASS)
                                                .get(0).text();

        return new BashOrgEntry(id, creationDate, text, rating);
    }


//    public BashOrgEntry(Parcel in) {
//        super(CONTENT_TYPE);
//
//        String[] vals = new String[3];
//        in.readStringArray(vals);
//
//        id = vals[0];
//        creationDate = vals[1];
//        text = vals[2];
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeStringArray(new String[] {
//                id,
//                creationDate,
//                text
//        });
//    }
//
//    public static final Parcelable.Creator<BashOrgEntry> CREATOR
//            = new Parcelable.Creator<BashOrgEntry>() {
//        public BashOrgEntry createFromParcel(Parcel in) {
//            return new BashOrgEntry(in);
//        }
//
//        public BashOrgEntry[] newArray(int size) {
//            return new BashOrgEntry[size];
//        }
//    };
}
