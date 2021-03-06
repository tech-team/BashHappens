package org.techteam.bashhappens.rest.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.rest.OperationType;
import org.techteam.bashhappens.rest.service_helper.ServiceHelper;
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
            public static final String CONTENT_SOURCE = "CONTENT_SOURCE";
            public static final String LOAD_INTENTION = "LOAD_INTENTION";
        }

        public class BashVoteOperation {
            public static final String ENTRY_ID = "ENTRY_ID";
            public static final String RATING = "rating";
            public static final String DIRECTION = "direction";
            public static final String ENTRY_POSITION = "entryPosition";
            public static final String BAYAN = "bayan";
        }

        public class ItHappensVoteOperation {
        }
    }

    public class CallbackIntentExtras {
        public static final String REQUEST_ID = "REQUEST_ID";
        public static final String OPERATION = "OPERATION";
        public static final String STATUS = "STATUS";
        public static final String ERROR_MSG = "ERROR_MSG";
        public static final String EXTRA_DATA = "EXTRA_DATA";

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
        OperationType operation = null;
        try {
            operation = Enum.valueOf(OperationType.class, extras.getString(IntentExtras.OPERATION));
        } catch (IllegalArgumentException ignored) {
        }

        if (requestId == null || operation == null) {
            throw new RuntimeException("No REQUEST_ID or operation specified");
        }

        Processor processor = null;


        if (operation == OperationType.GET_POSTS) {

            ContentSource contentSource = extras.getParcelable(IntentExtras.GetPostsOperation.CONTENT_SOURCE);
            int loadIntention = extras.getInt(IntentExtras.GetPostsOperation.LOAD_INTENTION);

            processor = new GetPostsProcessor(getBaseContext(), contentSource, loadIntention);

        } else if (operation == OperationType.BASH_VOTE) {

            int entryPosition = intent.getIntExtra(IntentExtras.BashVoteOperation.ENTRY_POSITION, -1);
            String entryId = extras.getString(IntentExtras.BashVoteOperation.ENTRY_ID);
            String rating = extras.getString(IntentExtras.BashVoteOperation.RATING);
            int direction = extras.getInt(IntentExtras.BashVoteOperation.DIRECTION, 100); // 100 is a value to break
//            boolean bayaning = extras.getBoolean(IntentExtras.BashVoteOperation.BAYAN, false);

            processor = new BashVoteProcessor(getBaseContext(), entryPosition, entryId, rating, direction);
        } else if (operation == OperationType.IT_VOTE) {
            // TODO

            processor = null;
        }

        final Intent cbIntent = new Intent(ServiceHelper.ServiceBroadcastReceiverHelper.NAME);
        cbIntent.putExtra(CallbackIntentExtras.REQUEST_ID, requestId);
        cbIntent.putExtra(CallbackIntentExtras.OPERATION, operation.toString());

        if (processor != null) {
            processor.start(operation, requestId, new ProcessorCallback() {
                @Override
                public void onSuccess(Bundle data) {
                    cbIntent.putExtra(CallbackIntentExtras.STATUS, CallbackIntentExtras.Status.OK);
                    cbIntent.putExtra(CallbackIntentExtras.EXTRA_DATA, data);
                    LocalBroadcastManager.getInstance(BHService.this).sendBroadcast(cbIntent);
                }

                @Override
                public void onError(String message, Bundle data) {
                    cbIntent.putExtra(CallbackIntentExtras.STATUS, CallbackIntentExtras.Status.ERROR);
                    cbIntent.putExtra(CallbackIntentExtras.ERROR_MSG, message);
                    cbIntent.putExtra(CallbackIntentExtras.EXTRA_DATA, data);
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
