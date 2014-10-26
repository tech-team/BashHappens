package org.techteam.bashhappens.content.bashorg;

import android.util.Log;

import org.jsoup.nodes.Element;
import org.techteam.bashhappens.content.Constants;
import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentType;
import org.techteam.bashhappens.content.exceptions.VoteException;
import org.techteam.bashhappens.net.HttpDownloader;

import java.io.IOException;

public class BashOrgEntry extends ContentEntry {
    private static final String LOG_TAG = BashOrgEntry.class.toString();

    public static final class DOM {
        public static final String DOM_CLASS_NAME = "quote";
        private static final String ACTIONS_BAR_CLASS = "actions";
        private static final String TEXT_CLASS = "text";
        private static final String RATING_CLASS = "rating";
    }

    public static final class Urls {
        private static final String VOTE_UP = "http://bash.im/quote/%s/rulez";
        private static final String VOTE_DOWN = "http://bash.im/quote/%s/sux";

        public static String getVoteUp(String id) {
            return String.format(VOTE_UP, id);
        }

        public static String getVoteDown(String id) {
            return String.format(VOTE_DOWN, id);
        }
    }

    public static final ContentType CONTENT_TYPE = ContentType.BASH_ORG;

    //TODO: add string constant names of fields
    private String id;
    private String creationDate;
    private String text;
    private String rating;

    public BashOrgEntry(String id, String creationDate, String text, String rating) {
        super(CONTENT_TYPE);
        this.id = id;
        this.creationDate = creationDate;
        this.text = text;
        this.rating = rating;
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

    public void increaseRating() throws VoteException, IOException {
        changeRating(+1);
    }

    public void decreaseRating() throws VoteException, IOException {
        changeRating(-1);
    }

    private void changeRating(int direction) throws VoteException, IOException {
        try {
            int ratingInt = Integer.parseInt(rating);

            boolean status = vote(direction);
            if (status) {
                ++ratingInt;
                rating = ((Integer) ratingInt).toString();
            } else {
                throw new VoteException();
            }

        } catch (NumberFormatException ignored) {
            boolean status = vote(direction);
            if (!status) {
                // TODO: probably need to do something on good status. We can mimic bash.im behaviour.
                throw new VoteException();
            }
        }

    }

    private boolean vote(int direction) throws IOException {
        // TODO: send request
        String url;
        if (direction > 0) {
            // UP
            url = Urls.getVoteUp(id);
        } else {
            // DOWN
            url = Urls.getVoteDown(id);
        }

        String resp = HttpDownloader.httpPost(new HttpDownloader.Request(url, Constants.ENCODING));
        return resp.equals("OK");
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
