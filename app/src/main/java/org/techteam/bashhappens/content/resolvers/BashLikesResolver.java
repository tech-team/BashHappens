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
import org.techteam.bashhappens.db.tables.BashNewest;

public class BashLikesResolver extends BashResolver {
    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashLikes.TABLE_NAME);
    }

    @Override
    protected String[] getProjection() {
        return new String[] {BashLikes.ARTICLE_ID, BashLikes.DIRECTION};
    }

    @Override
    protected QueryField getQueryField(ContentEntry contentEntry) {
        return new QueryField(BashLikes.ARTICLE_ID, new String[]{((BashOrgEntry) contentEntry).getId()});
    }
    @Override
    protected QueryField getUpdateField(ContentEntry contentEntry) {
        return new QueryField(BashLikes.ARTICLE_ID, new String[]{((BashOrgEntry) contentEntry).getId()});
    }
    @Override
    protected QueryField getDeletionField(ContentEntry contentEntry) {
        return new QueryField(BashLikes.ARTICLE_ID, new String[]{((BashOrgEntry) contentEntry).getId()});
    }

    public static BashOrgEntry getCurrentEntry(Cursor cur) {
        return new BashOrgEntry()
                .setId(cur.getString(cur.getColumnIndex(BashLikes.ARTICLE_ID)))
                .setDirection(cur.getInt(cur.getColumnIndex(BashLikes.DIRECTION)));
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
    public int insert(Context context, ContentEntry entry) {

        int result = 0;
        Cursor likesCur = getCursor(context, null, BashLikes.ARTICLE_ID + "= ?",
                new String[]{entry.toBashOrgEntry().getId()}, null);

        if (likesCur.getCount() == 0) {
            result = Integer.valueOf(context
                    .getContentResolver()
                    .insert(_getUri(), convertToContentValues(entry))
                    .getLastPathSegment());
            String[] selectionArgs = new String[]{entry.toBashOrgEntry().getId()};

            // TODO: iterate through all tables
            ContentSection section = ContentSection.BASH_ORG_NEWEST;
            AbstractContentResolver resolver = AbstractContentResolver.getResolver(section);

            Cursor cur = resolver.getCursor(context, null, AbstractTable.ID + "= ?",
                    selectionArgs, null);
            cur.moveToFirst();
            if (cur.getCount() != 0) {
                BashOrgEntry bashOrgEntry = new BashOrgEntry()
                        .setId(cur.getString(cur.getColumnIndex(AbstractTable.ID)))
                        .setCreationDate(cur.getString(cur.getColumnIndex(AbstractTable.DATE)))
                        .setText(cur.getString(cur.getColumnIndex(AbstractTable.TEXT)))
                        .setRating(entry.toBashOrgEntry().getRating())
                        .setDirection(entry.toBashOrgEntry().getDirection())
                        .setBayan(cur.getInt(cur.getColumnIndex(BashBayan.IS_BAYAN)) == 1);

                resolver.updateEntry(context, bashOrgEntry);
            }
            cur.close();
        }
        likesCur.close();
        return result;
    }
}
