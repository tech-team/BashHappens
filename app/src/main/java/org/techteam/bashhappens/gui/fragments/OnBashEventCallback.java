package org.techteam.bashhappens.gui.fragments;

import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.gui.adapters.BashOrgListAdapter;

public interface OnBashEventCallback {
    void onMakeVote(BashOrgEntry entry, int entryPosition, BashOrgEntry.VoteDirection direction, BashOrgListAdapter.VotedCallback votedCallback);
    // TODO: fav
}