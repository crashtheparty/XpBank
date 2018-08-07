package org.ctp.xpbank.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemSerialization {

	public static String itemToString(ItemStack item) {
		String itemString = "";
		if (item.getType() != null) {
			itemString = itemString + "name@" + item.getType();
		}
		itemString = itemString + " amount@" + item.getAmount();
		if (item.getType().equals(Material.AIR)) return itemString;
		if (item.getItemMeta().getDisplayName() != null && !item.getItemMeta().getDisplayName().equals("")) {
			itemString = itemString
					+ " item_name@"
					+ item.getItemMeta().getDisplayName().replace(" ", "_")
							.replace("�", "&");
		}
		if (item.getDurability() != 0) {
			itemString = itemString + " damage@" + item.getDurability();
		}
		Map<Enchantment, Integer> isEnch = item.getEnchantments();
		if (isEnch.size() > 0) {
			for (Map.Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
				itemString = itemString + " enchant@"
						+ ench.getKey().getKey().getNamespace() + "+" + ench.getKey().getKey().getKey() + "@"
						+ ench.getValue();
			}
		}

		List<String> isLore = item.getItemMeta().getLore();
		if ((item.getItemMeta().getLore() != null) && (isLore.size() != 0)) {
			for (String lore : isLore) {
				itemString = itemString + " lore@"
						+ lore.replace(" ", "_").replace("�", "&");
			}
		}

		if ((item.getType().equals(Material.PLAYER_HEAD))) {
			itemString = itemString + " owner@"
					+ ((SkullMeta) item.getItemMeta()).getOwningPlayer();
		}

		return itemString;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack stringToItem(String itemString) {
		ItemStack is = null;
		Boolean createdItemStack = Boolean.valueOf(false);

		String[] serializedItem = itemString.split(" ");
		for (String itemInfo : serializedItem) {
			String[] itemAttribute = itemInfo.split("@");
			if (itemAttribute[0].equals("name")) {
				is = new ItemStack(Material.getMaterial(itemAttribute[1]));
				createdItemStack = Boolean.valueOf(true);
			} else if ((itemAttribute[0].equals("damage"))
					&& (createdItemStack.booleanValue())) {
				is.setDurability(Short.valueOf(itemAttribute[1]).shortValue());
			} else if ((itemAttribute[0].equals("amount"))
					&& (createdItemStack.booleanValue())) {
				is.setAmount(Integer.valueOf(itemAttribute[1]).intValue());
			} else if ((itemAttribute[0].equals("item_name"))
					&& (createdItemStack.booleanValue())) {
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
						itemAttribute[1].replace("_", " ")));
				is.setItemMeta(im);
			} else if ((itemAttribute[0].equals("enchant"))
					&& (createdItemStack.booleanValue())) {
				NamespacedKey key = null;
				String[] enchString = itemAttribute[1].split("+");
				if(enchString[0].equalsIgnoreCase("minecraft")) {
					key = NamespacedKey.minecraft(enchString[1]);
				}
				
				if (Enchantment.getByKey(key) != null) {
					is.addUnsafeEnchantment(Enchantment
							.getByKey(key),
							Integer.valueOf(itemAttribute[2]).intValue());
				} else {
					ChatUtils.sendToConsole(
							"Wrong enchantment name: "
									+ itemAttribute[1]);
					ChatUtils.sendToConsole(
							"Please fix the name in config!");
				}
			} else if ((itemAttribute[0].equals("lore"))
					&& (createdItemStack.booleanValue())) {
				ItemMeta im = is.getItemMeta();
				List<String> il = new ArrayList<String>();

				if (is.getItemMeta().getLore() != null) {
					for (String lore : is.getItemMeta().getLore())
						if (lore != null)
							il.add(ChatColor.translateAlternateColorCodes('&',
									lore.replace("_", " ")));
				}
				il.add(ChatColor.translateAlternateColorCodes('&',
						itemAttribute[1].replace("_", " ")));
				im.setLore(il);
				is.setItemMeta(im);
			} else if ((itemAttribute[0].equals("owner"))
					&& (createdItemStack.booleanValue())) {
				SkullMeta im = (SkullMeta) is.getItemMeta();
				im.setOwner(itemAttribute[1]);
				is.setItemMeta(im);
			}
		}
		return is;
	}
}