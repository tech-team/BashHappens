package org.techteam.bashhappens.gui.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.techteam.bashhappens.content.Constants;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.exceptions.VoteException;
import org.techteam.bashhappens.content.resolvers.AbstractContentResolver;
import org.techteam.bashhappens.content.resolvers.BashLikesResolver;
import org.techteam.bashhappens.net.HttpDownloader;
import org.techteam.bashhappens.net.UrlParams;

import java.io.IOException;

public class BashVoteService extends IntentService {

    private static final String NAME = BashVoteService.class.getName();

    public static final class IntentKeys {
        public static final String ID = "id";
        public static final String RATING = "rating";
        public static final String DIRECTION = "direction";
        public static final String ENTRY_POSITION = "entryPosition";
    }

    public static final class Urls {
        private static final String VOTE_UP = "http://bash.im/quote/%s/%s";
        private static final String VOTE_DOWN = "http://bash.im/quote/%s/%s";

        public static final String ACT_UP = "rulez";
        public static final String ACT_DOWN = "sux";

        public static String getVoteUp(String id) {
            return String.format(VOTE_UP, id, ACT_UP);
        }

        public static String getVoteDown(String id) {
            return String.format(VOTE_DOWN, id, ACT_DOWN);
        }
    }

    public BashVoteService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int entryPosition = intent.getIntExtra(IntentKeys.ENTRY_POSITION, -1);

        String id = intent.getStringExtra(IntentKeys.ID);
        String rating = intent.getStringExtra(IntentKeys.RATING);
        int direction = intent.getIntExtra(IntentKeys.DIRECTION, 0);

        Intent localIntent;
        try {
            if (direction == 0) {
                throw new VoteException("Invalid vote direction");
            }

            String newRating = changeRating(id, rating, direction);

            AbstractContentResolver resolver = new BashLikesResolver();
            resolver.insertEntry(this, new BashOrgEntry().setId(id)
                                                         .setDirection(direction));

            localIntent = BroadcasterIntentBuilder.buildVoteSuccessIntent(entryPosition, id, newRating);
        }
        catch (IOException e) {
            Log.w(NAME, e);
            localIntent = BroadcasterIntentBuilder.buildVoteErrorIntent(entryPosition, id, e.getMessage());
        } catch (VoteException e) {
            Log.w(NAME, e);
            localIntent = BroadcasterIntentBuilder.buildVoteErrorIntent(entryPosition, id, e.getMessage());
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private String changeRating(String id, String currentRating, int direction) throws VoteException, IOException {
        // TODO: add DB communication

        String newRating = currentRating;
        try {
            int ratingInt = Integer.parseInt(currentRating);

            boolean status = vote(id, direction);
            if (status) {
                ratingInt = direction > 0 ? ratingInt + 1: ratingInt - 1;
                newRating = ((Integer) ratingInt).toString();
            } else {
                throw new VoteException();
            }

        } catch (NumberFormatException ignored) {
            boolean status = vote(id, direction);
            if (!status) {
                // TODO: probably need to do something on good status. We can mimic bash.im behaviour.
                // TODO: newRating = ???
                throw new VoteException();
            }
        }
        return newRating;
    }

    private boolean vote(String id, int direction) throws IOException {
        // TODO: send request
        String url;
        String act;
        if (direction > 0) {
            // UP
            url = Urls.getVoteUp(id);
            act = Urls.ACT_UP;
        } else {
            // DOWN
            url = Urls.getVoteDown(id);
            act = Urls.ACT_DOWN;
        }


        UrlParams data = new UrlParams().add("quote", id)
                                        .add("act", act);
        String resp = HttpDownloader.httpPost(new HttpDownloader.Request(url, data, null, Constants.ENCODING));
        return resp.equals("OK");
    }
}
