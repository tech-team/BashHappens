package org.techteam.bashhappens.gui.services;

import android.content.Intent;

public final class BroadcasterIntentBuilder {


    public static Intent buildVoteSuccessIntent(int entryPosition, String id, String rating) {
        Intent intent = new Intent(VoteServiceConstants.BROADCASTER_NAME);
        intent.putExtra(VoteServiceConstants.ENTRY_POSITION, entryPosition);
        intent.putExtra(VoteServiceConstants.ID, id);
        intent.putExtra(VoteServiceConstants.NEW_RATING, rating);
        return intent;
    }

    public static Intent buildVoteErrorIntent(int entryPosition, String id, String error) {
        Intent intent = new Intent(VoteServiceConstants.BROADCASTER_NAME);
        intent.putExtra(VoteServiceConstants.ENTRY_POSITION, entryPosition);
        intent.putExtra(VoteServiceConstants.ID, id);
        intent.putExtra(VoteServiceConstants.NEW_RATING, (String) null);
        intent.putExtra(VoteServiceConstants.ERROR, error);
        return intent;
    }

    public static Intent buildBayanSuccessIntent(int entryPosition, String id) {
        Intent intent = new Intent(VoteServiceConstants.BROADCASTER_NAME);
        intent.putExtra(VoteServiceConstants.ENTRY_POSITION, entryPosition);
        intent.putExtra(VoteServiceConstants.ID, id);
        intent.putExtra(VoteServiceConstants.BAYAN_OK, true);
        return intent;
    }
}
