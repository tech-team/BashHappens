package org.techteam.bashhappens.content.resolvers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashBayan;
import org.techteam.bashhappens.db.tables.BashCache;
import org.techteam.bashhappens.db.tables.BashLikes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BashCacheResolver extends AbstractContentResolver {

    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashCache.TABLE_NAME);
    }
    protected QueryField getDeletionField(ContentEntry contentEntry) {
        return new QueryField(BashCache.ID, new String[]{((BashOrgEntry) contentEntry).getId()});
    }

    @Override
    protected BashOrgList getEntriesList(Cursor cur) {
        cur.moveToFirst();

        BashOrgList bashOrgEntryList = new BashOrgList();
        while (!cur.isAfterLast()) {
            BashOrgEntry entry = new BashOrgEntry(cur.getString(cur.getColumnIndex(BashCache.ID)),
                                                  cur.getString(cur.getColumnIndex(BashCache.DATE)),
                                                  cur.getString(cur.getColumnIndex(BashCache.TEXT)),
                                                  cur.getString(cur.getColumnIndex(BashCache.RATING)),
                                                  cur.getString(cur.getColumnIndex(BashLikes.DIRECTION)),
                                                  cur.getInt(cur.getColumnIndex(BashBayan.IS_BAYAN)) == 1);
            bashOrgEntryList.add(entry);
            cur.moveToNext();
        }
        cur.close();
        return bashOrgEntryList;
    }

    @Override
    protected ContentValues convertToContentValues(ContentEntry entry) {
        ContentValues values = new ContentValues();
        BashOrgEntry bashOrgEntry = (BashOrgEntry) entry;

        values.put(BashCache.ID, bashOrgEntry.getId());
        values.put(BashCache.TEXT, bashOrgEntry.getText());
        values.put(BashCache.DATE, bashOrgEntry.getCreationDate());
        values.put(BashCache.RATING, bashOrgEntry.getRating());
        return values;
    }

}
