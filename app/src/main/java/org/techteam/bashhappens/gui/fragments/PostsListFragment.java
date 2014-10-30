package org.techteam.bashhappens.gui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.ContentFactory;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.gui.adapters.BashOrgListAdapter;
import org.techteam.bashhappens.gui.loaders.ContentAsyncLoader;
import org.techteam.bashhappens.gui.loaders.LoadIntention;
import org.techteam.bashhappens.gui.loaders.LoaderIds;
import org.techteam.bashhappens.gui.services.ServiceManager;
import org.techteam.bashhappens.gui.services.VoteServiceConstants;
import org.techteam.bashhappens.util.Toaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

public class PostsListFragment
        extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        OnBashEventCallback,
        OnListScrolledDownCallback {

    private static final class BundleKeys {
        public static final String FACTORY = "FACTORY";
    }

    private Queue<Runnable> delayedAdapterNotifications = new LinkedList<Runnable>();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;

    private ContentFactory factory = null;
    private ContentSource content = null;
    private BashOrgListAdapter adapter = new BashOrgListAdapter(PostsListFragment.this, PostsListFragment.this, null);

    private ServiceManager serviceManager = null;
    private VoteBroadcastReceiver voteBroadcastReceiver;

    private SparseArray<BashOrgListAdapter.VotedCallback> votedCallbackMap = new SparseArray<BashOrgListAdapter.VotedCallback>();
    private SparseArray<BashOrgListAdapter.VotedCallback> votedBayanCallbackMap = new SparseArray<BashOrgListAdapter.VotedCallback>();


    private LoaderManager.LoaderCallbacks<ContentList> contentListLoaderCallbacks = new LoaderManager.LoaderCallbacks<ContentList>() {

        @Override
        public Loader<ContentList> onCreateLoader(int i, Bundle bundle) {
            if  (i == LoaderIds.CONTENT_LOADER) {
                return new ContentAsyncLoader(getActivity(), content);
            }
            throw new IllegalArgumentException("Loader with given id is not found");
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onLoadFinished(Loader<ContentList> contentListLoader, ContentList contentList) {
            mSwipeRefreshLayout.setRefreshing(false);

            //TODO: obtain that value from Bundle
            int intention = LoadIntention.REFRESH;

            switch (contentList.getStoredContentType()) {
                case BASH_ORG:
                    final ArrayList<BashOrgEntry> entries = contentList.getEntries();
                    if (intention == LoadIntention.REFRESH) {
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
                    } else if (intention == LoadIntention.APPEND) {
                        final int oldCount = adapter.getItemCount();
                        adapter.addAll(entries);
                        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                            adapter.notifyItemRangeInserted(oldCount, entries.size());
                        } else {
                            delayedAdapterNotifications.add(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyItemRangeInserted(oldCount, entries.size());
                                }
                            });
                        }
                    }
                    break;
                case IT_HAPPENS:
                    // TODO: add ItHappens adapter
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<ContentList> contentListLoader) {
            // TODO
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        //TODO: write comment on this kostil'
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

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
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

        serviceManager = new ServiceManager(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            factory = new ContentFactory(Locale.getDefault().toString());
        } else {
            factory = savedInstanceState.getParcelable(BundleKeys.FACTORY);
        }
        content = factory.buildContent(ContentFactory.ContentSection.BASH_ORG_NEWEST);

        getLoaderManager().initLoader(LoaderIds.CONTENT_LOADER, null, contentListLoaderCallbacks);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BundleKeys.FACTORY, factory);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);

        Toaster.toast(getActivity().getBaseContext(), R.string.loading);
        // TODO: смотри TODO в onLoadFinished() ниже
        content = factory.buildContent(ContentFactory.ContentSection.BASH_ORG_NEWEST, true);
        getLoaderManager().restartLoader(LoaderIds.CONTENT_LOADER, null, contentListLoaderCallbacks);
    }

    @Override
    public void onScrolledDown() {
        Toaster.toast(getActivity().getBaseContext(), "onScrolledDown");
        //TODO: add flag into Bundle to mark intention: "refresh" or "append"
        //TODO: see LoadIntention
        getLoaderManager().initLoader(LoaderIds.CONTENT_LOADER, null, contentListLoaderCallbacks);
    }




    @Override
    public void onMakeVote(BashOrgEntry entry, int entryPosition, BashOrgEntry.VoteDirection direction, BashOrgListAdapter.VotedCallback votedCallback) {
        votedCallbackMap.put(entryPosition, votedCallback);
        serviceManager.startBashVoteService(entry, entryPosition, direction);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerBroadcastReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterBroadcastReceivers();
    }



    private void registerBroadcastReceivers() {
        IntentFilter voteIntentFilter = new IntentFilter(VoteServiceConstants.BROADCASTER_NAME);
        voteBroadcastReceiver = new VoteBroadcastReceiver();
        LocalBroadcastManager.getInstance(PostsListFragment.this.getActivity())
                .registerReceiver(voteBroadcastReceiver, voteIntentFilter);
    }

    private void unregisterBroadcastReceivers() {
        LocalBroadcastManager.getInstance(PostsListFragment.this.getActivity())
                .unregisterReceiver(voteBroadcastReceiver);
    }










    public final class VoteBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra(VoteServiceConstants.ID);
            String newRating = intent.getStringExtra(VoteServiceConstants.NEW_RATING);
            boolean bayanOk = intent.getBooleanExtra(VoteServiceConstants.BAYAN_OK, false);
            int position = intent.getIntExtra(VoteServiceConstants.ENTRY_POSITION, -1);
            String error = intent.getStringExtra(VoteServiceConstants.ERROR);

            if (error == null) {

                BashOrgEntry entry = adapter.get(position);
                BashOrgListAdapter.VotedCallback cb = votedCallbackMap.get(position);

                if (newRating != null) {
                    entry.setRating(newRating);
                    cb.onVoted(entry);
                    Toaster.toast(context.getApplicationContext(), "Changed rating for entry #" + id);
                } else if (bayanOk) {
                    entry.setBayan(true);
                    cb.onBayan(entry);
                    Toaster.toast(context.getApplicationContext(), "Set bayan for entry #" + id);
                }
            }
            else {
                Toaster.toast(context.getApplicationContext(), "Error for #" + id + ". " + error);
            }
        }
    }
}
