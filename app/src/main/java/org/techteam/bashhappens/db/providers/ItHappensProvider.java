package org.techteam.bashhappens.db.providers;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.techteam.bashhappens.db.tables.ItNewest;
import org.techteam.bashhappens.db.tables.ItLikes;

import static org.techteam.bashhappens.db.DatabaseHelper.AUTHORITY;

public class ItHappensProvider extends DbProvider {

    private static final int IT_CACHE = 1;
    private static final int IT_LIKES = 11;

    public ItHappensProvider() {
        super();

        mUriMatcher.addURI(AUTHORITY, ItNewest.TABLE_NAME, IT_CACHE);
        mUriMatcher.addURI(AUTHORITY, ItLikes.TABLE_NAME, IT_LIKES);

        for (String item : new String[] {ItNewest._ID, ItNewest.ID, ItNewest.HEADER,
                                         ItNewest.TEXT, ItNewest.RATING, ItNewest.DATE}) {
            mProjectionMap.put(item, ItNewest.TABLE_NAME + "." + item);
        }

        for (String item : new String[] {ItLikes._ID, ItLikes.ARTICLE_ID, ItLikes.IS_LIKED}) {
            mProjectionMap.put(item, ItLikes.TABLE_NAME + "." + item);
        }
    }

    @Override
    public void queryUriMatch(Uri uri, SQLiteQueryBuilder qb, StringWrapper sortOrder) {
        if (mUriMatcher.match(uri) == IT_CACHE) {
            qb.setTables(ItNewest.TABLE_NAME + " LEFT JOIN "
                    + ItLikes.TABLE_NAME + " ON "
                    + ItNewest.ID + " = " + ItLikes.ARTICLE_ID);
            if (TextUtils.isEmpty(sortOrder.data)) {
                sortOrder.data = ItNewest.DEFAULT_SORT_ORDER;
            }
        }
        else {
            throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case IT_CACHE:
                return ItNewest.CONTENT_TYPE;
            case IT_LIKES:
                return ItLikes.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    protected Uri performInsert(Uri uri, SQLiteDatabase db, ContentValues contentValues) {
        switch (mUriMatcher.match(uri)) {
            case IT_CACHE:
                return _insert(db, ItNewest.TABLE_NAME, ItNewest.CONTENT_ID_URI_BASE, contentValues);
            case IT_LIKES:
                return _insert(db, ItLikes.TABLE_NAME, ItLikes.CONTENT_ID_URI_BASE, contentValues);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }


    @Override
    protected synchronized int performDelete(Uri uri, SQLiteDatabase db,
                                             String where, String[] whereArgs) {
        switch (mUriMatcher.match(uri)) {
            case IT_CACHE:
                return db.delete(ItNewest.TABLE_NAME, where, whereArgs);
            case IT_LIKES:
                return db.delete(ItLikes.TABLE_NAME, where, whereArgs);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String s, String[] strings) {
        return 0;
    }
}
