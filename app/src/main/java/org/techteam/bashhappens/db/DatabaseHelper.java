package org.techteam.bashhappens.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.techteam.bashhappens.db.tables.BashCache;
import org.techteam.bashhappens.db.tables.BashLikes;
import org.techteam.bashhappens.db.tables.ItCache;
import org.techteam.bashhappens.db.tables.ItLikes;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bashhappens.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        BashCache.onCreate(sqLiteDatabase);
        BashLikes.onCreate(sqLiteDatabase);
        ItCache.onCreate(sqLiteDatabase);
        ItLikes.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        BashCache.onUpgrade(sqLiteDatabase, i, i2);
        BashLikes.onUpgrade(sqLiteDatabase, i, i2);
        ItCache.onUpgrade(sqLiteDatabase, i, i2);
        ItLikes.onUpgrade(sqLiteDatabase, i, i2);
    }
}
