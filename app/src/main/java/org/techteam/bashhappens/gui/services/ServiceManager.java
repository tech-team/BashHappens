package org.techteam.bashhappens.gui.services;

import android.content.Context;
import android.content.Intent;

import org.techteam.bashhappens.content.bashorg.BashOrgEntry;

public final class ServiceManager {
    private final Context context;

    public ServiceManager(final Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void startBashVoteService(BashOrgEntry entry, int entryPosition, BashOrgEntry.VoteDirection voteDirection) {
        Intent voteIntent;
        if (voteDirection == BashOrgEntry.VoteDirection.BAYAN) {
            voteIntent = ServiceIntentBuilder.voteBayanBashIntent(context, entryPosition, entry.getId());
        } else {
            voteIntent = ServiceIntentBuilder.voteBashIntent(context, entryPosition, entry.getId(), entry.getRating(), voteDirection.toDirection());
        }
        context.startService(voteIntent);
    }
}
