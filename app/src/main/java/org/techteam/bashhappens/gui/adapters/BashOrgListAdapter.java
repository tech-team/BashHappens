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
import org.techteam.bashhappens.gui.fragments.OnBashEventCallback;
import org.techteam.bashhappens.gui.fragments.OnListScrolledDownCallback;
import org.techteam.bashhappens.gui.fragments.PostsListFragment;
import org.techteam.bashhappens.util.Toaster;

import java.util.ArrayList;
import java.util.List;

public class BashOrgListAdapter
        extends RecyclerView.Adapter<BashOrgListAdapter.ViewHolder> {
    private final OnBashEventCallback voteCallback;
    private final OnListScrolledDownCallback scrolledDownCallback;
    private List<BashOrgEntry> dataset;

    private int VIEW_TYPE_ENTRY = 0;
    private int VIEW_TYPE_FOOTER = 1;

    public void setAll(ArrayList<BashOrgEntry> entries) {
        dataset.clear();
        dataset.addAll(entries);
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
    public BashOrgListAdapter(OnBashEventCallback voteCallback,
                              OnListScrolledDownCallback scrolledDownCallback,
                              List<BashOrgEntry> dataset) {
        this.voteCallback = voteCallback;
        this.scrolledDownCallback = scrolledDownCallback;

        if (dataset != null)
            this.dataset = dataset;
        else
            this.dataset = new ArrayList<BashOrgEntry>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BashOrgListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        if (viewType == VIEW_TYPE_ENTRY) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bashorg_list_entry, parent, false);

            BashOrgListAdapter.ViewHolder vh = new ViewHolder(v);
            return vh;
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_loading_entry, parent, false);

            BashOrgListAdapter.ViewHolder vh = new ViewHolder(v);
            return vh;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //footer visible
        if (position == dataset.size()) {
            scrolledDownCallback.onScrolledDown();
            return;
        }

        final BashOrgEntry entry = dataset.get(position);

        //set data
        holder.id.setText(entry.getId());
        holder.date.setText(entry.getCreationDate());
        holder.text.setText(entry.getText());

        holder.rating.setText(entry.getRating());

        //TODO: set buttons state according to DB


        final VotedCallback votedCallback = new VotedCallback() {
            @Override
            public void onVoted(BashOrgEntry entry) {
                holder.rating.setText(entry.getRating());

                // TODO: make things on voted
            }

            @Override
            public void onBayan(BashOrgEntry entry) {
                // TODO: make things on bayan
            }
        };

        //set handlers
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                Toaster.toast(context,
                        "Share pressed for entry.id: " + entry.getId());

                share(context, entry);
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
                voteCallback.onMakeVote(entry, position, BashOrgEntry.VoteDirection.BAYAN, votedCallback);
                Toaster.toast(context,
                        "Bayan pressed for entry.id: " + entry.getId());
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                voteCallback.onMakeVote(entry, position, BashOrgEntry.VoteDirection.UP, votedCallback);
                Toaster.toast(context,
                        "Like pressed for entry.id: " + entry.getId());
            }
        });

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                voteCallback.onMakeVote(entry, position, BashOrgEntry.VoteDirection.DOWN, votedCallback);
                Toaster.toast(context,
                        "Dislike pressed for entry.id: " + entry.getId());
            }
        });
    }

    private void share(Context context, BashOrgEntry entry) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                BashOrgListAdapter.this.formatEntryForSharing(context, entry));
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public BashOrgEntry get(int position) {
        return dataset.get(position);
    }

    private String formatEntryForSharing(Context context, BashOrgEntry entry) {
        StringBuilder sb = new StringBuilder();

        String delimiter = "\n";
        String emptyLine = " \n";
        String hashTag = "#" + context.getString(R.string.app_name);

        sb.append(context.getString(R.string.app_name));
        sb.append(delimiter);
        sb.append(entry.getCreationDate());
        sb.append(delimiter);
        sb.append(emptyLine);
        sb.append(entry.getText());
        sb.append(delimiter);
        //TODO: append real link
        sb.append(delimiter);
        sb.append("http://bash.im");
        sb.append(delimiter);
        sb.append(hashTag);

        return sb.toString();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size() + 1; //+footer
    }

    @Override
    public int getItemViewType(int position) {
        return position < dataset.size() ? VIEW_TYPE_ENTRY : VIEW_TYPE_FOOTER;
    }

    public interface VotedCallback {
        void onVoted(BashOrgEntry entry);
        void onBayan(BashOrgEntry entry);
    }
}