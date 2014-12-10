package org.techteam.bashhappens.rest.processors;

import android.content.Context;
import android.util.Log;

import org.techteam.bashhappens.content.Constants;
import org.techteam.bashhappens.content.bashorg.BashOrgUrls;
import org.techteam.bashhappens.content.exceptions.VoteException;
import org.techteam.bashhappens.net.HttpDownloader;
import org.techteam.bashhappens.net.UrlParams;
import org.techteam.bashhappens.rest.OperationType;

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
    public void start(OperationType operationType, String requestId, ProcessorCallback cb) {
        transactionStarted(operationType, requestId);
        try {
            if (bayaning) {
                // Bayan

                makeBayan(entryId);

                //TODO: decide about bayan/likes system and implement
                /*AbstractContentResolver resolver = new BashBayanResolver();
                resolver.insertEntry(getContext(), new BashOrgEntry().setId(entryId)
                                                                     .setBayan(true));*/
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
            }
            transactionFinished(operationType, requestId);
            cb.onSuccess(null);
        }
        catch (IOException | VoteException e) {
            Log.w(NAME, e);
            transactionError(operationType, requestId);
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
            url = BashOrgUrls.getVoteUp(id);
            act = BashOrgUrls.ACT_UP;
        } else if (direction == -1) {
            // DOWN
            url = BashOrgUrls.getVoteDown(id);
            act = BashOrgUrls.ACT_DOWN;
        } else if (direction == 0) {
            // BAYAN
            url = BashOrgUrls.getVoteBayan(id);
            act = BashOrgUrls.ACT_BAYAN;
        } else {
            throw new VoteException("Invalid direction o vote");
        }


        UrlParams data = new UrlParams().add("quote", id)
                .add("act", act);
        String resp = HttpDownloader.httpPost(new HttpDownloader.Request(url, data, null, Constants.ENCODING));
        return resp.equals("OK");
    }
}
