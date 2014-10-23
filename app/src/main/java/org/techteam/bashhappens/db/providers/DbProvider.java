package org.techteam.bashhappens.db.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.db.DatabaseHelper;

import java.util.HashMap;

public abstract class DbProvider extends ContentProvider {

    protected DatabaseHelper database;
    protected UriMatcher mUriMatcher;
    protected HashMap<String, String> mProjectionMap;

    protected DbProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mProjectionMap = new HashMap<String, String>();
    }

    @Override
    public boolean onCreate() {
        database = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public abstract Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2);

    @Override
    public abstract String getType(Uri uri);

    @Override
    public abstract Uri insert(Uri uri, ContentValues values);

    @Override
    public abstract int delete(Uri uri, String s, String[] strings);

    @Override
    public abstract int update(Uri uri, ContentValues values, String s, String[] strings);
}
