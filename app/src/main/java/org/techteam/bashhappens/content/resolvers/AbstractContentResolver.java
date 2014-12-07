package org.techteam.bashhappens.content.resolvers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.db.tables.BashBayan;
import org.techteam.bashhappens.db.tables.BashNewest;
import org.techteam.bashhappens.db.tables.BashFavs;
import org.techteam.bashhappens.db.tables.BashLikes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractContentResolver {

    protected abstract Uri _getUri();
    protected abstract ContentValues convertToContentValues(ContentEntry contentEntry);
    protected abstract QueryField getDeletionField(ContentEntry contentEntry);
    protected abstract String[] getProjection();

    protected List<ContentValues> convertToContentValues(ContentList<?> list) {
        List<ContentValues> contentValues = new ArrayList<ContentValues>();
        for (ContentEntry entry: list) {
            contentValues.add(convertToContentValues(entry));
        }
        return contentValues;
    }

    public static AbstractContentResolver getResolver(ContentSection section) {
        switch (section) {
            case BASH_ORG_NEWEST:
                return new BashNewestResolver();
            case IT_HAPPENS_NEWEST:
                //TODO: ItHappensNewest resolver, BashLikes prbbly
                return null;
            default:
                return null;
        }
    }

    public Cursor getCursor(Context context) {
        return getCursor(context, null, null, null, null);
    }
    public Cursor getCursor(Context context, String[] projection,
                                     String selection, String[] selectionArgs,
                                     String sortOrder) {
        if (selection != null) {
            selection += " = ?";
        }
        if (projection == null) {
            projection = getProjection();
        }
        return context.getContentResolver().query(_getUri(),
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    public abstract ContentEntry getCurrentEntry(Cursor cur);

    public List<Integer> insertEntries(Context context, ContentList<?> list) {
        List<Integer> insertedIds = new ArrayList<Integer>();
        for(ContentValues values: convertToContentValues(list)) {
            insertedIds.add(Integer.valueOf(
                            context
                            .getContentResolver()
                            .insert(_getUri(), values)
                            .getLastPathSegment()));
        }
        return insertedIds;
    }

    public int deleteAllEntries(Context context) {
        return context.getContentResolver().delete(_getUri(), null, null);
    }

    public <T extends ContentEntry> int insertEntry(Context context, T entry) {
        return Integer.valueOf(context
                               .getContentResolver()
                               .insert(_getUri(), convertToContentValues(entry))
                               .getLastPathSegment());
    }

    public <T extends ContentEntry> int deleteEntry(Context context, T entry) {
        QueryField field = getDeletionField(entry);
        return context.getContentResolver()
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

    public static Map<String, Integer> truncateAll(Context context) {
        Map<String, Integer> deletions = new HashMap<String, Integer>();
        deletions.put(BashNewest.TABLE_NAME, new BashNewestResolver().deleteAllEntries(context));
        return deletions;
    }

    //////////////////// Usage example //////////////////////
    /*BashOrgEntry entry = new BashOrgEntry().setId("1").setText("see?");
    BashOrgList list = new BashOrgList();
    list.add(entry);
    AbstractContentResolver resolver = AbstractContentResolver.getResolver(ContentSection.BASH_ORG_NEWEST);
    resolver.insertEntries(this, list);
    Cursor cur = resolver.getCursor(this);

    BashOrgList bashOrgEntryList = new BashOrgList();
    cur.moveToFirst();
    while (!cur.isAfterLast()) {
        bashOrgEntryList.add((BashOrgEntry)resolver.getCurrentEntry(cur));
        cur.moveToNext();
    }
    cur.close();
    AbstractContentResolver.truncateAll(this);*/
}
