package org.ctp.xpbank.database;

import org.ctp.crashapi.CrashAPIPlugin;
import org.ctp.crashapi.db.SQLite;
import org.ctp.xpbank.database.tables.XpTable;

public class XpDatabase extends SQLite {

	public XpDatabase(CrashAPIPlugin instance) {
		super(instance, "xpbank");
		addTable(new XpTable(this));
	}

	@Override
	public <T> T getTable(Class<T> cls) {
		return super.getTable(cls);
	}

}
