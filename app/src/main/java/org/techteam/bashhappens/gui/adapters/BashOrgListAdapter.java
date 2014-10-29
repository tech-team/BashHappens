package org.techteam.bashhappens.gui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.gui.fragments.PostsListFragment;
import org.techteam.bashhappens.util.Toaster;

import java.util.ArrayList;
import java.util.List;

public class BashOrgListAdapter
        extends RecyclerView.Adapter<BashOrgListAdapter.ViewHolder> {
    private final PostsListFragment.OnBashVoteCallback voteCallback;
    private List<BashOrgEntry> dataset;

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

        //header
        public TextView id;
        public TextView date;

        //content
        public TextView text;

        //bottom buttons panel
        public ImageButton share;
        public ImageButton fav;
        public Button bayan;
        public ImageButton like;
        public TextView rating;
        public ImageButton dislike;


        public ViewHolder(View v) {
            super(v);

            id = (TextView) v.findViewById(R.id.post_id);
            date = (TextView) v.findViewById(R.id.post_date);

            text = (TextView) v.findViewById(R.id.post_text);

            share = (ImageButton) v.findViewById(R.id.post_share);
            fav = (ImageButton) v.findViewById(R.id.post_fav);
            bayan = (Button) v.findViewById(R.id.post_bayan);
            like = (ImageButton) v.findViewById(R.id.post_like);
            rating = (TextView) v.findViewById(R.id.post_rating);
            dislike = (ImageButton) v.findViewById(R.id.post_dislike);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BashOrgListAdapter(PostsListFragment.OnBashVoteCallback voteCallback,
                              List<BashOrgEntry> dataset) {
        this.voteCallback = voteCallback;
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
        final BashOrgEntry entry = dataset.get(position);

        //set data
        holder.id.setText(entry.getId());
        holder.date.setText(entry.getCreationDate());
        holder.text.setText(entry.getText());

        holder.rating.setText(entry.getRating());

        //TODO: set buttons state according to DB

        //set handlers
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                Toaster.toast(context,
                        "Share pressed for entry.id: " + entry.getId());

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        BashOrgListAdapter.this.formatEntryForSharing(entry));
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }
        });

        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                Toaster.toast(context,
                        "Fav pressed for entry.id: " + entry.getId());
            }
        });

        holder.bayan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                Toaster.toast(context,
                        "Bayan pressed for entry.id: " + entry.getId());
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                voteCallback.onVote(entry, BashOrgEntry.VoteDirection.UP);
                Toaster.toast(context,
                        "Like pressed for entry.id: " + entry.getId());
            }
        });

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                voteCallback.onVote(entry, BashOrgEntry.VoteDirection.DOWN);
                Toaster.toast(context,
                        "Dislike pressed for entry.id: " + entry.getId());
            }
        });
    }

    private String formatEntryForSharing(BashOrgEntry entry) {
        //TODO: make it look nice
        return entry.getText();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}