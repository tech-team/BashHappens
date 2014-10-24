package org.techteam.bashhappens.db.tables;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.techteam.bashhappens.db.DatabaseHelper;
import org.techteam.bashhappens.db.ITable;

import static org.techteam.bashhappens.db.DataTypes.*;

public class ItCache implements BaseColumns, ITable {
    public static final String TABLE_NAME = "it_cache";
    public static final String ID = "id";
    public static final String HEADER = "header";
    public static final String TEXT = "text";
    public static final String RATING = "rating";
    public static final String DATE = "date";
    public static final String DEFAULT_SORT_ORDER = "id DESC";

    public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse("content://" + DatabaseHelper.AUTHORITY + "/it_cache/");
    public static final String CONTENT_TYPE
            = "org.techteam.bashhappens.db.tables.itcache/org.techteam.bashhappens.db";

    public ItCache() {}

    private static final String TABLE_CREATE = "CREATE TABLE "
            + ItCache.TABLE_NAME + "(" + COLUMN_ID + TYPE_SERIAL + SEPARATOR
            + ItCache.ID + TYPE_INTEGER + SEPARATOR
            + ItCache.HEADER + TYPE_TEXT + SEPARATOR
            + ItCache.TEXT + TYPE_TEXT + SEPARATOR
            + ItCache.RATING + TYPE_INTEGER + SEPARATOR
            + ItCache.DATE + TYPE_TIMESTAMP + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ItCache.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
