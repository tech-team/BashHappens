package org.techteam.bashhappens.content.resolvers;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractContentResolver {

    protected abstract Uri _getUri();
    protected abstract ContentList<?> getEntriesList(Cursor cur);
    protected abstract ContentValues convertToContentValues(ContentEntry contentEntry);
    protected abstract QueryField getDeletionField(ContentEntry contentEntry);

    protected List<ContentValues> convertToContentValues(ContentList<?> list) {
        List<ContentValues> contentValues = new ArrayList<ContentValues>();
        for (ContentEntry entry: list) {
            contentValues.add(convertToContentValues(entry));
        }
        return contentValues;
    }

    public ContentList<?> getAllEntries(Activity activity) {
        return getEntries(activity, null, null, null, null);
    }
    public ContentList<?> getNotSortedEntries(Activity activity) {
        return getEntries(activity, null, null, null, "");
    }
    public ContentList<?> getEntries(Activity activity, String[] projection,
                                     String selection, String[] selectionArgs,
                                     String sortOrder) {
        selection += " = ?";
        Cursor cur = activity.getContentResolver().query(_getUri(),
                                                         projection,
                                                         selection,
                                                         selectionArgs,
                                                         sortOrder);
        return getEntriesList(cur);
    }

    public List<Integer> insertEntries(Activity activity, ContentList<?> list) {
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

    public int deleteAllEntries(Activity activity) {
        return activity.getContentResolver().delete(_getUri(), null, null);
    }

    public int insertEntry(Activity activity, ContentEntry entry) {
        return Integer.valueOf(activity
                               .getContentResolver()
                               .insert(_getUri(), convertToContentValues(entry))
                               .getLastPathSegment());
    }

    public int deleteEntry(Activity activity, ContentEntry entry) {
        QueryField field = getDeletionField(entry);
        return activity.getContentResolver()
                       .delete(_getUri(), field.where , field.whereArgs);
    }

    protected class QueryField {
        public String where;
        public String[] whereArgs;

        public QueryField(String where, String[] whereArgs) {
            this.where = where + " = ?";
            this.whereArgs = whereArgs;
        }
    }
}
