package org.ctp.xpbank.listeners;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.utils.ChatUtils;
import org.ctp.xpbank.Chatable;
import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.inventories.ExperienceInventory;
import org.ctp.xpbank.utils.DBUtils;
import org.ctp.xpbank.utils.XpUtils;

import com.google.common.collect.Maps;

public class InventoryClick implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory openedInv = event.getInventory();
		Player player = null;
		if (event.getWhoClicked() instanceof Player) player = (Player) event.getWhoClicked();
		else
			return;
		InventoryData i = XpBank.getPlugin().getInventory(player);
		if (i != null && i instanceof ExperienceInventory) {
			HashMap<String, Object> bankCodes = ChatUtils.getCodes();
			bankCodes.put("%bank%", Chatable.get().getMessage(ChatUtils.getCodes(), "bank.name"));
			HashMap<String, Object> addTake = Maps.newHashMap(bankCodes);
			ExperienceInventory expInv = (ExperienceInventory) i;
			OfflinePlayer editing = expInv.getEditing();
			Inventory inv = event.getClickedInventory();
			event.setCancelled(true);
			if (inv != null && inv.equals(openedInv)) {
				int finalLevel = -1;
				int formerXp = 0;
				int levelExperience = 0;
				int difference = 0;
				switch (event.getSlot()) {
					case 22:
						int mend = expInv.getExperienceToMend(player);
						if (mend > 0) {
							formerXp = DBUtils.getExperience(editing);
							if (formerXp == 0) {
								Chatable.get().sendMessage(player, Chatable.get().getMessage(bankCodes, "inventory.fail_mending"));
								expInv.setInventory();
								break;
							}
							int newXp = expInv.mendItems(player, formerXp);
							DBUtils.setExperience(editing, newXp);
							if (mend > formerXp) Chatable.get().sendMessage(player, Chatable.get().getMessage(bankCodes, "inventory.partial_mending"));
							else
								Chatable.get().sendMessage(player, Chatable.get().getMessage(bankCodes, "inventory.full_mending"));

						} else
							Chatable.get().sendMessage(player, Chatable.get().getMessage(bankCodes, "inventory.no_mending"));
						expInv.setInventory();
						break;
					case 11:
						finalLevel = 0;
					case 10:
						if (finalLevel == -1) {
							finalLevel = player.getLevel() - 10;
							if (player.getExp() != 0) finalLevel++;
						}
					case 9:
						if (finalLevel == -1) {
							finalLevel = player.getLevel() - 1;
							if (player.getExp() != 0) finalLevel++;
							if (finalLevel < 0) finalLevel = 0;
						}
						formerXp = DBUtils.getExperience(editing);
						levelExperience = XpUtils.getExpForLevel(finalLevel);
						difference = XpUtils.changeExp(player, levelExperience, formerXp);
						addTake.put("%num%", difference);
						Chatable.get().sendMessage(player, Chatable.get().getMessage(addTake, "inventory.add"));
						DBUtils.setExperience(editing, formerXp + difference);
						expInv.setInventory();
						break;
					case 17:
						finalLevel = 21863;
					case 16:
						if (finalLevel == -1) finalLevel = player.getLevel() + 10;
					case 15:
						if (finalLevel == -1) finalLevel = player.getLevel() + 1;
						formerXp = DBUtils.getExperience(editing);
						levelExperience = XpUtils.getExpForLevel(finalLevel);
						difference = XpUtils.changeExp(player, levelExperience, formerXp);
						addTake.put("%num%", -1 * difference);
						Chatable.get().sendMessage(player, Chatable.get().getMessage(addTake, "inventory.take"));
						DBUtils.setExperience(editing, formerXp + difference);
						expInv.setInventory();
						break;
				}
			}
		}
	}

}
