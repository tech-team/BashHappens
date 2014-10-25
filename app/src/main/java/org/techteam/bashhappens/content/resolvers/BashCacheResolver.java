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

        ArrayList<BashOrgEntry> bashOrgEntryList = new ArrayList<BashOrgEntry>();
        while (!cur.isAfterLast()) {
            BashOrgEntry entry = new BashOrgEntry(cur.getString(cur.getColumnIndex("id")),
                                                  cur.getString(cur.getColumnIndex("date")),
                                                  cur.getString(cur.getColumnIndex("text")));
            //TODO: Fill likes (acquired by query)
            bashOrgEntryList.add(entry);
        }
        cur.close();
        return new BashOrgList(bashOrgEntryList);
    }

    @Override
    protected ContentValues convertToContentValues(ContentEntry entry) {
        ContentValues values = new ContentValues();
        values.put("id", ((BashOrgEntry) entry).getId());
        values.put("text", ((BashOrgEntry) entry).getText());
        values.put("date", ((BashOrgEntry) entry).getText());
        return values;
    }

    /*
    public static void rateBashArticle(Activity act, ContentValues values) {
        Uri uri = Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashLikes.TABLE_NAME);
        act.getContentResolver().insert(uri, values);
    }
    */

}
