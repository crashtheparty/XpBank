package org.ctp.xpbank.listeners;

//import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
//import org.ctp.xpbank.XpBank;
//import org.ctp.xpbank.utils.ChatUtils;
//import org.ctp.xpbank.utils.InventoryUtils;
//import org.ctp.xpbank.utils.XpUtils;

//import org.bukkit.ChatColor;

public class InventoryClick implements Listener{
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		Inventory openedInv = event.getInventory();
		openedInv.getName();
//		Player player = null;
//		if (event.getWhoClicked() instanceof Player) {
//			player = (Player) event.getWhoClicked();
//		} else {
//			return;
//		}
//		Inventory inv = player.getInventory();
//		if (inv == null)
//			return;
//		if (openedInv.getName().equals(XpBank.getConfigUtils().getTranslatedBankName())) {
//			event.setCancelled(true);
//			if(inv.equals(openedInv)){
//				int finalLevel = -1;
//				int experience = 0;
//				int formerXp = 0;
//				switch(event.getSlot()){
//				case 22:
//					int mend = InventoryUtils.getExperienceToMend(player);
//					if(mend > 0) {
//						formerXp = XpBank.db.getInteger("xpbank", player.getUniqueId().toString(), "xp");
//						if(formerXp == 0) {
//							ChatUtils.sendMessage(player, ChatColor.RED + "No XP in " + XpBank.getConfigUtils().getTranslatedBankName() + ChatColor.RED + "! No items repaired.");
//							player.openInventory(InventoryUtils.createXpBank(player));
//							break;
//						}
//						int newXp = InventoryUtils.mendItems(player, formerXp);
//						XpBank.db.setInteger("xpbank", player.getUniqueId().toString(), "xp", newXp);
//						if(mend > formerXp) {
//							ChatUtils.sendMessage(player, ChatColor.RED + "Not enough XP in " + XpBank.getConfigUtils().getTranslatedBankName() + ChatColor.RED + "! Some items repaired.");
//						} else {
//							ChatUtils.sendMessage(player, "Mended all mendable items with " + XpBank.getConfigUtils().getTranslatedBankName() + ChatColor.WHITE + " XP!");
//						}
//						
//					} else {
//						ChatUtils.sendMessage(player, ChatColor.RED + "No mendable items or all items repaired!");
//					}
//					player.openInventory(InventoryUtils.createXpBank(player));
//					break;
//				case 11:
//					finalLevel = 0;
//				case 10:
//					if(finalLevel == -1){
//						finalLevel = player.getLevel() - 10;
//						if(player.getExp() != 0){
//							finalLevel ++;
//						}
//					}
//				case 9:
//					if(finalLevel == -1){
//						finalLevel = player.getLevel() - 1;
//						if(player.getExp() != 0){
//							finalLevel ++;
//						}
//						if(finalLevel < 0) finalLevel = 0;
//					}
//					boolean change = false;
//					experience = XpUtils.getExp(player);
//					if(player.getLevel() > 30){
//						change = true;
//					}
//					while(player.getExp() != 0 || player.getLevel() != finalLevel){
//						if(player.getLevel() < finalLevel) break;
//						if(player.getExp() <= 0 && player.getLevel() <= 0) break;
//						if(player.getLevel() == finalLevel || player.getLevel() == 0){
//							int finalXp = XpUtils.getExp(player) - XpUtils.getExpFromLevel(player.getLevel());
//							XpUtils.changeExp(player, -1 * finalXp);
//							break;
//						}else{
//							int finalXp = XpUtils.getExpFromLevel(player.getLevel()) - XpUtils.getExpFromLevel(player.getLevel() - 1);
//							XpUtils.changeExp(player, -1 * finalXp);
//						}
//					}
//					if(change){
//						experience -= 2;
//					}
//					experience -= XpUtils.getExp(player);
//					formerXp = XpBank.db.getInteger("xpbank", player.getUniqueId().toString(), "xp");
//					ChatUtils.sendMessage(player, "Added " + experience + " experience to " + XpBank.getConfigUtils().getTranslatedBankName() + ChatColor.WHITE + ".");
//					XpBank.db.setInteger("xpbank", player.getUniqueId().toString(), "xp", formerXp + experience);
//					player.openInventory(InventoryUtils.createXpBank(player));
//					break;
//				case 17:
//					finalLevel = 21863;
//				case 16:
//					if(finalLevel == -1){
//						finalLevel = player.getLevel() + 10;
//					}
//				case 15:
//					if(finalLevel == -1){
//						finalLevel = player.getLevel() + 1;
//					}
//					formerXp = XpBank.db.getInteger("xpbank", player.getUniqueId().toString(), "xp");
//					while(player.getExp() != 0 || player.getLevel() != finalLevel){
//						if(formerXp - experience <= 0){
//							int difference = Math.abs(formerXp - experience);
//							experience -= difference;
//							XpUtils.changeExp(player, -1 * difference);
//							break;
//						}
//						int finalXp = XpUtils.getExpToNext(player.getLevel());
//						experience += finalXp;
//						XpUtils.changeExp(player, finalXp);
//					}
//					if(formerXp - experience <= 0){
//						int difference = Math.abs(formerXp - experience);
//						experience -= difference;
//						XpUtils.changeExp(player, -1 * difference);
//					}
//					ChatUtils.sendMessage(player, "Taken " + experience + " experience from " + XpBank.getConfigUtils().getTranslatedBankName() + ChatColor.WHITE + ".");
//					XpBank.db.setInteger("xpbank", player.getUniqueId().toString(), "xp", formerXp - experience);
//					player.openInventory(InventoryUtils.createXpBank(player));
//					break;
//				}
//			}
//		}
	}

}
