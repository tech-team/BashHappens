package org.techteam.bashhappens.gui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.ContentFactory;
import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.gui.adapters.BashOrgListAdapter;
import org.techteam.bashhappens.gui.loaders.ContentLoader;
import org.techteam.bashhappens.gui.loaders.LoadIntention;
import org.techteam.bashhappens.gui.loaders.LoaderIds;
import org.techteam.bashhappens.rest.CallbacksMaintainer;
import org.techteam.bashhappens.rest.OperationType;
import org.techteam.bashhappens.rest.PendingOperation;
import org.techteam.bashhappens.util.Toaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import org.techteam.bashhappens.rest.service_helper.ServiceCallback;
import org.techteam.bashhappens.rest.service_helper.ServiceHelper;

public class PostsListFragment
        extends Fragment
        implements
        SwipeRefreshLayout.OnRefreshListener,
        OnBashEventCallback,
        OnListScrolledDownCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = PostsListFragment.class.toString();

    private static final class BundleKeys {
        public static final String FACTORY = "FACTORY";
        public static final String PENDING_OPERATIONS = "PENDING_OPERATIONS";
    }

    //see comment in onCreateView()
    private Queue<Runnable> delayedAdapterNotifications = new LinkedList<Runnable>();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;

    private ContentFactory factory = null;
    private ContentSource content = null;
    private BashOrgListAdapter adapter = new BashOrgListAdapter(null, PostsListFragment.this);

    private CallbacksMaintainer callbacksMaintainer = new CallbacksMaintainer();
    private ServiceHelper serviceHelper;
    private Map<String, PendingOperation> pendingOperations = new HashMap<>();
//    private ServiceManager serviceManager = null;
//    private VoteBroadcastReceiver voteBroadcastReceiver;

//    @Deprecated private SparseArray<BashOrgListAdapter.VotedCallback> votedCallbackMap = new SparseArray<BashOrgListAdapter.VotedCallback>();

    private LoaderManager.LoaderCallbacks<Cursor> contentDataLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if  (id == LoaderIds.CONTENT_LOADER) {
                return new ContentLoader(getActivity(), content.getSection());
            }
            throw new IllegalArgumentException("Loader with given id is not found");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
            adapter.swapCursor(newCursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            adapter.swapCursor(null);
        }
    };

    @Deprecated
    private void setPosts(ArrayList<BashOrgEntry> entries) {
        adapter.setAll(entries);
        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            adapter.notifyDataSetChanged();
        } else {
            delayedAdapterNotifications.add(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Deprecated
    private void addPosts(ArrayList<BashOrgEntry> entries) {
        final int oldCount = adapter.getItemCount() - 1; //minus "Loading..." item
        final int addedCount = entries.size();

        final String str = Integer.toString(addedCount) + " posts added";

        adapter.addAll(entries);
        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            adapter.notifyItemRangeInserted(oldCount, addedCount);
            Toaster.toast(getActivity(), str + " (right away)");
        } else {
            delayedAdapterNotifications.add(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemRangeInserted(oldCount, addedCount);
                    Toaster.toast(getActivity(), str + " (after scroll)");
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        //this thing waits for user to stop scrolling and adds new data or refreshes existing data
        //because it's impossible to notify*() adapter when scrolling
        // (getting IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling)
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    for (Runnable r: delayedAdapterNotifications)
                        r.run();

                    delayedAdapterNotifications.clear();
                }
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        serviceHelper = new ServiceHelper(activity);
        callbacksMaintainer.addCallback(OperationType.GET_POSTS, new ServiceCallback() {
            @Override
            public void onSuccess(String operationId, Bundle data) {
                mSwipeRefreshLayout.setRefreshing(false);
                content = data.getParcelable(GetPostsExtras.NEW_CONTENT_SOURCE);
                String msg = "ServiceCallback call #1";
                Toaster.toast(getActivity().getApplicationContext(), msg);
                System.out.println(msg);

                pendingOperations.remove(operationId);
                getLoaderManager().restartLoader(LoaderIds.CONTENT_LOADER, null, contentDataLoaderCallbacks);
            }

            @Override
            public void onError(String operationId, Bundle data, String message) {
                mSwipeRefreshLayout.setRefreshing(false);
                String msg = "ServiceCallback call #1. ERROR";
                Toaster.toast(getActivity().getApplicationContext(), msg);
                System.out.println(msg);

                pendingOperations.remove(operationId);
            }
        });

        callbacksMaintainer.addCallback(OperationType.BASH_VOTE, new ServiceCallback() {
            @Override
            public void onSuccess(String operationId, Bundle data) {
                String msg = "voted for entry: "; // + entry.getId();
                Toaster.toast(getActivity().getApplicationContext(), msg);
                System.out.println(msg);
            }

            @Override
            public void onError(String operationId, Bundle data, String message) {
                String msg = "vote failed for entry: "; // + entry.getId();
                Toaster.toast(getActivity().getApplicationContext(), msg);
                System.out.println(msg);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        if (savedInstanceState == null) {
            factory = new ContentFactory(Locale.getDefault().toString());
        } else {
            factory = savedInstanceState.getParcelable(BundleKeys.FACTORY);
            ArrayList<PendingOperation> operations = savedInstanceState.getParcelableArrayList(BundleKeys.PENDING_OPERATIONS);
            for (PendingOperation op : operations) {
                pendingOperations.put(op.getOperationId(), op);
            }
        }

        // callbacks are subscribed again to restored pending operations
        for (String opId : pendingOperations.keySet()) {
            PendingOperation op = pendingOperations.get(opId);
            serviceHelper.addCallback(op.getOperationId(), callbacksMaintainer.getCallback(op.getOperationType()));
        }

        //TODO: should be saved in prefs i think
        content = factory.buildContent(ContentSection.BASH_ORG_NEWEST);
        getLoaderManager().initLoader(LoaderIds.CONTENT_LOADER, null, contentDataLoaderCallbacks);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BundleKeys.FACTORY, factory);
        outState.putParcelableArrayList(BundleKeys.PENDING_OPERATIONS, new ArrayList<>(pendingOperations.values()));
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);

        Toaster.toast(getActivity().getBaseContext(), R.string.loading);
        content = factory.buildContent(ContentSection.BASH_ORG_NEWEST, true);

        String opId = serviceHelper.getPosts(content, LoadIntention.REFRESH, callbacksMaintainer.getCallback(OperationType.GET_POSTS));
        pendingOperations.put(opId, new PendingOperation(OperationType.GET_POSTS, opId));
    }

    @Override
    public void onScrolledDown() {
        Toaster.toast(getActivity().getBaseContext(), "Bottom reached");

        System.out.println("Load has begun");
        String opId = serviceHelper.getPosts(content, LoadIntention.APPEND, callbacksMaintainer.getCallback(OperationType.GET_POSTS));
        pendingOperations.put(opId, new PendingOperation(OperationType.GET_POSTS, opId));
    }




    @Override
    public void onMakeVote(final BashOrgEntry entry, int entryPosition, BashOrgEntry.VoteDirection direction, BashOrgListAdapter.VotedCallback votedCallback) {
        String opId = serviceHelper.bashVote(entry, entryPosition, direction, callbacksMaintainer.getCallback(OperationType.BASH_VOTE));
        pendingOperations.put(opId, new PendingOperation(OperationType.BASH_VOTE, opId));
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume");
        serviceHelper.init();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause");
        serviceHelper.release();
    }



    private void registerBroadcastReceivers() {
    }

    private void unregisterBroadcastReceivers() {
    }





    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (isAdded()) {
            Resources res = getResources();

            int resId = res.getIdentifier(key, "string", getActivity().getPackageName());

            //TODO: move defaults somewhere
            switch (resId) {
                case R.string.pref_justify_by_width_key:
                    Toaster.toast(getActivity().getBaseContext(),
                            "You have changed "+ key + " to " + sharedPreferences.getBoolean(key, false));

                    break;
                case R.string.pref_night_mode_key:
                    Toaster.toast(getActivity().getBaseContext(),
                            "You have changed "+ key + " to " + sharedPreferences.getBoolean(key, false));

                    break;

                case R.string.pref_shorten_long_posts_key:
                    Toaster.toast(getActivity().getBaseContext(),
                            "You have changed "+ key + " to " + sharedPreferences.getBoolean(key, false));

                    break;

                case R.string.pref_text_size_key:
                    Toaster.toast(getActivity().getBaseContext(),
                            "You have changed "+ key + " to " + sharedPreferences.getString(key, "small"));

                    break;

                default:
                    Toaster.toast(getActivity().getBaseContext(),
                            getActivity().getString(R.string.preferences_error));
                    break;
            }
        }
    }
}
