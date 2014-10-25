package org.techteam.bashhappens.content;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class ContentList<T extends ContentEntry> implements Iterable<T> {

    private ArrayList<T> entries;

    public ContentList(ArrayList<T> entries) {
        this.entries = entries;
    }

    public ArrayList<T> getEntries() {
        return entries;
    }

    public void add(T entry) {
        entries.add(entry);
    }

    public ContentType getStoredContentType() {
        return getEntries().get(0).getContentType();
    }

    @Override
    public Iterator<T> iterator() {
        return entries.iterator();
    }
}
