package org.ctp.xpbank.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.ctp.xpbank.XpBank;

public class InventoryUtils {

	public static Inventory createXpBank(Player player){
		Inventory inv = Bukkit.createInventory(null, 27, XpBank.getConfigUtils().getTranslatedBankName());
		int totalExp = XpBank.db.getInteger("xpbank", player.getUniqueId().toString(), "xp");
		ItemStack total = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta totalMeta = total.getItemMeta();
		totalMeta.setDisplayName(ChatColor.GOLD + "Experience: " + ChatColor.DARK_AQUA + "" + totalExp);
		total.setItemMeta(totalMeta);
		inv.setItem(4, total);
		ItemStack addOne = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta addOneMeta = addOne.getItemMeta();
		addOneMeta.setDisplayName(ChatColor.GREEN + "Add 1 Level to Bank");
		addOne.setItemMeta(addOneMeta);
		inv.setItem(9, addOne);
		ItemStack addTen = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta addTenMeta = addTen.getItemMeta();
		addTenMeta.setDisplayName(ChatColor.GREEN + "Add 10 Levels to Bank");
		addTen.setItemMeta(addTenMeta);
		inv.setItem(10, addTen);
		ItemStack addAll = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta addAllMeta = addAll.getItemMeta();
		addAllMeta.setDisplayName(ChatColor.GREEN + "Add All Levels to Bank");
		addAll.setItemMeta(addAllMeta);
		inv.setItem(11, addAll);
		
		ItemStack removeOne = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta removeOneMeta = removeOne.getItemMeta();
		removeOneMeta.setDisplayName(ChatColor.RED + "Take 1 Level from Bank");
		removeOne.setItemMeta(removeOneMeta);
		inv.setItem(15, removeOne);
		ItemStack removeTen = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta removeTenMeta = removeTen.getItemMeta();
		removeTenMeta.setDisplayName(ChatColor.RED + "Take 10 Levels from Bank");
		removeTen.setItemMeta(removeTenMeta);
		inv.setItem(16, removeTen);
		ItemStack removeAll = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta removeAllMeta = removeAll.getItemMeta();
		removeAllMeta.setDisplayName(ChatColor.RED + "Take All Levels from Bank");
		removeAll.setItemMeta(removeAllMeta);
		inv.setItem(17, removeAll);
		if(player.hasPermission("xpbank.mending")) {
			ItemStack mend = new ItemStack(Material.ENCHANTED_BOOK);
			ItemMeta mendMeta = mend.getItemMeta();
			mendMeta.addEnchant(Enchantment.MENDING, 1, false);
			mendMeta.setDisplayName(ChatColor.BLUE + "Mend Items");
			mendMeta.setLore(Arrays.asList("Cost: " + getExperienceToMend(player)));
			mend.setItemMeta(mendMeta);
			inv.setItem(22, mend);
		}
		return inv;
	}
	
	public static int getExperienceToMend(Player player) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		ItemStack[] armor = player.getInventory().getArmorContents();
		for(int i = 0; i < armor.length; i++) {
			if(armor[i] != null) {
				items.add(armor[i]);
			}
		}
		items.add(player.getInventory().getItemInMainHand());
		items.add(player.getInventory().getItemInOffHand());
		
		int exp = 0;
		for(ItemStack item : items) {
			if(item != null && item.getItemMeta() != null) {
				if(item.getItemMeta().hasEnchant(Enchantment.MENDING) && item.getItemMeta() instanceof Damageable) {
					exp += (((Damageable) item.getItemMeta()).getDamage() + 1) / 2;
				}
			}
		}
		
		return exp;
	}
	
	public static int mendItems(Player player, int exp) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		ItemStack[] armor = player.getInventory().getArmorContents();
		for(int i = 0; i < armor.length; i++) {
			if(armor[i] != null) {
				items.add(armor[i]);
			}
		}
		items.add(player.getInventory().getItemInMainHand());
		items.add(player.getInventory().getItemInOffHand());
		
		for(ItemStack item : items) {
			if(item != null && item.getItemMeta() != null) {
				if(item.getItemMeta().hasEnchant(Enchantment.MENDING) && item.getItemMeta() instanceof Damageable) {
					Damageable meta = ((Damageable) item.getItemMeta());
					int durability = meta.getDamage();
					while(exp > 0 && durability > 0) {
						durability -= 2;
						exp--;
					}
					if(durability < 0) durability = 0;
					((Damageable) item.getItemMeta()).setDamage(durability);
				}
			}
		}
		
		return exp;
	}
}
