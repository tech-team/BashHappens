package org.techteam.bashhappens.rest.processors;

import android.content.Context;

public abstract class Processor {

    private final Context context;

    public Processor(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public abstract void start(ProcessorCallback cb);
}
