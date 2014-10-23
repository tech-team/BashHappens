package org.techteam.bashhappens.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.techteam.bashhappens.db.tables.BashCache;
import org.techteam.bashhappens.db.tables.BashLikes;
import org.techteam.bashhappens.db.tables.ItCache;
import org.techteam.bashhappens.db.tables.ItLikes;

import java.util.HashMap;

import static org.techteam.bashhappens.db.DatabaseHelper.AUTHORITY;

public class BashHappensDbProvider extends ContentProvider {

    private DatabaseHelper database;
    private final UriMatcher mUriMatcher;
    private final HashMap<String, String> mProjectionMap;

    private static final int BASH_CACHE = 1;
    private static final int BASH_CACHE_ID = 2;
    private static final int BASH_LIKES = 11;
    private static final int BASH_LIKES_ID = 12;
    private static final int BASH_LIKES_ARTICLE_ID = 13;
    private static final int IT_CACHE = 21;
    private static final int IT_CACHE_ID = 22;
    private static final int IT_LIKES = 31;
    private static final int IT_LIKES_ID = 32;
    private static final int IT_LIKES_ARTICLE_ID = 33;

    public BashHappensDbProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, BashCache.TABLE_NAME, BASH_CACHE);
        mUriMatcher.addURI(AUTHORITY, BashLikes.TABLE_NAME, BASH_LIKES);
        mUriMatcher.addURI(AUTHORITY, ItCache.TABLE_NAME, IT_CACHE);
        mUriMatcher.addURI(AUTHORITY, ItLikes.TABLE_NAME, IT_LIKES);

        mProjectionMap = new HashMap<String, String>();

        for (String item : new String[] {BashCache._ID, BashCache.ID, BashCache.TEXT, BashCache.RATING, BashCache.DATE}) {
            mProjectionMap.put(item, BashCache.TABLE_NAME + "." + item);
        }

        for (String item : new String[] {BashLikes._ID, BashLikes.ARTICLE_ID, BashLikes.DIRECTION, BashLikes.IS_BOYAN }) {
            mProjectionMap.put(item, BashLikes.TABLE_NAME + "." + item);
        }

        for (String item : new String[] {ItCache._ID, ItCache.ID, ItCache.HEADER, BashCache.TEXT, BashCache.RATING, BashCache.DATE}) {
            mProjectionMap.put(item, ItCache.TABLE_NAME + "." + item);
        }

        for (String item : new String[] {ItLikes._ID, ItLikes.ARTICLE_ID, ItLikes.IS_LIKED}) {
            mProjectionMap.put(item, ItLikes.TABLE_NAME + "." + item);
        }
    }

    @Override
    public boolean onCreate() {
        database = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection,
                                     String selection, String[] selectionArgs,
                                     String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setProjectionMap(mProjectionMap);

        switch (mUriMatcher.match(uri)) {
            case BASH_CACHE:
                qb.setTables(BashCache.TABLE_NAME + " LEFT JOIN "
                        + BashLikes.TABLE_NAME + " ON "
                        + BashCache.ID + " = " + BashLikes.ARTICLE_ID);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = BashCache.DEFAULT_SORT_ORDER;
                }
                break;
            case IT_CACHE:
                qb.setTables(ItCache.TABLE_NAME + " LEFT JOIN "
                             + ItLikes.TABLE_NAME + " ON "
                             + ItCache.ID + " = " + ItLikes.ARTICLE_ID);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ItCache.DEFAULT_SORT_ORDER;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }

        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cur = qb.query(db, projection, selection, selectionArgs,
                              null, null, sortOrder);

        cur.setNotificationUri(getContext().getContentResolver(), uri);
        return cur;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    private Uri _insert(SQLiteDatabase db, String tableName, Uri uriBase, ContentValues values) {
        long rowId = db.insert(tableName, null, values);

        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uriBase, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        return null;
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues rawValues) {
        if (mUriMatcher.match(uri) != BASH_CACHE &&
            mUriMatcher.match(uri) != IT_CACHE) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues contentValues;

        if (rawValues != null) {
            contentValues = new ContentValues(rawValues);
        } else {
            contentValues = new ContentValues();
        }

        SQLiteDatabase db = database.getWritableDatabase();

        Uri noteUri;

        switch (mUriMatcher.match(uri)) {
            case BASH_CACHE:
                noteUri = _insert(db, BashCache.TABLE_NAME, BashCache.CONTENT_ID_URI_BASE, contentValues);
                break;
            case BASH_LIKES:
                noteUri = _insert(db, BashLikes.TABLE_NAME, BashLikes.CONTENT_ID_URI_BASE, contentValues);
                break;
            case IT_CACHE:
                noteUri = _insert(db, ItCache.TABLE_NAME, ItCache.CONTENT_ID_URI_BASE, contentValues);
                break;
            case IT_LIKES:
                noteUri = _insert(db, ItLikes.TABLE_NAME, ItLikes.CONTENT_ID_URI_BASE, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }

        return noteUri;
    }

    @Override
    public synchronized int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int count;

        switch (mUriMatcher.match(uri)) {
            case BASH_CACHE:
                count = db.delete(BashCache.TABLE_NAME, null, null);
                break;
            case BASH_LIKES:
                count = db.delete(BashLikes.TABLE_NAME, null, null);
                break;
            case IT_CACHE:
                count = db.delete(ItCache.TABLE_NAME, null, null);
                break;
            case IT_LIKES:
                count = db.delete(ItLikes.TABLE_NAME, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        return 0;
    }
}
