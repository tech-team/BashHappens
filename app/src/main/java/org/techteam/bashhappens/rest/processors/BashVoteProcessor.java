package org.techteam.bashhappens.rest.processors;

import android.content.Context;
import android.util.Log;

import org.techteam.bashhappens.content.Constants;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.exceptions.VoteException;
import org.techteam.bashhappens.content.resolvers.AbstractContentResolver;
import org.techteam.bashhappens.gui.services.BashService;
import org.techteam.bashhappens.net.HttpDownloader;
import org.techteam.bashhappens.net.UrlParams;

import java.io.IOException;

public class BashVoteProcessor extends Processor {
    private static final String NAME = BashVoteProcessor.class.getName();

    private final int entryPosition;
    private final String entryId;
    private final String rating;
    private final int direction;
    private final boolean bayaning;

    public BashVoteProcessor(Context context, int entryPosition, String entryId, String rating, int direction, boolean bayaning) {
        super(context);
        this.entryPosition = entryPosition;
        this.entryId = entryId;
        this.rating = rating;
        this.direction = direction;
        this.bayaning = bayaning;
    }

    @Override
    public void start(ProcessorCallback cb) {
        try {
            if (bayaning) {
                // Bayan

                makeBayan(entryId);

                //TODO: decide about bayan/likes system and implement
                /*AbstractContentResolver resolver = new BashBayanResolver();
                resolver.insertEntry(getContext(), new BashOrgEntry().setId(entryId)
                                                                     .setBayan(true));*/

                cb.onSuccess();

            } else {
                // Normal rating

                if (direction == 0) {
                    throw new VoteException("Invalid vote direction");
                }

                String newRating = changeRating(entryId, rating, direction);

                //TODO: decide about bayan/likes system and implement
                /*AbstractContentResolver resolver = new BashLikesResolver();
                resolver.insertEntry(getContext(), new BashOrgEntry().setId(entryId)
                        .setRating(newRating)
                        .setDirection(direction));*/

                cb.onSuccess();
            }
        }
        catch (IOException e) {
            Log.w(NAME, e);
            cb.onError(e.getMessage());
        } catch (VoteException e) {
            Log.w(NAME, e);
            cb.onError(e.getMessage());
        }
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
            url = BashService.Urls.getVoteUp(id);
            act = BashService.Urls.ACT_UP;
        } else if (direction == -1) {
            // DOWN
            url = BashService.Urls.getVoteDown(id);
            act = BashService.Urls.ACT_DOWN;
        } else if (direction == 0) {
            // BAYAN
            url = BashService.Urls.getVoteBayan(id);
            act = BashService.Urls.ACT_BAYAN;
        } else {
            throw new VoteException("Invalid direction o vote");
        }


        UrlParams data = new UrlParams().add("quote", id)
                .add("act", act);
        String resp = HttpDownloader.httpPost(new HttpDownloader.Request(url, data, null, Constants.ENCODING));
        return resp.equals("OK");
    }
}
