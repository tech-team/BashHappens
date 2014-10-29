package org.techteam.bashhappens.db.providers;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.techteam.bashhappens.db.tables.BashBayan;
import org.techteam.bashhappens.db.tables.BashCache;
import org.techteam.bashhappens.db.tables.BashFavs;
import org.techteam.bashhappens.db.tables.BashLikes;
import org.techteam.bashhappens.db.tables.BashTable;

import static org.techteam.bashhappens.db.DatabaseHelper.AUTHORITY;

public class BashHappensDbProvider extends DbProvider {

    private static final int BASH_CACHE = 1;
    private static final int BASH_LIKES = 2;
    private static final int BASH_BAYAN = 3;
    private static final int BASH_FAVS = 4;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public BashHappensDbProvider() {
        super();

        mUriMatcher.addURI(AUTHORITY, BashCache.TABLE_NAME, BASH_CACHE);
        mUriMatcher.addURI(AUTHORITY, BashLikes.TABLE_NAME, BASH_LIKES);
        mUriMatcher.addURI(AUTHORITY, BashBayan.TABLE_NAME, BASH_BAYAN);
        mUriMatcher.addURI(AUTHORITY, BashFavs.TABLE_NAME, BASH_FAVS);

        for (String item : new String[] {BashCache._ID, BashCache.ID, BashCache.TEXT, BashCache.RATING, BashCache.DATE}) {
            mProjectionMap.put(item, item);
        }
        for (String item : new String[] {BashLikes.ARTICLE_ID, BashLikes.DIRECTION}) {
            mProjectionMap.put(item, item);
        }

        mProjectionMap.put(BashBayan.IS_BAYAN, BashBayan.IS_BAYAN);
    }

    @Override
    protected void queryUriMatch(Uri uri, SQLiteQueryBuilder qb, StringWrapper sortOrder) {
        StringBuilder query = new StringBuilder();
        switch(mUriMatcher.match(uri)) {
            case BASH_CACHE:
                query.append(BashCache.TABLE_NAME);
                break;
            case BASH_FAVS:
                query.append(BashFavs.TABLE_NAME);
                break;
            case BASH_LIKES:
                qb.setTables(BashLikes.TABLE_NAME);
                return;
            case BASH_BAYAN:
                qb.setTables(BashBayan.TABLE_NAME);
                return;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        query.append(" LEFT JOIN "
                + BashLikes.TABLE_NAME + " ON "
                + BashTable.ID
                + " = " + BashLikes.TABLE_NAME + "." + BashLikes.ARTICLE_ID
                + " LEFT JOIN " + BashBayan.TABLE_NAME + " ON "
                + BashTable.ID
                + " = " + BashBayan.TABLE_NAME + "." + BashBayan.ARTICLE_ID);
        qb.setTables(query.toString());
        if (sortOrder.data == null) {
            sortOrder.data = BashTable.DEFAULT_SORT_ORDER;
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case BASH_CACHE:
                return BashCache.CONTENT_TYPE;
            case BASH_LIKES:
                return BashLikes.CONTENT_TYPE;
            case BASH_BAYAN:
                return BashBayan.CONTENT_TYPE;
            case BASH_FAVS:
                return BashFavs.CONTENT_TYPE;
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
            case BASH_BAYAN:
                return _insert(db, BashBayan.TABLE_NAME, BashBayan.CONTENT_ID_URI_BASE, contentValues);
            case BASH_FAVS:
                return _insert(db, BashFavs.TABLE_NAME, BashFavs.CONTENT_ID_URI_BASE, contentValues);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    protected synchronized int performDelete(Uri uri, SQLiteDatabase db,
                                             String where, String[] whereArgs) {
        switch (mUriMatcher.match(uri)) {
            case BASH_CACHE:
                return db.delete(BashCache.TABLE_NAME, where, whereArgs);
            case BASH_LIKES:
                return db.delete(BashLikes.TABLE_NAME, where, whereArgs);
            case BASH_BAYAN:
                return db.delete(BashBayan.TABLE_NAME, where, whereArgs);
            case BASH_FAVS:
                return db.delete(BashFavs.TABLE_NAME, where, whereArgs);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        return 0;
    }
}
