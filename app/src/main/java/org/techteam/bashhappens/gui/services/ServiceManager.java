package org.techteam.bashhappens.gui.services;

import android.content.Context;
import android.content.Intent;

import org.techteam.bashhappens.content.bashorg.BashOrgEntry;

public final class ServiceManager {
    private final Context context;
    private Intent translationIntent;

    public ServiceManager(final Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void startBashVoteService(BashOrgEntry entry, BashOrgEntry.VoteDirection voteDirection) {
        if (translationIntent != null) {
            context.stopService(translationIntent);
        }
        translationIntent = ServiceIntentBuilder.voteBashIntent(context, entry.getId(), entry.getRating(), voteDirection.toDirection());
        context.startService(translationIntent);
    }
}
