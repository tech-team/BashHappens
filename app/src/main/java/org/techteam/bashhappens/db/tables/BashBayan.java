package org.techteam.bashhappens.db.tables;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.techteam.bashhappens.db.DatabaseHelper;
import org.techteam.bashhappens.db.ITable;

import static org.techteam.bashhappens.db.DataTypes.COLUMN_ID;
import static org.techteam.bashhappens.db.DataTypes.SEPARATOR;
import static org.techteam.bashhappens.db.DataTypes.TYPE_BOOLEAN;
import static org.techteam.bashhappens.db.DataTypes.TYPE_SERIAL;
import static org.techteam.bashhappens.db.DataTypes.TYPE_TEXT;

public class BashBayan implements BaseColumns, ITable {
    public static final String TABLE_NAME = "bash_bayan";
    public static final String ARTICLE_ID = "article_id";
    public static final String IS_BAYAN = "is_bayan";

    public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse("content://" + DatabaseHelper.AUTHORITY + "/bash_bayan/");
    public static final String CONTENT_TYPE
            = "org.techteam.bashhappens.db.tables.bashbayan/org.techteam.bashhappens.db";

    public BashBayan() {}

    private static final String TABLE_CREATE = "CREATE TABLE "
            + TABLE_NAME + "(" + COLUMN_ID + TYPE_SERIAL + SEPARATOR
            + ARTICLE_ID + TYPE_TEXT + SEPARATOR
            + IS_BAYAN + TYPE_BOOLEAN + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.w(BashBayan.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
