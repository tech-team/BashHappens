package org.techteam.bashhappens.rest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.ContentType;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;

import java.util.List;

public class ServiceHelper {
    private final Context context;
    private CallbackHelper<String, ServiceCallback> callbackHelper = new CallbackHelper<String, ServiceCallback>();
    private boolean isInit = false;
    private ServiceBroadcastReceiver receiver;

    public ServiceHelper(Context context) {
        this.context = context;
    }

    public String getPosts(ContentSource contentSource, int loadIntention, ServiceCallback cb) {
        if (!isInit) {
            throw new ServiceHelperNotInitializedException();
        }

        String requestId = OperationType.GET_POSTS + "_" + contentSource.getFootprint();
        CallbackHelper.AddStatus s = callbackHelper.addCallback(requestId, cb);

        if (s == CallbackHelper.AddStatus.NEW_CB) {
            Intent intent = ServiceIntentBuilder.getPostsIntent(context, requestId, contentSource, loadIntention);
            context.startService(intent);
        }
        return requestId;
    }

    public String bashVote(BashOrgEntry entry, int entryPosition, BashOrgEntry.VoteDirection voteDirection, ServiceCallback cb) {
        if (!isInit) {
            throw new ServiceHelperNotInitializedException();
        }

        String requestId = OperationType.BASH_VOTE + "_"
                            + entry.getId() + "_"
                            + entryPosition + "_"
                            + voteDirection.toString();
        CallbackHelper.AddStatus s = callbackHelper.addCallback(requestId, cb);
        if (s == CallbackHelper.AddStatus.NEW_CB) {
            Intent intent;

            if (voteDirection == BashOrgEntry.VoteDirection.BAYAN) {
                intent = ServiceIntentBuilder.voteBayanBashIntent(context, requestId, entryPosition, entry.getId());
            } else {
                intent = ServiceIntentBuilder.voteBashIntent(context, requestId, entryPosition, entry.getId(), entry.getRating(), voteDirection.toDirection());
            }

            context.startService(intent);
        }
        return requestId;
    }

    public void init() {
        if (!isInit) {
            IntentFilter filter = new IntentFilter(ServiceBroadcastReceiverHelper.NAME);
            receiver = new ServiceBroadcastReceiver();
            LocalBroadcastManager.getInstance(context)
                    .registerReceiver(receiver, filter);
            isInit = true;
        }
    }

    public void release() {
        if (isInit) {
            LocalBroadcastManager.getInstance(context)
                    .unregisterReceiver(receiver);
            isInit = false;
        }
    }

    public void addCallback(String operationId, ServiceCallback cb) {
        callbackHelper.addCallback(operationId, cb);
    }



    public class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String id = extras.getString(BHService.CallbackIntentExtras.REQUEST_ID);
            OperationType operation = Enum.valueOf(OperationType.class, extras.getString(BHService.CallbackIntentExtras.OPERATION));

            // TODO: extract all the statuses and push to the callbacks
            int status = extras.getInt(BHService.CallbackIntentExtras.STATUS);
            String errorMsg = extras.getString(BHService.CallbackIntentExtras.ERROR_MSG);

            List<ServiceCallback> callbacks = callbackHelper.getCallbacks(id);
            if (callbacks != null) {
                for (ServiceCallback cb : callbacks) {
                    if (status == BHService.CallbackIntentExtras.Status.OK) {
                        Bundle data = extras.getBundle(BHService.CallbackIntentExtras.EXTRA_DATA);
                        cb.onSuccess(id, data); // TODO: provide some params
                    } else {
                        Bundle data = new Bundle();
                        cb.onError(id, data, errorMsg);
                    }
                }
            }

            callbackHelper.removeCallbacks(id);
        }
    }

    public static class ServiceBroadcastReceiverHelper {
        public static final String NAME = ServiceBroadcastReceiver.class.getName();
    }
}
