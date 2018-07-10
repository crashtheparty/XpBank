package org.ctp.xpbank.database;

import java.util.logging.Level;

import org.ctp.xpbank.XpBank;

public class Error {
	public static void execute(XpBank plugin, Exception ex) {
		plugin.getLogger().log(Level.SEVERE,
				"Couldn't execute MySQL statement: ", ex);
	}

	public static void close(XpBank plugin, Exception ex) {
		plugin.getLogger().log(Level.SEVERE,
				"Failed to close MySQL connection: ", ex);
	}
}