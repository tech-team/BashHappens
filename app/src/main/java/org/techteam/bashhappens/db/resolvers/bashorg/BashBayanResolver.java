package org.techteam.bashhappens.db.resolvers.bashorg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.Entry;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.resolvers.AbstractContentResolver;
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
    protected QueryField getQueryField(Entry entry) {
        return new QueryField(BashBayan.ARTICLE_ID, new String[]{((BashOrgEntry) entry).getId()});
    }
    @Override
    protected QueryField getUpdateField(Entry entry) {
        return new QueryField(BashBayan.ARTICLE_ID, new String[]{((BashOrgEntry) entry).getId()});
    }
    @Override
    protected QueryField getDeletionField(Entry entry) {
        return new QueryField(BashBayan.ARTICLE_ID, new String[]{((BashOrgEntry) entry).getId()});
    }

    @Override
    protected ContentValues convertToContentValues(Entry entry) {
        ContentValues values = new ContentValues();
        BashOrgEntry bashOrgEntry = (BashOrgEntry) entry;

        values.put(BashBayan.ARTICLE_ID, bashOrgEntry.getId());
        values.put(BashBayan.IS_BAYAN, bashOrgEntry.getIsBayan());
        return values;
    }

    @Override
    public int insert(Context context, Entry entry) {
        BashOrgEntry borgEntry = (BashOrgEntry) entry;
        int result = 0;
        Cursor bayanCur = getCursor(context, null, BashBayan.ARTICLE_ID + "= ?",
                new String[]{borgEntry.getId()}, null);

        if (bayanCur.getCount() == 0) {

            result = Integer.valueOf(context
                    .getContentResolver()
                    .insert(_getUri(), convertToContentValues(entry))
                    .getLastPathSegment());
            String[] selectionArgs = new String[]{borgEntry.getId()};

            // TODO: iterate through all tables
            ContentSection section = ContentSection.BASH_ORG_NEWEST;
            AbstractContentResolver resolver = getResolver(section);

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
                        .setBayan(borgEntry.getIsBayan());

                resolver.updateEntry(context, bashOrgEntry);
            }
            cur.close();
        }
        bayanCur.close();
        return result;
    }
}
