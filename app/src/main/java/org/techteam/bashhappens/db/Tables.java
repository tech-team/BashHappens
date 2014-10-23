package org.techteam.bashhappens.db;

import org.techteam.bashhappens.db.tables.*;

public abstract class Tables {
    public static final ITable[] TABLES = new ITable[] {
                                       new BashCache(),
                                       new BashLikes(),
                                       new ItCache(),
                                       new ItLikes()
                                    };
}
