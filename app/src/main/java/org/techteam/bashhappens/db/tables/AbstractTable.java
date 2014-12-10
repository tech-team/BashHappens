package org.techteam.bashhappens.db.tables;

import android.provider.BaseColumns;

import org.techteam.bashhappens.db.ITable;

public abstract class AbstractTable implements BaseColumns, ITable {
    public static final String ID = "id";
    public static final String TEXT = "text";
    public static final String RATING = "rating";
    public static final String DATE = "date";
    public static final String DEFAULT_SORT_ORDER = "id DESC";
}
