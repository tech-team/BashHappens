package org.techteam.bashhappens.gui.services;

import android.content.Context;
import android.content.Intent;

import org.techteam.bashhappens.content.bashorg.BashOrgEntry;

public final class ServiceManager {
    private final Context context;
    private Intent voteIntent;

    public ServiceManager(final Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void startBashVoteService(BashOrgEntry entry, int entryPosition, BashOrgEntry.VoteDirection voteDirection) {
        if (voteIntent != null) {
            context.stopService(voteIntent);
        }
        voteIntent = ServiceIntentBuilder.voteBashIntent(context, entryPosition, entry.getId(), entry.getRating(), voteDirection.toDirection());
        context.startService(voteIntent);
    }
}
