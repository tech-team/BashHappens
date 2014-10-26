package org.techteam.bashhappens.gui.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentFactory;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.gui.adapters.BashOrgListAdapter;
import org.techteam.bashhappens.gui.loaders.ContentAsyncLoader;
import org.techteam.bashhappens.util.LoaderIds;
import org.techteam.bashhappens.util.Toaster;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class PostsListFragment
        extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<ContentList> {

    private static final class BundleKeys {
        public static final String FACTORY = "FACTORY";
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;

    private ContentFactory factory = null;
    private ContentSource content = null;
    private BashOrgListAdapter adapter = null;
    private List<ContentEntry> list = new LinkedList<ContentEntry>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);

        return rootView;
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

        getLoaderManager().initLoader(LoaderIds.CONTENT_LOADER, null, this);
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, adapter.getItem(position).getText());
//        sendIntent.setType("text/plain");
//        startActivity(sendIntent);
//    }

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
        //adapter = null;
        content = factory.buildContent(ContentFactory.ContentSection.BASH_ORG_NEWEST, true);
        getLoaderManager().restartLoader(LoaderIds.CONTENT_LOADER, null, PostsListFragment.this);
    }

    @Override
    public Loader<ContentList> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LoaderIds.CONTENT_LOADER:
                return new ContentAsyncLoader(getActivity(), content);
        }
        throw new IllegalArgumentException("Loader with given id is not found");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader<ContentList> contentListLoader, ContentList contentList) {
        mSwipeRefreshLayout.setRefreshing(false);

        switch (contentList.getStoredContentType()) {
            case BASH_ORG:
                if (adapter == null) {
                    // TODO: кажется слишком глупо пересоздавать adapter каждый раз, когда обновляем, надо "добавлять" в начало массива, но в то же время учесть пересоздание адаптера, если меняется источник данных
                    adapter = new BashOrgListAdapter(contentList.getEntries());
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.addAll(contentList.getEntries());
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
}
