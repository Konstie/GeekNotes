package com.gnotes.app.sync;

import android.app.backup.BackupAgentHelper;

public class DbBackupAgent extends BackupAgentHelper {
    static final String GEEKNOTES_DATABASE = "geeknotes.db";

    static final String FILES_BACKUP_KEY = "geeknotes_database";

    @Override
    public void onCreate() {
        addHelper(FILES_BACKUP_KEY, new DbBackupHelper(this, GEEKNOTES_DATABASE));
    }
}
