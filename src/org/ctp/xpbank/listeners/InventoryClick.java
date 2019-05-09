package org.ctp.xpbank.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.inventories.ExperienceInventory;
import org.ctp.xpbank.utils.ChatUtils;
import org.ctp.xpbank.utils.InventoryUtils;
import org.ctp.xpbank.utils.XpUtils;

import org.bukkit.ChatColor;

public class InventoryClick implements Listener{
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		Inventory openedInv = event.getInventory();
		Player player = null;
		if (event.getWhoClicked() instanceof Player) {
			player = (Player) event.getWhoClicked();
		} else {
			return;
		}
		ExperienceInventory expInv = InventoryUtils.getInventory(player);
		if (expInv != null) {
			Inventory inv = event.getClickedInventory();
			event.setCancelled(true);
			if(inv.equals(openedInv)){
				int finalLevel = -1;
				int formerXp = 0;
				int levelExperience = 0;
				int difference = 0;
				switch(event.getSlot()){
				case 22:
					int mend = expInv.getExperienceToMend(player);
					if(mend > 0) {
						formerXp = XpBank.db.getInteger("xpbank", player.getUniqueId().toString(), "xp");
						if(formerXp == 0) {
							ChatUtils.sendMessage(player, ChatColor.RED + "No XP in " + XpBank.getConfigUtils().getTranslatedBankName() + ChatColor.RED + "! No items repaired.");
							expInv.openInventory();
							break;
						}
						int newXp = expInv.mendItems(player, formerXp);
						XpBank.db.setInteger("xpbank", player.getUniqueId().toString(), "xp", newXp);
						if(mend > formerXp) {
							ChatUtils.sendMessage(player, ChatColor.RED + "Not enough XP in " + XpBank.getConfigUtils().getTranslatedBankName() + ChatColor.RED + "! Some items repaired.");
						} else {
							ChatUtils.sendMessage(player, "Mended all mendable items with " + XpBank.getConfigUtils().getTranslatedBankName() + ChatColor.WHITE + " XP!");
						}
						
					} else {
						ChatUtils.sendMessage(player, ChatColor.RED + "No mendable items or all items repaired!");
					}
					expInv.openInventory();
					break;
				case 11:
					finalLevel = 0;
				case 10:
					if(finalLevel == -1){
						finalLevel = player.getLevel() - 10;
						if(player.getExp() != 0){
							finalLevel ++;
						}
					}
				case 9:
					if(finalLevel == -1){
						finalLevel = player.getLevel() - 1;
						if(player.getExp() != 0){
							finalLevel ++;
						}
						if(finalLevel < 0) finalLevel = 0;
					}
					formerXp = XpBank.db.getInteger("xpbank", player.getUniqueId().toString(), "xp");
					levelExperience = XpUtils.getExpForLevel(finalLevel);
					difference = XpUtils.changeExp(player, levelExperience, formerXp);
					ChatUtils.sendMessage(player, "Added " + difference + " experience to " + XpBank.getConfigUtils().getTranslatedBankName() + ChatColor.WHITE + ".");
					XpBank.db.setInteger("xpbank", player.getUniqueId().toString(), "xp", formerXp + difference);
					expInv.openInventory();
					break;
				case 17:
					finalLevel = 21863;
				case 16:
					if(finalLevel == -1){
						finalLevel = player.getLevel() + 10;
					}
				case 15:
					if(finalLevel == -1){
						finalLevel = player.getLevel() + 1;
					}
					formerXp = XpBank.db.getInteger("xpbank", player.getUniqueId().toString(), "xp");
					levelExperience = XpUtils.getExpForLevel(finalLevel);
					difference = XpUtils.changeExp(player, levelExperience, formerXp);
					ChatUtils.sendMessage(player, "Taken " + (-1 * difference) + " experience from " + XpBank.getConfigUtils().getTranslatedBankName() + ChatColor.WHITE + ".");
					XpBank.db.setInteger("xpbank", player.getUniqueId().toString(), "xp", formerXp + difference);
					expInv.openInventory();
					break;
				}
			}
		}
	}

}
