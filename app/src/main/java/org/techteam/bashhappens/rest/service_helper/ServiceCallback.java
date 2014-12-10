package org.techteam.bashhappens.rest.service_helper;

import android.os.Bundle;

public interface ServiceCallback {
    void onSuccess(String operationId, Bundle data);
    void onError(String operationId, Bundle data, String message);

    class GetPostsExtras {
        public static final String NEW_CONTENT_SOURCE = "NEW_CONTENT_SOURCE";
    }

    class BashVoteExtras {
        public static final String ENTRY_ID = "ENTRY_ID";
    }
}
