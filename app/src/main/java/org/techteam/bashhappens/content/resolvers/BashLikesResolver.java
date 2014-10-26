package org.techteam.bashhappens.content.resolvers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashLikes;

import java.util.List;

public class BashLikesResolver extends AbstractContentResolver {
    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashLikes.TABLE_NAME);
    }

    @Override
    protected ContentList<?> getEntries(Cursor cur) {
        return null;
    }

    @Override
    protected ContentValues convertToContentValues(ContentEntry entry) {
        ContentValues values = new ContentValues();
        BashOrgEntry bashOrgEntry = (BashOrgEntry) entry;

        values.put("article_id", bashOrgEntry.getId());
        values.put("direction", bashOrgEntry.getDirection());
        values.put("isBayan", bashOrgEntry.getIsBayan());
        return values;
    }
}
