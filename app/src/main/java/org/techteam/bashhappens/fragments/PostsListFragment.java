package org.techteam.bashhappens.fragments;

import android.os.Handler;
import android.app.ListFragment;
import android.content.Intent;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PostsListFragment
        extends ListFragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    BashOrgListAdapter adapter = null;

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

//        String li = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

//        BashOrgEntry entry1 = new BashOrgEntry("#12345", "23.10.2014", li);
//        BashOrgEntry entry2 = new BashOrgEntry("#12345", "23.10.2014", li);
//        BashOrgEntry entry3 = new BashOrgEntry("#12345", "23.10.2014", li);
//
//        List<BashOrgEntry> list = new ArrayList<BashOrgEntry>();
//        list.add(entry1);
//        list.add(entry2);
//        list.add(entry3);

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ContentFactory factory = new ContentFactory(Locale.getDefault().toString());
                    ContentSource content = factory.buildContent(ContentFactory.ContentSection.BASH_ORG_NEWEST);
                    final ContentList<?> list = content.retrieveNextList();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new BashOrgListAdapter((List<BashOrgEntry>) list.getEntries());
                            setListAdapter(adapter);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FeedOverflowException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();

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
