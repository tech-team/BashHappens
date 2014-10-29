package org.techteam.bashhappens.content.resolvers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashFavs;
import org.techteam.bashhappens.db.tables.BashLikes;


public class BashLikesResolver extends AbstractContentResolver {
    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashLikes.TABLE_NAME);
    }

    @Override
    protected String[] getProjection() {
        return new String[]{BashLikes.ARTICLE_ID, BashLikes.DIRECTION};
    }

    @Override
    protected ContentList<?> getEntriesList(Cursor cur) {
        cur.moveToFirst();

        BashOrgList bashOrgEntryList = new BashOrgList();
        while (!cur.isAfterLast()) {
            BashOrgEntry entry = new BashOrgEntry()
                    .setId(cur.getString(cur.getColumnIndex(BashLikes.ARTICLE_ID)))
                    .setDirection(cur.getInt(cur.getColumnIndex(BashLikes.DIRECTION)));

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

        values.put(BashLikes.ARTICLE_ID, bashOrgEntry.getId());
        values.put(BashLikes.DIRECTION, bashOrgEntry.getDirection());
        return values;
    }

    @Override
    protected QueryField getDeletionField(ContentEntry contentEntry) {
        return new QueryField(BashLikes.ARTICLE_ID, new String[]{((BashOrgEntry) contentEntry).getId()});
    }

}
