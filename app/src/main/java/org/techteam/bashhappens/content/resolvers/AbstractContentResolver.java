package org.techteam.bashhappens.content.resolvers;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentList;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContentResolver {

    protected abstract Uri _getUri();
    protected abstract ContentList<?> getEntries(Cursor cur);
    protected abstract ContentValues convertToContentValues(ContentEntry contentEntry);

    protected List<ContentValues> convertToContentValues(ContentList<?> list) {
        List<ContentValues> contentValues = new ArrayList<ContentValues>();
        for (ContentEntry entry: list) {
            contentValues.add(convertToContentValues(entry));
        }
        return contentValues;
    }

    public ContentList<?> getCache(Activity activity) {
        Cursor cur = activity.getContentResolver().query(_getUri(), null, null, null, null);
        return getEntries(cur);
    }

    public List<Integer> fillCache(Activity activity, ContentList<?> list) {
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

    public int clearCache(Activity activity) {
        return activity.getContentResolver().delete(_getUri(), null, null);
    }

    public int insertEntry(Activity activity, ContentEntry entry) {
        return Integer.valueOf(activity
                               .getContentResolver()
                               .insert(_getUri(), convertToContentValues(entry))
                               .getLastPathSegment());
    }
}
