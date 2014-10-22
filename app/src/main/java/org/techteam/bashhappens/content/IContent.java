package org.techteam.bashhappens.content;

import org.techteam.bashhappens.content.bashorg.BashOrgList;

import java.io.IOException;

public interface IContent {
    BashOrgList retrieveNextList() throws IOException, FeedOverflowException;
}
