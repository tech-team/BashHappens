package org.techteam.bashhappens.fragments;

import android.os.Handler;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.ContentFactory;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.FeedOverflowException;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.fragments.loaders.ContentAsyncLoader;
import org.techteam.bashhappens.util.LoaderIds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PostsListFragment
        extends ListFragment
        implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<ContentList> {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    BashOrgListAdapter adapter = null;
    private ContentFactory factory = null;
    private ContentSource content = null;
    private BashOrgListAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);

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

        factory = new ContentFactory(Locale.getDefault().toString());
        content = factory.buildContent(ContentFactory.ContentSection.BASH_ORG_NEWEST);

        getLoaderManager().initLoader(LoaderIds.CONTENT_LOADER, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, adapter.getItem(position).getText());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //updateCountries();

                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 5000);
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
        switch (contentList.getStoredContentType()) {
            case BASH_ORG:
                adapter = new BashOrgListAdapter(contentList.getEntries());
                break;
            case IT_HAPPENS:
                // TODO: add ItHappens adapter
                break;
        }
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<ContentList> contentListLoader) {
        // TODO
    }

    private class BashOrgListAdapter extends ArrayAdapter<BashOrgEntry> {

        public BashOrgListAdapter(List<BashOrgEntry> objects) {
            super(getActivity(), 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.bashorg_list_entry, parent, false);
                viewHolder = new ViewHolder();

                viewHolder.id = (TextView) convertView.findViewById(R.id.post_id);
                viewHolder.date = (TextView) convertView.findViewById(R.id.post_date);
                viewHolder.text = (TextView) convertView.findViewById(R.id.post_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.id.setText(getItem(position).getId());
            viewHolder.date.setText(getItem(position).getCreationDate());
            viewHolder.text.setText(getItem(position).getText());
            return convertView;
        }

        private class ViewHolder {
            public TextView id;
            public TextView date;
            public TextView text;
        }
    }
}
