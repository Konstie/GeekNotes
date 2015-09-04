package com.gnotes.app.sync;

import android.app.backup.FileBackupHelper;
import android.content.Context;

public class DbBackupHelper extends FileBackupHelper {
    /**
     * Construct a helper to manage backup/restore of entire files within the
     * application's data directory hierarchy.
     *
     * @param context The backup agent's Context object
     * @param files   A list of the files to be backed up or restored.
     */
    public DbBackupHelper(Context context, String dbName) {
        super(context, context.getDatabasePath(dbName).getAbsolutePath());
    }
}
