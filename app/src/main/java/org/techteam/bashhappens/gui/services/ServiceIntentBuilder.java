package org.techteam.bashhappens.gui.services;

import android.content.Context;
import android.content.Intent;

public final class ServiceIntentBuilder {


    public static Intent voteBashIntent(Context context, int entryPosition, String id, String rating, int direction) {
        Intent intent = new Intent(context, BashVoteService.class);
        intent.putExtra(BashVoteService.IntentKeys.ENTRY_POSITION, entryPosition);
        intent.putExtra(BashVoteService.IntentKeys.ID, id);
        intent.putExtra(BashVoteService.IntentKeys.RATING, rating);
        intent.putExtra(BashVoteService.IntentKeys.DIRECTION, direction);
        return intent;
    }

}
