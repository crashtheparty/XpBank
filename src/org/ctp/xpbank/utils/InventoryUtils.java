package org.ctp.xpbank.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.ctp.xpbank.inventories.ExperienceInventory;

public class InventoryUtils {
	
	private static Map<String, ExperienceInventory> INVENTORIES = new HashMap<String, ExperienceInventory>();

	public static ExperienceInventory getInventory(OfflinePlayer player) {
		String id = player.getUniqueId().toString();
		if(INVENTORIES.containsKey(id)) {
			return INVENTORIES.get(id);
		}
		return null;
	}
	
	public static void addInventory(OfflinePlayer player) {
		ExperienceInventory inv = new ExperienceInventory(player);
		String id = player.getUniqueId().toString();
		INVENTORIES.put(id, inv);
		inv.openInventory();
	}
	
	public static void removeInventory(OfflinePlayer player) {
		INVENTORIES.remove(player.getUniqueId().toString());
	}
	
	public static void addInventory(OfflinePlayer admin, OfflinePlayer player) {
		ExperienceInventory inv = new ExperienceInventory(player, admin);
		String id = admin.getUniqueId().toString();
		INVENTORIES.put(id, inv);
		inv.openInventory();
	}
}
