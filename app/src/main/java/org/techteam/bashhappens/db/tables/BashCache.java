package org.techteam.bashhappens.db.tables;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.techteam.bashhappens.db.DatabaseHelper;
import org.techteam.bashhappens.db.ITable;

import static org.techteam.bashhappens.db.DataTypes.*;

public class BashCache implements BaseColumns, ITable {

    public static final String TABLE_NAME = "bash_cache";
    public static final String ID = "id";
    public static final String TEXT = "text";
    public static final String RATING = "rating";
    public static final String DATE = "date";
    public static final String DEFAULT_SORT_ORDER = "id DESC";

    public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse("content://" + DatabaseHelper.AUTHORITY + "/bash_cache/");
    public static final String CONTENT_TYPE
            = "org.techteam.bashhappens.db.tables.bashcache/org.techteam.bashhappens.db";

    public BashCache() {}

    private static final String TABLE_CREATE = "CREATE TABLE "
            + BashCache.TABLE_NAME + "(" + COLUMN_ID + SERIAL
            + BashCache.ID + INTEGER + SEPARATOR
            + BashCache.TEXT + TEXT + SEPARATOR
            + BashCache.RATING + INTEGER + SEPARATOR
            + BashCache.DATE + TIMESTAMP + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(BashCache.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
