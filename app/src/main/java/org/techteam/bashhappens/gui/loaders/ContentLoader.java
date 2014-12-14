package org.techteam.bashhappens.gui.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.db.resolvers.AbstractContentResolver;

public class ContentLoader extends CursorLoader {

    private final ContentSection section;
    private Integer entryPosition;
    private Integer insertedCount;
    private int loadIntention;

    public Integer getInsertedCount() {
        return insertedCount;
    }

    public abstract class BundleKeys {
        public static final String ENTRY_POSITION = "ENTRY_POSITION";
        public static final String INSERTED_COUNT = "INSERTED_COUNT";
        public static final String LOAD_INTENTION = "LOAD_INTENTION";
    }

    public ContentLoader(Context context, ContentSection section, Integer entryPosition, Integer insertedCount, int loadIntention) {
        super(context);
        this.section = section;
        this.entryPosition = entryPosition;
        this.insertedCount = insertedCount;
        this.loadIntention = loadIntention;
    }

    @Override
    public Cursor loadInBackground() {
        AbstractContentResolver resolver = AbstractContentResolver.getResolver(section);
        return resolver.getCursor(getContext());
    }

    public Integer getEntryPosition() {
        return entryPosition;
    }

    public int getLoadIntention() {
        return loadIntention;
    }
}
