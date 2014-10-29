package org.techteam.bashhappens.content.resolvers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashBayan;

public class BashBayanResolver extends AbstractContentResolver {
    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashBayan.TABLE_NAME);
    }

    @Override
    protected String[] getProjection() {
        return new String[]{BashBayan.ARTICLE_ID, BashBayan.IS_BAYAN};
    }

    @Override
    protected ContentList<?> getEntriesList(Cursor cur) {
        cur.moveToFirst();

        BashOrgList bashOrgEntryList = new BashOrgList();
        while (!cur.isAfterLast()) {
            BashOrgEntry entry = new BashOrgEntry()
                    .setId(cur.getString(cur.getColumnIndex(BashBayan.ARTICLE_ID)))
                    .setBayan(cur.getInt(cur.getColumnIndex(BashBayan.IS_BAYAN)) == 1);

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

        values.put(BashBayan.ARTICLE_ID, bashOrgEntry.getId());
        values.put(BashBayan.IS_BAYAN, bashOrgEntry.getIsBayan() ? 1 : 0);
        return values;
    }

    @Override
    protected QueryField getDeletionField(ContentEntry contentEntry) {
        return new QueryField(BashBayan.ARTICLE_ID,
                              new String[]{((BashOrgEntry) contentEntry).getId()});
    }

}
