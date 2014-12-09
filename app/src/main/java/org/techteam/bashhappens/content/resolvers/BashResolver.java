package org.techteam.bashhappens.content.resolvers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;
import org.techteam.bashhappens.db.tables.AbstractTable;

public abstract class BashResolver extends AbstractContentResolver {

    @Override
    protected abstract Uri _getUri();

    @Override
    protected String[] getProjection() {
        return new String[] {AbstractTable.ID, AbstractTable.TEXT, AbstractTable.DATE, AbstractTable.RATING};
                //BashLikes.DIRECTION, BashBayan.IS_BAYAN};
    }

    @Override
    protected QueryField getUpdateField(ContentEntry contentEntry) {
        return null;
    }

    @Override
    protected QueryField getDeletionField(ContentEntry contentEntry) {
        return new QueryField(AbstractTable.ID, new String[]{((BashOrgEntry) contentEntry).getId()});
    }

    @Override
    public Cursor getCursor(Context context) {
        return getCursor(context, getProjection(), null, null, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BashOrgEntry getCurrentEntry(Cursor cur) {
        return new BashOrgEntry()
                    .setId(cur.getString(cur.getColumnIndex(AbstractTable.ID)))
                    .setCreationDate(cur.getString(cur.getColumnIndex(AbstractTable.DATE)))
                    .setText(cur.getString(cur.getColumnIndex(AbstractTable.TEXT)))
                    .setRating(cur.getString(cur.getColumnIndex(AbstractTable.RATING)));
                    //.setDirection(cur.getInt(cur.getColumnIndex(BashLikes.DIRECTION)))
                    //.setBayan(cur.getInt(cur.getColumnIndex(BashBayan.IS_BAYAN)) == 1);
    }

    @Override
    protected ContentValues convertToContentValues(ContentEntry contentEntry) {
        ContentValues values = new ContentValues();
        BashOrgEntry bashOrgEntry = (BashOrgEntry) contentEntry;

        values.put(AbstractTable.ID, bashOrgEntry.getId());
        values.put(AbstractTable.TEXT, bashOrgEntry.getText());
        values.put(AbstractTable.DATE, bashOrgEntry.getCreationDate());
        values.put(AbstractTable.RATING, bashOrgEntry.getRating());
        return values;
    }

    @Deprecated
    protected ContentList<?> getEntriesList(Cursor cur) {
        cur.moveToFirst();

        BashOrgList bashOrgEntryList = new BashOrgList();
        while (!cur.isAfterLast()) {
            BashOrgEntry entry = new BashOrgEntry()
                    .setId(cur.getString(cur.getColumnIndex(AbstractTable.ID)))
                    .setCreationDate(cur.getString(cur.getColumnIndex(AbstractTable.DATE)))
                    .setText(cur.getString(cur.getColumnIndex(AbstractTable.TEXT)))
                    .setRating(cur.getString(cur.getColumnIndex(AbstractTable.RATING)));
                    //.setDirection(cur.getInt(cur.getColumnIndex(BashLikes.DIRECTION)))
                    //.setBayan(cur.getInt(cur.getColumnIndex(BashBayan.IS_BAYAN)) == 1);

            bashOrgEntryList.add(entry);
            cur.moveToNext();
        }
        cur.close();
        return bashOrgEntryList;
    }
}
