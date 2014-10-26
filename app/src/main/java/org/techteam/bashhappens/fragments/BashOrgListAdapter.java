package org.techteam.bashhappens.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;

import java.util.ArrayList;
import java.util.List;

public class BashOrgListAdapter
        extends RecyclerView.Adapter<BashOrgListAdapter.ViewHolder> {
    List<BashOrgEntry> dataset;

    public void setAll(ArrayList<BashOrgEntry> entries) {
        //TODO: i don't think it will work such easy
        dataset = entries;
    }

    public void addAll(ArrayList<BashOrgEntry> entries) {
        dataset.addAll(entries);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView id;
        public TextView date;
        public TextView text;

        public ViewHolder(View v) {
            super(v);

            id = (TextView) v.findViewById(R.id.post_id);
            date = (TextView) v.findViewById(R.id.post_date);
            text = (TextView) v.findViewById(R.id.post_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BashOrgListAdapter(List<BashOrgEntry> dataset) {
        this.dataset = dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BashOrgListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bashorg_list_entry, parent, false);

        BashOrgListAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.id.setText(dataset.get(position).getId());
        holder.date.setText(dataset.get(position).getCreationDate());
        holder.text.setText(dataset.get(position).getText());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}