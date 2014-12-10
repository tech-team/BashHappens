package org.techteam.bashhappens.rest;

import org.techteam.bashhappens.content.ContentSource;

public interface GetPostsCallback extends ServiceCallback {
    public static class Extras {
        public static final String NEW_CONTENT_SOURCE = "NEW_CONTENT_SOURCE";
    }
}
