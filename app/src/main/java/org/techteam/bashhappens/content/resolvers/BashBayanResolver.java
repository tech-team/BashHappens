package org.techteam.bashhappens.content.resolvers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashBayan;

public class BashBayanResolver extends AbstractContentResolver {
    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashBayan.TABLE_NAME);
    }

    @Override
    protected ContentList<?> getEntriesList(Cursor cur) {
        return null;
    }

    @Override
    protected ContentValues convertToContentValues(ContentEntry contentEntry) {
        ContentValues values = new ContentValues();
        BashOrgEntry bashOrgEntry = (BashOrgEntry) contentEntry;

        values.put("article_id", bashOrgEntry.getId());
        values.put("isBayan", bashOrgEntry.getIsBayan());
        return values;
    }

    @Override
    protected QueryField getDeletionField(ContentEntry contentEntry) {
        return new QueryField(BashBayan.ARTICLE_ID,
                              new String[]{((BashOrgEntry) contentEntry).getId()});
    }

}
