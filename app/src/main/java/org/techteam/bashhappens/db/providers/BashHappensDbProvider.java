package org.techteam.bashhappens.db.providers;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.techteam.bashhappens.db.tables.BashCache;
import org.techteam.bashhappens.db.tables.BashLikes;

import static org.techteam.bashhappens.db.DatabaseHelper.AUTHORITY;

public class BashHappensDbProvider extends DbProvider {

    private static final int BASH_CACHE = 1;
    private static final int BASH_LIKES = 11;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public BashHappensDbProvider() {
        super();

        mUriMatcher.addURI(AUTHORITY, BashCache.TABLE_NAME, BASH_CACHE);
        mUriMatcher.addURI(AUTHORITY, BashLikes.TABLE_NAME, BASH_LIKES);

        for (String item : new String[] {BashCache._ID, BashCache.ID, BashCache.TEXT, BashCache.RATING, BashCache.DATE}) {
            mProjectionMap.put(item, BashCache.TABLE_NAME + "." + item);
        }

        for (String item : new String[] {BashLikes._ID, BashLikes.ARTICLE_ID, BashLikes.DIRECTION, BashLikes.IS_BAYAN}) {
            mProjectionMap.put(item, BashLikes.TABLE_NAME + "." + item);
        }

    }

    @Override
    protected void queryUriMatch(Uri uri, SQLiteQueryBuilder qb, StringWrapper sortOrder) {
        if (mUriMatcher.match(uri) == BASH_CACHE) {
            qb.setTables(BashCache.TABLE_NAME + " LEFT JOIN "
                    + BashLikes.TABLE_NAME + " ON "
                    + BashCache.ID + " = " + BashLikes.ARTICLE_ID);
            if (TextUtils.isEmpty(sortOrder.data)) {
                sortOrder.data = BashCache.DEFAULT_SORT_ORDER;
            }
        }
        else {
            throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case BASH_CACHE:
                return BashCache.CONTENT_TYPE;
            case BASH_LIKES:
                return BashLikes.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    protected Uri performInsert(Uri uri, SQLiteDatabase db, ContentValues contentValues) {
        switch (mUriMatcher.match(uri)) {
            case BASH_CACHE:
                return _insert(db, BashCache.TABLE_NAME, BashCache.CONTENT_ID_URI_BASE, contentValues);
            case BASH_LIKES:
                return _insert(db, BashLikes.TABLE_NAME, BashLikes.CONTENT_ID_URI_BASE, contentValues);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    protected synchronized int performDelete(Uri uri, SQLiteDatabase db) {
        switch (mUriMatcher.match(uri)) {
            case BASH_CACHE:
                return db.delete(BashCache.TABLE_NAME, null, null);
            case BASH_LIKES:
                return db.delete(BashLikes.TABLE_NAME, null, null);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        return 0;
    }
}