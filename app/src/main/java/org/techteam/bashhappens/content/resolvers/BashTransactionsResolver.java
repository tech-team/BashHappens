package org.techteam.bashhappens.content.resolvers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.bashorg.BashTransactionsEntry;
import org.techteam.bashhappens.db.TransactionStatus;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashTransactions;
import org.techteam.bashhappens.rest.OperationType;

import java.util.ArrayList;
import java.util.List;

public class BashTransactionsResolver extends AbstractContentResolver {
    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashTransactions.TABLE_NAME);
    }

    @Override
    protected ContentValues convertToContentValues(ContentEntry contentEntry) {
        ContentValues values = new ContentValues();
        BashTransactionsEntry entry = (BashTransactionsEntry) contentEntry;

        values.put(BashTransactions.ID, entry.getId());
        values.put(BashTransactions.STATUS, entry.getStatus().toInt());
        values.put(BashTransactions.TYPE, entry.getOperationType().toInt());
        return values;
    }

    @Override
    protected QueryField getQueryField(ContentEntry contentEntry) {
        return new QueryField(BashTransactions.ID, new String[]{((BashTransactionsEntry) contentEntry).getId()});
    }

    @Override
    protected QueryField getUpdateField(ContentEntry contentEntry) {
        return new QueryField(BashTransactions.ID, new String[]{((BashTransactionsEntry) contentEntry).getId()});
    }

    @Override
    protected QueryField getDeletionField(ContentEntry contentEntry) {
        return new QueryField(BashTransactions.ID, new String[]{((BashTransactionsEntry) contentEntry).getId()});
    }

    @Override
    protected String[] getProjection() {
        return new String[] {BashTransactions.ID, BashTransactions.STATUS, BashTransactions.TYPE};
    }


//    @Override
    public static BashTransactionsEntry getCurrentEntry(Cursor cur) {
        return new BashTransactionsEntry()
                .setId(cur.getString(cur.getColumnIndex(BashTransactions.ID)))
                .setStatus(cur.getInt(cur.getColumnIndex(BashTransactions.STATUS)))
                .setOperationType(cur.getInt(cur.getColumnIndex(BashTransactions.TYPE)));
    }

    public List<String> getEntriesByStatus(Context context, TransactionStatus status) {
        Cursor cur = getCursor(context, new String[]{BashTransactions.ID}, BashTransactions.STATUS + "= ?",
                               new String[]{ Integer.toString(status.toInt()) }, null);
        List<String> lst = new ArrayList<>();
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            lst.add(cur.getString(cur.getColumnIndex(BashTransactions.ID)));
            cur.moveToNext();
        }
        cur.close();
        return lst;
    }
    public List<String> getEntriesByStatusAndType(Context context, TransactionStatus status, OperationType type) {
        String where = BashTransactions.STATUS + "= ? and " + BashTransactions.TYPE + "= ?";
        String[] whereArgs = new String[] { Integer.toString(status.toInt()), Integer.toString(type.toInt()) };

        Cursor cur = getCursor(context, new String[]{BashTransactions.ID}, where, whereArgs, null);
        List<String> lst = new ArrayList<>();
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            lst.add(cur.getString(cur.getColumnIndex(BashTransactions.ID)));
            cur.moveToNext();
        }
        cur.close();
        return lst;
    }
}
