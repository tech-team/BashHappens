package org.techteam.bashhappens.gui.services;

import android.content.Intent;

public final class BroadcasterIntentBuilder {


    public static Intent buildVoteSuccessIntent(String id, String rating) {
        Intent intent = new Intent(VoteServiceConstants.BROADCASTER_NAME);
        intent.putExtra(VoteServiceConstants.ID, id);
        intent.putExtra(VoteServiceConstants.NEW_RATING, rating);
        return intent;
    }

    public static Intent buildVoteErrorIntent(String id, String error) {
        Intent intent = new Intent(VoteServiceConstants.BROADCASTER_NAME);
        intent.putExtra(VoteServiceConstants.ID, id);
        intent.putExtra(VoteServiceConstants.NEW_RATING, (String) null);
        intent.putExtra(VoteServiceConstants.ERROR, error);
        return intent;
    }
}
