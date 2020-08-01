package org.ctp.xpbank.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.inventories.ExperienceInventory;

public class InventoryClose implements Listener {

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = null;
		if (event.getPlayer() instanceof Player) player = (Player) event.getPlayer();
		else
			return;
		InventoryData expInv = XpBank.getPlugin().getInventory(player);

		if (expInv != null && expInv instanceof ExperienceInventory && !((ExperienceInventory) expInv).isOpening()) XpBank.getPlugin().removeInventory(expInv);
	}

}
