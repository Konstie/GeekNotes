package com.gnotes.app.backup;

import android.app.backup.FileBackupHelper;
import android.content.Context;

public class DbBackupHelper extends FileBackupHelper {
    public DbBackupHelper(Context context, String dbName) {
        super(context, context.getDatabasePath(dbName).getAbsolutePath());
    }
}
