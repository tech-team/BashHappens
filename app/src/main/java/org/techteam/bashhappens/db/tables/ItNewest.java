package org.techteam.bashhappens.db.tables;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.techteam.bashhappens.db.DatabaseHelper;
import org.techteam.bashhappens.db.ITable;

import static org.techteam.bashhappens.db.DataTypes.*;

public class ItNewest extends AbstractTable {
    public static final String TABLE_NAME = "it_newest";
    public static final String HEADER = "header";

    public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse("content://" + DatabaseHelper.AUTHORITY + "/it_newest/");
    public static final String CONTENT_TYPE
            = "org.techteam.bashhappens.db.tables.itnewest/org.techteam.bashhappens.db";

    public ItNewest() {}

    private static final String TABLE_CREATE = "CREATE TABLE "
            + ItNewest.TABLE_NAME + "(" + COLUMN_ID + TYPE_SERIAL + SEPARATOR
            + ItNewest.ID + TYPE_INTEGER + SEPARATOR
            + ItNewest.HEADER + TYPE_TEXT + SEPARATOR
            + ItNewest.TEXT + TYPE_TEXT + SEPARATOR
            + ItNewest.RATING + TYPE_INTEGER + SEPARATOR
            + ItNewest.DATE + TYPE_TEXT + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ItNewest.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
