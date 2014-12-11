package org.techteam.bashhappens.gui.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.resolvers.BashResolver;
import org.techteam.bashhappens.gui.fragments.OnBashEventCallback;
import org.techteam.bashhappens.gui.fragments.OnListScrolledDownCallback;
import org.techteam.bashhappens.gui.views.EllipsizingTextView;
import org.techteam.bashhappens.gui.views.PostToolbarView;
import org.techteam.bashhappens.gui.views.RatingView;
import org.techteam.bashhappens.util.Clipboard;
import org.techteam.bashhappens.util.Toaster;

import java.util.ArrayList;
import java.util.List;

public class BashOrgListAdapter
        extends CursorRecyclerViewAdapter<BashOrgListAdapter.ViewHolder> {
    private final OnBashEventCallback eventCallback;
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

    public boolean isEmpty() {
        return getItemCount() - 1 == 0; //-footer
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        //header
        public TextView id;
        public TextView date;
        public ImageButton overflow;

        //content
        public EllipsizingTextView text;

        public TextView ellipsizeHint;

        //bottom buttons panel
        public PostToolbarView toolbarView;


        public ViewHolder(View v) {
            super(v);

            id = (TextView) v.findViewById(R.id.post_id);
            date = (TextView) v.findViewById(R.id.post_date);
            overflow = (ImageButton) v.findViewById(R.id.overflow_button);

            text = (EllipsizingTextView) v.findViewById(R.id.post_text);

            ellipsizeHint = (TextView) v.findViewById(R.id.post_ellipsize_hint);

            toolbarView = (PostToolbarView) v.findViewById(R.id.post_toolbar_view);
        }
    }

    public BashOrgListAdapter(Cursor contentCursor,
                              OnBashEventCallback eventCallback,
                              OnListScrolledDownCallback scrolledDownCallback) {
        super(contentCursor); // TODO
        this.eventCallback = eventCallback;
        this.scrolledDownCallback = scrolledDownCallback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BashOrgListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v;
        if (viewType == VIEW_TYPE_ENTRY) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bashorg_list_entry, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_loading_entry, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor, final int position) {
        //footer visible
        if (position == cursor.getCount()) {
            scrolledDownCallback.onScrolledDown();
            return;
        }

        final BashOrgEntry entry = BashResolver.getCurrentEntry(cursor);

        //set data
        holder.id.setText(entry.getId());
        holder.date.setText(entry.getCreationDate());
        holder.text.setText(entry.getText());

        int direction = entry.getDirection();
        switch(direction) {
            case 0:
                holder.toolbarView._setRatingState(RatingView.State.IDLE);
                break;
            case 1:
                holder.toolbarView._setRatingState(RatingView.State.LIKED);
                break;
            case -1:
                holder.toolbarView._setRatingState(RatingView.State.DISLIKED);
                break;
        }

        holder.toolbarView.setRating(entry.getRating());
        holder.toolbarView._setBayaned(entry.getIsBayan());
        holder.toolbarView._setFaved(entry.isFavorite());

        //set handlers
        holder.toolbarView.setListener(new PostToolbarView.Listener() {
            @Override
            public void likePressed(PostToolbarView view) {
                eventCallback.onMakeVote(entry, position, BashOrgEntry.VoteDirection.UP);

                Context context = view.getContext();
                Toaster.toast(context,
                        "Like pressed for entry.id: " + entry.getId());
            }

            @Override
            public void dislikePressed(PostToolbarView view) {
                eventCallback.onMakeVote(entry, position, BashOrgEntry.VoteDirection.DOWN);

                Context context = view.getContext();
                Toaster.toast(context,
                        "Dislike pressed for entry.id: " + entry.getId());
            }

            @Override
            public void bayanPressed(PostToolbarView view) {
                eventCallback.onMakeVote(entry, position, BashOrgEntry.VoteDirection.BAYAN);

                Context context = view.getContext();
                Toaster.toast(context,
                        "Bayan pressed for entry.id: " + entry.getId());
            }

            @Override
            public void sharePressed(PostToolbarView view) {
                Context context = view.getContext();
                share(context, entry);
                Toaster.toast(context,
                        "Share pressed for entry.id: " + entry.getId());
            }

            @Override
            public void favPressed(PostToolbarView view) {
                eventCallback.onFavorite(entry);
                Context context = view.getContext();
                Toaster.toast(context,
                        "Fav pressed for entry.id: " + entry.getId());
            }
        });

        holder.text.addEllipsizeListener(new EllipsizingTextView.EllipsizeListener() {
            @Override
            public void ellipsizeStateChanged(boolean ellipsized) {
                holder.ellipsizeHint.setVisibility(ellipsized ? View.VISIBLE : View.GONE);
            }
        });

        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.text.setMaxLines(Integer.MAX_VALUE);
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();

                PopupMenu menu = new PopupMenu(context, v);
                menu.inflate(R.menu.entry_context_menu);

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                        switch (item.getItemId()) {
                            case R.id.copy_text:
                                Clipboard.copyText(
                                        context,
                                        R.string.clipboard_label_for_post,
                                        entry.getText());

                                Toaster.toast(context, context.getString(R.string.post_text_copied));

                                return true;
                            case R.id.copy_link:
                                Clipboard.copyText(
                                        context,
                                        R.string.clipboard_label_for_link,
                                        entry.getLink());

                                Toaster.toast(context, context.getString(R.string.post_link_copied));

                                return true;
                            default:
                                return false;
                        }
                    }
                });

                menu.show();
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
        sb.append(delimiter);
        sb.append(entry.getLink());
        sb.append(delimiter);
        sb.append(hashTag);

        return sb.toString();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Cursor cursor = getCursor();
        if (cursor == null)
            return 1;
        return cursor.getCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position < BashOrgListAdapter.this.getItemCount()-1 ? VIEW_TYPE_ENTRY : VIEW_TYPE_FOOTER;
    }
}