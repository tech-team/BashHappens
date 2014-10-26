package org.techteam.bashhappens.content.resolvers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashCache;

import java.util.ArrayList;
import java.util.List;

public class BashCacheResolver extends AbstractContentResolver {

    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashCache.TABLE_NAME);
    }
    @Override
    protected BashOrgList getEntries(Cursor cur) {
        cur.moveToFirst();

        BashOrgList bashOrgEntryList = new BashOrgList();
        while (!cur.isAfterLast()) {
            BashOrgEntry entry = new BashOrgEntry(cur.getString(cur.getColumnIndex("id")),
                                                  cur.getString(cur.getColumnIndex("date")),
                                                  cur.getString(cur.getColumnIndex("text")),
                                                  cur.getString(cur.getColumnIndex("rating")));
            //TODO: Fill likes (acquired by query)
            bashOrgEntryList.add(entry);
        }
        cur.close();
        return bashOrgEntryList;
    }

    @Override
    protected ContentValues convertToContentValues(ContentEntry entry) {
        ContentValues values = new ContentValues();
        BashOrgEntry bashOrgEntry = (BashOrgEntry) entry;

        values.put("id", bashOrgEntry.getId());
        values.put("text", bashOrgEntry.getText());
        values.put("date", bashOrgEntry.getCreationDate());
        values.put("rating", bashOrgEntry.getRating());
        return values;
    }

    /*
    public static void rateBashArticle(Activity act, ContentValues values) {
        Uri uri = Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashLikes.TABLE_NAME);
        act.getContentResolver().insert(uri, values);
    }
    */

}
