package org.techteam.bashhappens.content.resolvers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.db.tables.BashLikes;
import org.techteam.bashhappens.db.tables.BashNewest;
import org.techteam.bashhappens.db.tables.BashTransactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractContentResolver {

    protected abstract Uri _getUri();
    protected abstract ContentValues convertToContentValues(ContentEntry contentEntry);
    protected abstract QueryField getUpdateField(ContentEntry contentEntry);
    protected abstract QueryField getQueryField(ContentEntry contentEntry);
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
            case BASH_ORG_LIKES:
                return new BashLikesResolver();
            case BASH_ORG_FAVS:
                return new BashFavsResolver();
            case BASH_ORG_BAYAN:
                return new BashBayanResolver();
            case TRANSACTIONS:
                return new TransactionsResolver();
            case IT_HAPPENS_NEWEST:
                //TODO: ItHappensNewest resolver
                return null;
            default:
                return null;
        }
    }

    public Cursor getCursor(Context context) {
        return getCursor(context, getProjection(), null, null, null);
    }
    public Cursor getCursor(Context context, String[] projection,
                                     String selection, String[] selectionArgs,
                                     String sortOrder) {
        /*if (selection != null) {
            selection += " = ?";
        }*/
        if (projection == null) {
            projection = getProjection();
        }
        return context.getContentResolver().query(_getUri(),
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    // Because it is needed in static context
//    public abstract ContentEntry getCurrentEntry(Cursor cur);

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

    public int insert(Context context, ContentEntry entry) {
        QueryField field = getQueryField(entry);
        Cursor cur = getCursor(context, null, field.where, field.whereArgs, null);
        int rows = cur.getCount();
        cur.close();
        if (rows != 0) {
            return updateEntry(context, entry);
        }
        else {
            return insertEntry(context, entry);
        }
    }

    public  int insertEntry(Context context, ContentEntry entry) {
        return Integer.valueOf(context
                               .getContentResolver()
                               .insert(_getUri(), convertToContentValues(entry))
                               .getLastPathSegment());
    }

    public int deleteEntry(Context context, ContentEntry entry) {
        QueryField field = getDeletionField(entry);
        return (field == null)
                ? (-1)
                : context.getContentResolver()
                      .delete(_getUri(), field.where , field.whereArgs);
    }

    public int updateEntry(Context context, ContentEntry entry) {
        QueryField field = getUpdateField(entry);
        return (field == null)
                ? (-1)
                : context.getContentResolver()
                    .update(_getUri(), this.convertToContentValues(entry), field.where, field.whereArgs);
    }

    public static Map<String, Integer> truncateAll(Context context) {
        Map<String, Integer> deletions = new HashMap<String, Integer>();
        deletions.put(BashNewest.TABLE_NAME, new BashNewestResolver().deleteAllEntries(context));
        deletions.put(BashLikes.TABLE_NAME, new BashLikesResolver().deleteAllEntries(context));
        deletions.put(BashTransactions.TABLE_NAME, new TransactionsResolver().deleteAllEntries(context));
        return deletions;
    }

    protected class QueryField {
        public String where;
        public String[] whereArgs;

        public QueryField(String where, String[] whereArgs) {
            this.where = where + " = ?";
            this.whereArgs = whereArgs;
        }
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


    BashTransactionsResolver resolver = (BashTransactionsResolver) AbstractContentResolver.getResolver(ContentSection.BASH_ORG_TRANSACTIONS);
    resolver.insert(this, new BashTransactionsEntry().setId("unsee").setStatus(TransactionStatus.ERROR).setOperationType(OperationType.IT_VOTE));
    resolver.insert(this, new BashTransactionsEntry().setId("see").setStatus(TransactionStatus.ERROR));
    List<String> lst1 = resolver.getEntriesByStatusAndType(this, TransactionStatus.ERROR, OperationType.IT_VOTE);
    List<String> lst2 = resolver.getEntriesByStatus(this, TransactionStatus.ERROR);
    AbstractContentResolver.truncateAll(this);
    */
}
