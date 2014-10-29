package org.techteam.bashhappens.content.resolvers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;
import org.techteam.bashhappens.db.tables.BashBayan;
import org.techteam.bashhappens.db.tables.BashLikes;
import org.techteam.bashhappens.db.tables.BashTable;

public abstract class BashResolver extends AbstractContentResolver {
    @Override
    protected abstract Uri _getUri();

    @Override
    public ContentList<?> getAllEntries(Context context) {
        String[] projection = {BashTable.ID, BashTable.TEXT, BashTable.DATE, BashTable.RATING,
                BashLikes.DIRECTION, BashBayan.IS_BAYAN};
        return getEntries(context, projection, null, null, null);
    }

    @Override
    protected ContentList<?> getEntriesList(Cursor cur) {
        cur.moveToFirst();

        BashOrgList bashOrgEntryList = new BashOrgList();
        while (!cur.isAfterLast()) {
            BashOrgEntry entry = new BashOrgEntry(cur.getString(cur.getColumnIndex(BashTable.ID)),
                    cur.getString(cur.getColumnIndex(BashTable.DATE)),
                    cur.getString(cur.getColumnIndex(BashTable.TEXT)),
                    cur.getString(cur.getColumnIndex(BashTable.RATING)),
                    cur.getInt(cur.getColumnIndex(BashLikes.DIRECTION)),
                    cur.getInt(cur.getColumnIndex(BashBayan.IS_BAYAN)) == 1);
            bashOrgEntryList.add(entry);
            cur.moveToNext();
        }
        cur.close();
        return bashOrgEntryList;
    }

    @Override
    protected ContentValues convertToContentValues(ContentEntry contentEntry) {
        ContentValues values = new ContentValues();
        BashOrgEntry bashOrgEntry = (BashOrgEntry) contentEntry;

        values.put(BashTable.ID, bashOrgEntry.getId());
        values.put(BashTable.TEXT, bashOrgEntry.getText());
        values.put(BashTable.DATE, bashOrgEntry.getCreationDate());
        values.put(BashTable.RATING, bashOrgEntry.getRating());
        return values;
    }

    @Override
    protected QueryField getDeletionField(ContentEntry contentEntry) {
        return new QueryField(BashTable.ID, new String[]{((BashOrgEntry) contentEntry).getId()});
    }
}
