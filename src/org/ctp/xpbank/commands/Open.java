package org.ctp.xpbank.commands;

import org.bukkit.ChatColor;
import net.milkbowl.vault.economy.Economy;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.threads.OpenTime;
import org.ctp.xpbank.utils.ChatUtils;
import org.ctp.xpbank.utils.ConfigUtils;
import org.ctp.xpbank.utils.InventoryUtils;

public class Open implements CommandExecutor{
	
	private static HashMap<String, OpenTime> ACCESS = new HashMap<String, OpenTime>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if(sender instanceof Player){
			ConfigUtils utils = XpBank.getConfigUtils();
			Player player = (Player) sender;
			if (!openedForFree(player)) {
				if(utils.isOneTime()) {
					oneTime(player);
				} else {
					timedAccess(player);
				}
			} else {
				Inventory inv = InventoryUtils.createXpBank(player);
				player.openInventory(inv);
			}
		}
		return false;
	}
	
	private boolean openedForFree(Player player) {
		ConfigUtils utils = XpBank.getConfigUtils();
		if(utils.usingEconomy() && utils.getPrice() <= 0){
			ChatUtils.sendMessage(player, "Opening your account.");
			return true;
		}else if(utils.getPriceItem().getType().equals(Material.AIR)){
			ChatUtils.sendMessage(player, "Opening your account.");
			return true;
		}
		if(player.hasPermission("xpbank.free")) {
			ChatUtils.sendMessage(player, "Opening account for free.");
			player.openInventory(InventoryUtils.createXpBank(player));
			return true;
		}
		return false;
	}
	
	private void oneTime(Player player) {
		ConfigUtils utils = XpBank.getConfigUtils();
		if(utils.usingEconomy()){
			Economy econ = XpBank.getEconomy();
			double price = utils.getPrice();
			if(econ.getBalance(player) >= price){
				econ.withdrawPlayer(player, price);
				Inventory inv = InventoryUtils.createXpBank(player);
				ChatUtils.sendMessage(player, "Spent " + price + " to unlock your account.");
				player.openInventory(inv);
			}else{
				ChatUtils.sendMessage(player, "You do not have enough money to unlock your account. Must have " + price + ".");
			}
		}else{
			int reward = 0;
			ItemStack item = utils.getPriceItem();
			for (int j = 1; j <= 64; j++) {
				ItemStack rewardItem = new ItemStack(item.getType(), j);
				rewardItem.setItemMeta(item.getItemMeta());
				rewardItem.setDurability(item.getDurability());
				if (player.getInventory().contains(rewardItem)) {
					reward += j;
				}
			}
			if(reward >= item.getAmount()){
				player.getInventory().removeItem(item);
				ChatUtils.sendMessage(player, "Spent " + item.toString().replace(ChatColor.COLOR_CHAR, '&') + " to unlock your account.");
				player.openInventory(InventoryUtils.createXpBank(player));
			}else{
				ChatUtils.sendMessage(player, "You do not have the items to unlock your account: " + item.toString().replace(ChatColor.COLOR_CHAR, '&') + ".");
			}
		}
	}
	
	private void timedAccess(Player player) {
		ConfigUtils utils = XpBank.getConfigUtils();
		if(ACCESS.containsKey(player.getUniqueId().toString())) {
			OpenTime access = ACCESS.get(player.getUniqueId().toString());
			ChatUtils.sendMessage(player, "Opening account for free.");
			ChatUtils.sendMessage(player, "Account will lock again in " + access.getRunTime() + " seconds.");
			player.openInventory(InventoryUtils.createXpBank(player));
			return;
		}
		if(utils.usingEconomy()){
			double price = utils.getPrice();
			Economy econ = XpBank.getEconomy();
			if(econ.getBalance(player) >= price){
				OpenTime access = new OpenTime(player.getUniqueId());
				int scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(XpBank.PLUGIN, access, 20l, 20l);
				access.setScheduler(scheduler);
				ACCESS.put(player.getUniqueId().toString(), access);
				econ.withdrawPlayer(player, price);
				Inventory inv = InventoryUtils.createXpBank(player);
				ChatUtils.sendMessage(player, "Spent " + price + " to unlock your account.");
				ChatUtils.sendMessage(player, "Your account will be locked again in " + utils.getAccessTime() + " seconds.");
				player.openInventory(inv);
			}else{
				ChatUtils.sendMessage(player, "You do not have enough money to unlock your account. Must have " + price + ".");
			}
		}else{
			int reward = 0;
			ItemStack item = utils.getPriceItem();
			for (int j = 1; j <= 64; j++) {
				ItemStack rewardItem = new ItemStack(item.getType(), j);
				rewardItem.setItemMeta(item.getItemMeta());
				rewardItem.setDurability(item.getDurability());
				if (player.getInventory().contains(rewardItem)) {
					reward += j;
				}
			}
			if(reward >= item.getAmount()){
				OpenTime access = new OpenTime(player.getUniqueId());
				int scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(XpBank.PLUGIN, access, 20l, 20l);
				access.setScheduler(scheduler);
				ACCESS.put(player.getUniqueId().toString(), access);
				player.getInventory().removeItem(item);
				ChatUtils.sendMessage(player, "Spent " + item.toString().replace(ChatColor.COLOR_CHAR, '&') + " to unlock your account.");
				ChatUtils.sendMessage(player, "Your account will be locked again in " + utils.getAccessTime() + " seconds.");
				player.openInventory(InventoryUtils.createXpBank(player));
			}else{
				ChatUtils.sendMessage(player, "You do not have the items to unlock your account: " + item.toString().replace(ChatColor.COLOR_CHAR, '&') + ".");
			}
		}
	}
	
	public static void revokeAccess(OpenTime access) {
		ACCESS.remove(access.getPlayer().toString());
		Bukkit.getScheduler().cancelTask(access.getScheduler());
		Player player = Bukkit.getServer().getPlayer(access.getPlayer());
		if(player != null) {
			ChatUtils.sendMessage(player, "Your account has been locked.");
		}
	}

}
