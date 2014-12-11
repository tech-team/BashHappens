package org.techteam.bashhappens.content.resolvers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.AbstractTable;
import org.techteam.bashhappens.db.tables.BashBayan;
import org.techteam.bashhappens.db.tables.BashLikes;

public class BashBayanResolver extends BashResolver {
    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashBayan.TABLE_NAME);
    }

    @Override
    protected String[] getProjection() {
        return new String[] {BashBayan.ARTICLE_ID, BashBayan.IS_BAYAN};
    }

    public static BashOrgEntry getCurrentEntry(Cursor cur) {
        return new BashOrgEntry()
                .setId(cur.getString(cur.getColumnIndex(BashBayan.ARTICLE_ID)))
                .setBayan(cur.getInt(cur.getColumnIndex(BashBayan.IS_BAYAN)) == 1);
    }

    @Override
    protected ContentValues convertToContentValues(ContentEntry contentEntry) {
        ContentValues values = new ContentValues();
        BashOrgEntry bashOrgEntry = (BashOrgEntry) contentEntry;

        values.put(BashBayan.ARTICLE_ID, bashOrgEntry.getId());
        values.put(BashBayan.IS_BAYAN, bashOrgEntry.getIsBayan());
        return values;
    }

    public int[] insert(Context context, ContentEntry entry, ContentSection section) {
        int[] result = {0, 0};
        result[0] = Integer.valueOf(context
                .getContentResolver()
                .insert(_getUri(), convertToContentValues(entry))
                .getLastPathSegment());
        String[] selectionArgs = new String[] { entry.toBashOrgEntry().getId() };

        AbstractContentResolver resolver = AbstractContentResolver.getResolver(section);

        Cursor cur = resolver.getCursor(context, null, AbstractTable.ID + "= ?",
                selectionArgs, null);
        cur.moveToFirst();
        if (cur.getCount() != 0) {
            BashOrgEntry bashOrgEntry = new BashOrgEntry()
                    .setId(cur.getString(cur.getColumnIndex(AbstractTable.ID)))
                    .setCreationDate(cur.getString(cur.getColumnIndex(AbstractTable.DATE)))
                    .setText(cur.getString(cur.getColumnIndex(AbstractTable.TEXT)))
                    .setRating(cur.getString(cur.getColumnIndex(AbstractTable.RATING)))
                    .setDirection(cur.getInt(cur.getColumnIndex(BashLikes.DIRECTION)))
                    .setBayan(entry.toBashOrgEntry().getIsBayan());

            result[1] = resolver.updateEntry(context, bashOrgEntry);
        }
        cur.close();
        return result;
    }
}
