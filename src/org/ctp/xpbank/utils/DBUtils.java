package org.ctp.xpbank.utils;

import org.bukkit.OfflinePlayer;
import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.database.tables.XpTable;

public class DBUtils {

	public static int getExperience(OfflinePlayer player) {
		String uuid = player.getUniqueId().toString();
		XpTable table = XpBank.getDB().getTable(XpTable.class);
		return table.getExp(uuid);
	}

	public static void setExperience(OfflinePlayer player, int xp) {
		String uuid = player.getUniqueId().toString();
		XpTable table = XpBank.getDB().getTable(XpTable.class);
		table.setExp(uuid, xp);
	}

}
