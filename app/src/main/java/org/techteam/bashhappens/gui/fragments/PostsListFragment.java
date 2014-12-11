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
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.gui.activities.MainActivity;
import org.techteam.bashhappens.gui.adapters.BashOrgListAdapter;
import org.techteam.bashhappens.gui.loaders.ContentLoader;
import org.techteam.bashhappens.gui.loaders.LoadIntention;
import org.techteam.bashhappens.gui.loaders.LoaderIds;
import org.techteam.bashhappens.rest.CallbacksKeeper;
import org.techteam.bashhappens.rest.OperationType;
import org.techteam.bashhappens.rest.service_helper.ServiceCallback;
import org.techteam.bashhappens.rest.service_helper.ServiceHelper;
import org.techteam.bashhappens.util.Toaster;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

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
    private BashOrgListAdapter adapter;

    private CallbacksKeeper callbacksKeeper = new CallbacksKeeper();
    private ServiceHelper serviceHelper;

    private MainActivity activity;

    private LoaderManager.LoaderCallbacks<Cursor> contentDataLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if  (id == LoaderIds.CONTENT_LOADER) {
                Integer entryPos = null;
                if (args != null) {
                    entryPos = args.getInt(ContentLoader.BundleKeys.ENTRY_POSITION, -1);
                    entryPos = entryPos == -1 ? null : entryPos;
                }

                return new ContentLoader(getActivity(), content.getSection(), entryPos);
            }
            throw new IllegalArgumentException("Loader with given id is not found");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
            Integer entryPos = ((ContentLoader) loader).getEntryPosition();
            adapter.swapCursor(newCursor, entryPos);
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

        adapter = new BashOrgListAdapter(null, getActivity(), PostsListFragment.this, PostsListFragment.this);
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
        
        this.activity = (MainActivity) activity;
        serviceHelper = new ServiceHelper(activity);
        callbacksKeeper.addCallback(OperationType.GET_POSTS, new ServiceCallback() {
            @Override
            public void onSuccess(String operationId, Bundle data) {
                mSwipeRefreshLayout.setRefreshing(false);
                content = data.getParcelable(GetPostsExtras.NEW_CONTENT_SOURCE);
                boolean isFeedFinished = data.getBoolean(GetPostsExtras.FEED_FINISHED, false);

                String msg;
                if (isFeedFinished) {
                    msg = getActivity().getString(R.string.feed_finished);
                } else {
                    msg = "Successfully fetched posts";
                    getLoaderManager().restartLoader(LoaderIds.CONTENT_LOADER, null, contentDataLoaderCallbacks);
                }

//                Toaster.toast(getActivity().getApplicationContext(), msg);
                System.out.println(msg);
            }

            @Override
            public void onError(String operationId, Bundle data, String message) {
                mSwipeRefreshLayout.setRefreshing(false);
                String msg = "Error. " + message;
                Toaster.toast(getActivity().getApplicationContext(), msg);
                System.out.println(msg);
            }
        });

        callbacksKeeper.addCallback(OperationType.BASH_VOTE, new ServiceCallback() {
            @Override
            public void onSuccess(String operationId, Bundle data) {
                String entryId = data.getString(ServiceCallback.BashVoteExtras.ENTRY_ID);
                int entryPosition = data.getInt(BashVoteExtras.ENTRY_POSITION);

                String msg = "Voted for entry: " + entryId;
//                Toaster.toast(getActivity().getApplicationContext(), msg);
                System.out.println(msg);

                Bundle args = new Bundle();
                args.putInt(ContentLoader.BundleKeys.ENTRY_POSITION, entryPosition);
                getLoaderManager().restartLoader(LoaderIds.CONTENT_LOADER, args, contentDataLoaderCallbacks);
            }

            @Override
            public void onError(String operationId, Bundle data, String message) {
                String entryId = data.getString(ServiceCallback.BashVoteExtras.ENTRY_ID);

                String msg = "Vote failed for entry: " + entryId + ". " + message;
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
            boolean isRefreshing = serviceHelper.restoreOperationsState(savedInstanceState, BundleKeys.PENDING_OPERATIONS, callbacksKeeper);
            if (isRefreshing) {
                // TODO: it is not working
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

        content = factory.buildContent(activity.getSection().getContentSection());
        getLoaderManager().initLoader(LoaderIds.CONTENT_LOADER, null, contentDataLoaderCallbacks);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BundleKeys.FACTORY, factory);
        serviceHelper.saveOperationsState(outState, BundleKeys.PENDING_OPERATIONS);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);

//        Toaster.toast(getActivity().getBaseContext(), R.string.loading);
        content = factory.buildContent(activity.getSection().getContentSection(), true);

        serviceHelper.getPosts(content, LoadIntention.REFRESH, callbacksKeeper.getCallback(OperationType.GET_POSTS));
    }

    @Override
    public void onScrolledDown() {
//        Toaster.toast(getActivity().getBaseContext(), "Bottom reached");

        System.out.println("Load has begun");
        serviceHelper.getPosts(content, LoadIntention.APPEND, callbacksKeeper.getCallback(OperationType.GET_POSTS));
    }


    @Override
    public void onMakeVote(final BashOrgEntry entry, int entryPosition, BashOrgEntry.VoteDirection direction) {
        serviceHelper.bashVote(entry, entryPosition, direction, callbacksKeeper.getCallback(OperationType.BASH_VOTE));
    }

    @Override
    public void onFavorite(BashOrgEntry entry) {

    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume");
        serviceHelper.init();
        content = activity.getContentSourceFromPrefs(content);
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause");
        serviceHelper.release();
        activity.saveContentSource(content);
    }




    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (isAdded()) {
            Resources res = getResources();

            int resId = res.getIdentifier(key, "string", getActivity().getPackageName());

            //TODO: move defaults somewhere
            switch (resId) {
                case R.string.pref_justify_by_width_key:
//                    Toaster.toast(getActivity().getBaseContext(),
//                            "You have changed "+ key + " to " + sharedPreferences.getBoolean(key, false));

                    break;
                case R.string.pref_night_mode_key:
//                    Toaster.toast(getActivity().getBaseContext(),
//                            "You have changed "+ key + " to " + sharedPreferences.getBoolean(key, false));

                    break;

                case R.string.pref_shorten_long_posts_key:
//                    Toaster.toast(getActivity().getBaseContext(),
//                            "You have changed "+ key + " to " + sharedPreferences.getBoolean(key, false));

                    break;

                case R.string.pref_text_size_key:
//                    Toaster.toast(getActivity().getBaseContext(),
//                            "You have changed "+ key + " to " + sharedPreferences.getString(key, "small"));

                    break;

                default:
//                    Toaster.toast(getActivity().getBaseContext(),
//                            "Some settings changed programmatically");
                    break;
            }

            //cause posts to re-render
            getLoaderManager().restartLoader(LoaderIds.CONTENT_LOADER, null, contentDataLoaderCallbacks);
        }
    }
}
