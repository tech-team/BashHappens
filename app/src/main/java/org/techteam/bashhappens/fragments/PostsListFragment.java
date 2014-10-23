package org.techteam.bashhappens.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentFactory;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.FeedOverflowException;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostsListFragment extends ListFragment {

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
                            setListAdapter(new BashOrgListAdapter((List<BashOrgEntry>) list.getEntries()));
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
