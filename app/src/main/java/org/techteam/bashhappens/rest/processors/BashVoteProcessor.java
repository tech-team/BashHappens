package org.techteam.bashhappens.rest.processors;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.techteam.bashhappens.content.Constants;
import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgUrls;
import org.techteam.bashhappens.content.exceptions.VoteException;
import org.techteam.bashhappens.content.resolvers.AbstractContentResolver;
import org.techteam.bashhappens.content.resolvers.BashBayanResolver;
import org.techteam.bashhappens.content.resolvers.BashLikesResolver;
import org.techteam.bashhappens.net.HttpDownloader;
import org.techteam.bashhappens.net.UrlParams;
import org.techteam.bashhappens.rest.OperationType;
import org.techteam.bashhappens.rest.service_helper.ServiceCallback;

import java.io.IOException;

public class BashVoteProcessor extends Processor {
    private static final String NAME = BashVoteProcessor.class.getName();

    private final int entryPosition;
    private final String entryId;
    private final String rating;
    private final int direction;

    public BashVoteProcessor(Context context, int entryPosition, String entryId, String rating, int direction) {
        super(context);
        this.entryPosition = entryPosition;
        this.entryId = entryId;
        this.rating = rating;
        this.direction = direction;
    }

    @Override
    public void start(OperationType operationType, String requestId, ProcessorCallback cb) {
        transactionStarted(operationType, requestId);

        Bundle data = new Bundle();
        data.putString(ServiceCallback.BashVoteExtras.ENTRY_ID, entryId);
        data.putInt(ServiceCallback.BashVoteExtras.ENTRY_POSITION, entryPosition);
        try {
            if (direction == 0) {
                // Bayan

                makeBayan(entryId);

                BashBayanResolver resolver = new BashBayanResolver();
                resolver.insert(getContext(), new BashOrgEntry().setId(entryId)
                                                                .setBayan(true));
            } else {
                // Normal rating

                String newRating = changeRating(entryId, rating, direction);

                BashLikesResolver resolver = new BashLikesResolver();
                resolver.insert(getContext(), new BashOrgEntry().setId(entryId)
                                                                .setRating(newRating)
                                                                .setDirection(direction));
            }
            transactionFinished(operationType, requestId);

            cb.onSuccess(data);
        }
        catch (IOException | VoteException e) {
            Log.w(NAME, e);
            transactionError(operationType, requestId);
            cb.onError(e.getMessage(), data);
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
