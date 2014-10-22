package org.techteam.bashhappens.logic;

import org.techteam.bashhappens.logic.bashorg.BashOrgList;

import java.io.IOException;

public interface Parsable {
    BashOrgList retrieveNextList(String locale) throws IOException, FeedOverflowException;
}
