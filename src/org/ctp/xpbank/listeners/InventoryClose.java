package org.ctp.xpbank.listeners;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.ctp.xpbank.inventories.ExperienceInventory;
import org.ctp.xpbank.utils.ChatUtils;
import org.ctp.xpbank.utils.InventoryUtils;

public class InventoryClose implements Listener{
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = null;
		if (event.getPlayer() instanceof Player) {
			player = (Player) event.getPlayer();
		} else {
			return;
		}
		ExperienceInventory expInv = InventoryUtils.getInventory(player);
		
		if(expInv != null) {
			if(!expInv.isOpening()) {
				ChatUtils.sendToConsole(Level.INFO, "Removing the inventory");
				InventoryUtils.removeInventory(player);
			}
		}
	}

}
