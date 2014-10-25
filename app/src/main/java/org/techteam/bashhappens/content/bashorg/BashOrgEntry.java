package org.techteam.bashhappens.content.bashorg;

import android.os.Parcel;
import android.os.Parcelable;

import org.jsoup.nodes.Element;
import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentType;

public class BashOrgEntry extends ContentEntry {
    public static final String DOM_CLASS_NAME = "quote";
    private static final String ACTIONS_BAR_CLASS = "actions";
    private static final String TEXT_CLASS = "text";

    public static final ContentType CONTENT_TYPE = ContentType.BASH_ORG;

    //TODO: add string constant names of fields
    private String id;
    private String creationDate;
    private String text;

    public BashOrgEntry(String id, String creationDate, String text) {
        super(CONTENT_TYPE);
        this.id = id;
        this.creationDate = creationDate;
        this.text = text;
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

    public static BashOrgEntry fromHtml(Element element) {
        Element actionsBar;
        String text;
        try {
            actionsBar = element.getElementsByClass(ACTIONS_BAR_CLASS).get(0);
            text = element.getElementsByClass(TEXT_CLASS).get(0).text();
        } catch (Exception ignored) {
            return null;
        }

        String id = actionsBar.getElementsByClass("id").get(0).text().substring(1);
        String creationDate = actionsBar.getElementsByClass("date").text();

        return new BashOrgEntry(id, creationDate, text);
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
