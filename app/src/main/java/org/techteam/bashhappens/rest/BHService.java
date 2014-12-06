package org.techteam.bashhappens.rest;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.rest.processors.GetPostsProcessor;
import org.techteam.bashhappens.rest.processors.BashVoteProcessor;

import org.techteam.bashhappens.rest.processors.Processor;
import org.techteam.bashhappens.rest.processors.ProcessorCallback;

public class BHService extends IntentService {
    public static final String TAG = BHService.class.getName();

    public BHService() {
        super(TAG);
    }

    public BHService(String name) {
        super(name);
    }

    public class IntentExtras {
        public static final String REQUEST_ID = "REQUEST_ID";
        public static final String OPERATION = "OPERATION";

        public class GetPostsOperation {
            public static final String TAG = "GET_POSTS";
            public static final String CONTENT_SOURCE = "CONTENT_SOURCE";
            public static final String LOAD_INTENTION = "LOAD_INTENTION";
        }

        public class BashVoteOperation {
            public static final String TAG = "BASH_VOTE";
            public static final String ENTRY_ID = "ENTRY_ID";
            public static final String RATING = "rating";
            public static final String DIRECTION = "direction";
            public static final String ENTRY_POSITION = "entryPosition";
            public static final String BAYAN = "bayan";
        }

        public class ItHappensVoteOperation {
            public static final String TAG = "IT_VOTE";
        }
    }

    public class CallbackIntentExtras {
        public static final String REQUEST_ID = "REQUEST_ID";
        public static final String OPERATION = "OPERATION";
        public static final String STATUS = "STATUS";
        public static final String ERROR_MSG = "ERROR_MSG";

        public class Status {
            public static final int OK = 0;
            public static final int ERROR = 1;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("=== Started BHService ===");
        Bundle extras = intent.getExtras();
        String requestId = extras.getString(IntentExtras.REQUEST_ID);
        String operation = extras.getString(IntentExtras.OPERATION);

        if (requestId == null || operation == null) {
            throw new RuntimeException("No REQUEST_ID or operation specified");
        }

        Processor processor = null;

        if (operation.equalsIgnoreCase(IntentExtras.GetPostsOperation.TAG)) {

            ContentSource contentSource = extras.getParcelable(IntentExtras.GetPostsOperation.CONTENT_SOURCE);
            int loadIntention = extras.getInt(IntentExtras.GetPostsOperation.LOAD_INTENTION);

            processor = new GetPostsProcessor(getBaseContext(), contentSource, loadIntention);

        } else if (operation.equalsIgnoreCase(IntentExtras.BashVoteOperation.TAG)) {

            int entryPosition = intent.getIntExtra(IntentExtras.BashVoteOperation.ENTRY_POSITION, -1);
            String entryId = extras.getString(IntentExtras.BashVoteOperation.ENTRY_ID);
            String rating = extras.getString(IntentExtras.BashVoteOperation.RATING);
            int direction = extras.getInt(IntentExtras.BashVoteOperation.DIRECTION, 0);
            boolean bayaning = extras.getBoolean(IntentExtras.BashVoteOperation.BAYAN, false);

            processor = new BashVoteProcessor(getBaseContext(), entryPosition, entryId, rating, direction, bayaning);
        } else if (operation.equalsIgnoreCase(IntentExtras.ItHappensVoteOperation.TAG)) {
            // TODO

            processor = null;
        }

        final Intent cbIntent = new Intent(ServiceHelper.ServiceBroadcastReceiverHelper.NAME);
        cbIntent.putExtra(CallbackIntentExtras.REQUEST_ID, requestId);
        cbIntent.putExtra(CallbackIntentExtras.OPERATION, operation);

        if (processor != null) {
            processor.start(new ProcessorCallback() {
                @Override
                public void onSuccess() {
                    cbIntent.putExtra(CallbackIntentExtras.STATUS, CallbackIntentExtras.Status.OK);
                    LocalBroadcastManager.getInstance(BHService.this).sendBroadcast(cbIntent);
                }

                @Override
                public void onError(String message) {
                    cbIntent.putExtra(CallbackIntentExtras.STATUS, CallbackIntentExtras.Status.ERROR);
                    cbIntent.putExtra(CallbackIntentExtras.ERROR_MSG, message);
                    LocalBroadcastManager.getInstance(BHService.this).sendBroadcast(cbIntent);
                }
            });

        } else {
            cbIntent.putExtra(CallbackIntentExtras.STATUS, CallbackIntentExtras.Status.ERROR);
            cbIntent.putExtra(CallbackIntentExtras.ERROR_MSG, "Processor not found");
            LocalBroadcastManager.getInstance(BHService.this).sendBroadcast(cbIntent);
        }
    }


}
