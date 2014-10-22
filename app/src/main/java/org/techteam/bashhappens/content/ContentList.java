package org.techteam.bashhappens.content;

import java.util.ArrayList;

public class ContentList<T extends ContentEntry> {

    private ArrayList<T> entries;

    public ContentList(ArrayList<T> entries) {
        this.entries = entries;
    }

    public ArrayList<T> getEntries() {
        return entries;
    }
}
