package org.techteam.bashhappens.db.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.db.tables.ItCache;
import org.techteam.bashhappens.db.tables.ItLikes;

import static org.techteam.bashhappens.db.DatabaseHelper.AUTHORITY;

public class ItHappensProvider extends DbProvider {

    private static final int IT_CACHE = 1;
    private static final int IT_LIKES = 11;

    public ItHappensProvider() {
        super();

        mUriMatcher.addURI(AUTHORITY, ItCache.TABLE_NAME, IT_CACHE);
        mUriMatcher.addURI(AUTHORITY, ItLikes.TABLE_NAME, IT_LIKES);

        for (String item : new String[] {ItCache._ID, ItCache.ID, ItCache.HEADER,
                                         ItCache.TEXT, ItCache.RATING, ItCache.DATE}) {
            mProjectionMap.put(item, ItCache.TABLE_NAME + "." + item);
        }

        for (String item : new String[] {ItLikes._ID, ItLikes.ARTICLE_ID, ItLikes.IS_LIKED}) {
            mProjectionMap.put(item, ItLikes.TABLE_NAME + "." + item);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String s, String[] strings) {
        return 0;
    }
}
