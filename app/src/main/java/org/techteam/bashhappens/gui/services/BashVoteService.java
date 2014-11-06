package org.techteam.bashhappens.gui.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.techteam.bashhappens.content.Constants;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.exceptions.VoteException;
import org.techteam.bashhappens.content.resolvers.AbstractContentResolver;
import org.techteam.bashhappens.content.resolvers.BashBayanResolver;
import org.techteam.bashhappens.content.resolvers.BashLikesResolver;
import org.techteam.bashhappens.net.HttpDownloader;
import org.techteam.bashhappens.net.UrlParams;

import java.io.IOException;

public class BashVoteService extends BashService {

    private static final String NAME = BashVoteService.class.getName();

    public static final class IntentKeys {
        public static final String ID = "id";
        public static final String RATING = "rating";
        public static final String DIRECTION = "direction";
        public static final String ENTRY_POSITION = "entryPosition";
        public static final String BAYAN = "bayan";
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
        boolean bayaning = intent.getBooleanExtra(IntentKeys.BAYAN, false);

        Intent localIntent;
        try {
            if (bayaning) {
                // Bayan

                makeBayan(id);

                AbstractContentResolver resolver = new BashBayanResolver();
                resolver.insertEntry(this, new BashOrgEntry().setId(id)
                        .setBayan(true));

                localIntent = BroadcasterIntentBuilder.buildBayanSuccessIntent(entryPosition, id);

            } else {
                // Normal rating

                if (direction == 0) {
                    throw new VoteException("Invalid vote direction");
                }

                String newRating = changeRating(id, rating, direction);

                AbstractContentResolver resolver = new BashLikesResolver();
                resolver.insertEntry(this, new BashOrgEntry().setId(id)
                        .setDirection(direction));

                localIntent = BroadcasterIntentBuilder.buildVoteSuccessIntent(entryPosition, id, newRating);
            }
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

    private void makeBayan(String id) throws IOException, VoteException {
        boolean status = vote(id, 0);
        if (!status) {
            throw new VoteException();
        }
    }

    private String changeRating(String id, String currentRating, int direction) throws VoteException, IOException {
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

    private boolean vote(String id, int direction) throws IOException, VoteException {
        String url;
        String act;
        if (direction == +1) {
            // UP
            url = Urls.getVoteUp(id);
            act = Urls.ACT_UP;
        } else if (direction == -1) {
            // DOWN
            url = Urls.getVoteDown(id);
            act = Urls.ACT_DOWN;
        } else if (direction == 0) {
            // BAYAN
            url = Urls.getVoteBayan(id);
            act = Urls.ACT_BAYAN;
        } else {
            throw new VoteException("Invalid direction o vote");
        }


        UrlParams data = new UrlParams().add("quote", id)
                                        .add("act", act);
        String resp = HttpDownloader.httpPost(new HttpDownloader.Request(url, data, null, Constants.ENCODING));
        return resp.equals("OK");
    }
}
