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
            String newRating = cur.getString(cur.getColumnIndex(AbstractTable.RATING));
            if (!newRating.equals("???") && !newRating.equals("...")) {
                newRating = Integer.toString(Integer.parseInt(newRating)
                                            + entry.toBashOrgEntry().getDirection());
            }
            BashOrgEntry bashOrgEntry = new BashOrgEntry()
                    .setId(cur.getString(cur.getColumnIndex(AbstractTable.ID)))
                    .setCreationDate(cur.getString(cur.getColumnIndex(AbstractTable.DATE)))
                    .setText(cur.getString(cur.getColumnIndex(AbstractTable.TEXT)))
                    .setRating(newRating)
                    .setDirection(entry.toBashOrgEntry().getDirection())
                    .setBayan(cur.getInt(cur.getColumnIndex(BashBayan.IS_BAYAN)) == 1);

            result[1] = resolver.updateEntry(context, bashOrgEntry);
        }
        cur.close();
        return result;
    }
}
