package org.techteam.bashhappens.content.resolvers;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContentResolver {

    protected abstract Uri _getUri();
    protected abstract List<? extends ContentEntry> getEntries(Cursor cur);
    protected abstract ContentValues convertToContentValues(ContentEntry contentEntry);

    protected List<ContentValues> convertToContentValues(List<? extends ContentEntry> list) {
        List<ContentValues> contentValues = new ArrayList<ContentValues>();
        for (ContentEntry entry: list) {
            contentValues.add(convertToContentValues(entry));
        }
        return contentValues;
    }

    public List<? extends ContentEntry> getCache(Activity activity) {
        Cursor cur = activity.getContentResolver().query(_getUri(), null, null, null, null);
        return getEntries(cur);
    }

    public List<Integer> fillCache(Activity activity, List<? extends ContentEntry> list) {
        List<Integer> insertedIds = new ArrayList<Integer>();
        for(ContentValues values: convertToContentValues(list)) {
            insertedIds.add(Integer.valueOf(
                            activity
                            .getContentResolver()
                            .insert(_getUri(), values)
                            .getLastPathSegment()));
        }
        return insertedIds;
    }

    public int insertEntry(Activity activity, ContentEntry entry) {
        return Integer.valueOf(activity
                               .getContentResolver()
                               .insert(_getUri(), convertToContentValues(entry))
                               .getLastPathSegment());
    }
}
