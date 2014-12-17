package org.techteam.bashhappens.rest.service_helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.rest.CallbacksKeeper;
import org.techteam.bashhappens.rest.OperationType;
import org.techteam.bashhappens.rest.PendingOperation;
import org.techteam.bashhappens.rest.service.BHService;
import org.techteam.bashhappens.rest.service.ServiceIntentBuilder;
import org.techteam.bashhappens.util.CallbackHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceHelper {
    private final Context context;
    private CallbackHelper<String, ServiceCallback> callbackHelper = new CallbackHelper<String, ServiceCallback>();
    private Map<String, PendingOperation> pendingOperations = new HashMap<>();
    private boolean isInit = false;
    private ServiceBroadcastReceiver receiver;

    public ServiceHelper(Context context) {
        this.context = context;
    }

    public void getPosts(ContentSource contentSource, int loadIntention, ServiceCallback cb) {
        init();

        String requestId = OperationType.GET_POSTS + "_" + contentSource.getSection().toString() + "_" + contentSource.getFootprint();
        CallbackHelper.AddStatus s = callbackHelper.addCallback(requestId, cb);

        if (s == CallbackHelper.AddStatus.NEW_CB) {
            Intent intent = ServiceIntentBuilder.getPostsIntent(context, requestId, contentSource, loadIntention);
            context.startService(intent);
        }

        pendingOperations.put(requestId, new PendingOperation(OperationType.GET_POSTS, requestId));
    }

    public void bashVote(BashOrgEntry entry, int entryPosition, BashOrgEntry.VoteDirection voteDirection, ServiceCallback cb) {
        init();

        String requestId = OperationType.BASH_VOTE + "_"
                            + entry.getId() + "_"
                            + entryPosition + "_"
                            + voteDirection.toString();
        CallbackHelper.AddStatus s = callbackHelper.addCallback(requestId, cb);
        if (s == CallbackHelper.AddStatus.NEW_CB) {
            Intent intent = ServiceIntentBuilder.voteBashIntent(context, requestId, entryPosition, entry.getId(), entry.getRating(), voteDirection.toDirection());
            context.startService(intent);
        }

        pendingOperations.put(requestId, new PendingOperation(OperationType.BASH_VOTE, requestId));
    }

    public void saveOperationsState(Bundle outState, String key) {
        outState.putParcelableArrayList(key, new ArrayList<>(pendingOperations.values()));
    }

    /**
     * @return true if app should be refreshing right now, false otherwise
     **/
    public boolean restoreOperationsState(Bundle savedInstanceState, String key, CallbacksKeeper callbacksKeeper) {
        ArrayList<PendingOperation> operations = savedInstanceState.getParcelableArrayList(key);
        for (PendingOperation op : operations) {
            pendingOperations.put(op.getOperationId(), op);
        }

        boolean isRefreshing = false;

        // callbacks are subscribed again to restored pending operations
        for (String opId : pendingOperations.keySet()) {
            PendingOperation op = pendingOperations.get(opId);
            if (op.getOperationType() == OperationType.GET_POSTS)
                isRefreshing = true;

            addCallback(op.getOperationId(), callbacksKeeper.getCallback(op.getOperationType()));
        }
        return isRefreshing;
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

            int status = extras.getInt(BHService.CallbackIntentExtras.STATUS);
            String errorMsg = extras.getString(BHService.CallbackIntentExtras.ERROR_MSG);
            Bundle data = extras.getBundle(BHService.CallbackIntentExtras.EXTRA_DATA);

            List<ServiceCallback> callbacks = callbackHelper.getCallbacks(id);
            if (callbacks != null) {
                for (ServiceCallback cb : callbacks) {
                    if (status == BHService.CallbackIntentExtras.Status.OK) {
                        cb.onSuccess(id, data);
                    } else {
                        cb.onError(id, data, errorMsg);
                    }
                    pendingOperations.remove(id);
                }
            }

            callbackHelper.removeCallbacks(id);
        }
    }

    public static class ServiceBroadcastReceiverHelper {
        public static final String NAME = ServiceBroadcastReceiver.class.getName();
    }
}
