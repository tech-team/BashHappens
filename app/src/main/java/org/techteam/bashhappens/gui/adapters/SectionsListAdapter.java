package org.techteam.bashhappens.gui.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.techteam.bashhappens.R;

import java.util.List;

public class SectionsListAdapter
        extends ArrayAdapter<SectionsBuilder.Section> {


    private int selectedItem;

    public SectionsListAdapter(Context context, List<SectionsBuilder.Section> sections) {
        super(context, 0, sections);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("ADAPTER", Integer.toString(position));

        SectionsBuilder.Section section = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            if (section.isHeader())
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.sections_list_header, parent, false);
            else
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.sections_list_entry, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.section_label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.text.setText(section.getSideMenuText());

        viewHolder.text.setTypeface(null,
                position == selectedItem ? Typeface.BOLD : Typeface.NORMAL);

        return convertView;
    }

    public void selectItem(int selectedItem){
        this.selectedItem = selectedItem;
        notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(int position) {
        SectionsBuilder.Section section = getItem(position);


        return !section.isDisabled();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isHeader() ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2; //header + section
    }

    public int getSelectedItemId() {
        return selectedItem;
    }

    private class ViewHolder {
        public TextView text;
    }
}