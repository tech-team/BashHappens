package org.techteam.bashhappens.db.resolvers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.Entry;
import org.techteam.bashhappens.content.bashorg.TransactionEntry;
import org.techteam.bashhappens.db.TransactionStatus;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashTransactions;
import org.techteam.bashhappens.rest.OperationType;

import java.util.ArrayList;
import java.util.List;

public class TransactionsResolver extends AbstractContentResolver {
    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashTransactions.TABLE_NAME);
    }

    @Override
    protected ContentValues convertToContentValues(Entry entry) {
        ContentValues values = new ContentValues();
        TransactionEntry trEntry = (TransactionEntry) entry;

        values.put(BashTransactions.ID, trEntry.getId());
        values.put(BashTransactions.STATUS, trEntry.getStatus().toInt());
        values.put(BashTransactions.TYPE, trEntry.getOperationType().toInt());
        return values;
    }

    @Override
    protected QueryField getQueryField(Entry entry) {
        return new QueryField(BashTransactions.ID, new String[]{((TransactionEntry) entry).getId()});
    }

    @Override
    protected QueryField getUpdateField(Entry entry) {
        return new QueryField(BashTransactions.ID, new String[]{((TransactionEntry) entry).getId()});
    }

    @Override
    protected QueryField getDeletionField(Entry entry) {
        return new QueryField(BashTransactions.ID, new String[]{((TransactionEntry) entry).getId()});
    }

    @Override
    protected String[] getProjection() {
        return new String[] {BashTransactions.ID, BashTransactions.STATUS, BashTransactions.TYPE};
    }


//    @Override
    public static TransactionEntry getCurrentEntry(Cursor cur) {
        return new TransactionEntry()
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
