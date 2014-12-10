package org.techteam.bashhappens.gui.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.resolvers.AbstractContentResolver;

public class ContentLoader extends CursorLoader {

    private final ContentSection section;

    public ContentLoader(Context context, ContentSection section) {
        super(context);
        this.section = section;
    }

    @Override
    public Cursor loadInBackground() {
        AbstractContentResolver resolver = AbstractContentResolver.getResolver(section);
        return resolver.getCursor(getContext());
    }
}
