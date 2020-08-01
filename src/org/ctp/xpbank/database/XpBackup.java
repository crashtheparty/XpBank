package org.ctp.xpbank.database;

import org.ctp.crashapi.CrashAPIPlugin;
import org.ctp.crashapi.db.BackupDB;

public class XpBackup extends BackupDB {

	public XpBackup(CrashAPIPlugin instance) {
		super(instance, "backups");
	}

}
