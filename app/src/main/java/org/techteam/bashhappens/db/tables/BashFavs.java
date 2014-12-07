package org.techteam.bashhappens.db.tables;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.techteam.bashhappens.db.DatabaseHelper;
import org.techteam.bashhappens.db.ITable;

import static org.techteam.bashhappens.db.DataTypes.COLUMN_ID;
import static org.techteam.bashhappens.db.DataTypes.SEPARATOR;
import static org.techteam.bashhappens.db.DataTypes.TYPE_SERIAL;
import static org.techteam.bashhappens.db.DataTypes.TYPE_TEXT;

public class BashFavs extends AbstractTable implements ITable {

    public static final String TABLE_NAME = "bash_favs";

    public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse("content://" + DatabaseHelper.AUTHORITY + "/bash_favs/");
    public static final String CONTENT_TYPE
            = "org.techteam.bashhappens.db.tables.bashfavs/org.techteam.bashhappens.db";

    public BashFavs() {}

    private static final String TABLE_CREATE = "CREATE TABLE "
            + TABLE_NAME + "(" + COLUMN_ID + TYPE_SERIAL + SEPARATOR
            + ID + TYPE_TEXT + SEPARATOR
            + TEXT + TYPE_TEXT + SEPARATOR
            + RATING + TYPE_TEXT + SEPARATOR
            + DATE + TYPE_TEXT + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.w(BashFavs.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}

