package org.techteam.bashhappens.content;

import java.io.IOException;

public interface ContentSource {
    ContentList<?> retrieveNextList() throws IOException, FeedOverflowException;
}
