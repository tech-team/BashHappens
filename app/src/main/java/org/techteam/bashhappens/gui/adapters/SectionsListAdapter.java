package org.techteam.bashhappens.gui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SectionsListAdapter
        extends ArrayAdapter<SectionsBuilder.Section> {

    private List<SectionsBuilder.Section> sections;

    public SectionsListAdapter(Context context, List<SectionsBuilder.Section> sections) {
        super(context, 0, sections);
    }

    public List<SectionsBuilder.Section> getSections() {
        return sections;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("ADAPTER", Integer.toString(position));

        SectionsBuilder.Section section = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.text.setText(section.getSideMenuText());

        if (section.isDisabled()) {
            viewHolder.text.setEnabled(false);
            viewHolder.text.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        } else {
            viewHolder.text.setEnabled(true);
            viewHolder.text.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        SectionsBuilder.Section section = getItem(position);

        return !section.isDisabled();
    }

    private class ViewHolder {
        public TextView text;
    }
}