package org.ctp.xpbank.inventories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.utils.ChatUtils;
import org.ctp.crashapi.utils.DamageUtils;
import org.ctp.crashapi.utils.ItemUtils;
import org.ctp.xpbank.Chatable;
import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.utils.DBUtils;

public class ExperienceInventory implements InventoryData, Chatable {
	
	private OfflinePlayer editing;
	private Player player;
	private Inventory inventory;
	private boolean opening = true;
	
	public ExperienceInventory(Player player) {
		this.player = player;
		editing = player;
	}
	
	public ExperienceInventory(Player player, OfflinePlayer editing) {
		this.player = player;
		this.editing = editing;
	}

	@Override
	public void setInventory() {
		Inventory inv = Bukkit.createInventory(null, 27, getChat().getMessage(new HashMap<String, Object>(), "bank.name"));
				
		inv = open(inv);
		
		int totalExp = DBUtils.getExperience(editing);
		ItemStack total = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta totalMeta = total.getItemMeta();
		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%exp%", totalExp);
		totalMeta.setDisplayName(getChat().getMessage(codes, "inventory.total_exp"));
		total.setItemMeta(totalMeta);
		inv.setItem(4, total);
		ItemStack addOne = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta addOneMeta = addOne.getItemMeta();
		addOneMeta.setDisplayName(getChat().getMessage(ChatUtils.getCodes(), "inventory.add_one_level"));
		addOne.setItemMeta(addOneMeta);
		inv.setItem(9, addOne);
		ItemStack addTen = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta addTenMeta = addTen.getItemMeta();
		addTenMeta.setDisplayName(getChat().getMessage(ChatUtils.getCodes(), "inventory.add_ten_levels"));
		addTen.setItemMeta(addTenMeta);
		inv.setItem(10, addTen);
		ItemStack addAll = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta addAllMeta = addAll.getItemMeta();
		addAllMeta.setDisplayName(getChat().getMessage(ChatUtils.getCodes(), "inventory.add_all_levels"));
		addAll.setItemMeta(addAllMeta);
		inv.setItem(11, addAll);
		
		ItemStack removeOne = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta removeOneMeta = removeOne.getItemMeta();
		removeOneMeta.setDisplayName(getChat().getMessage(ChatUtils.getCodes(), "inventory.take_one_level"));
		removeOne.setItemMeta(removeOneMeta);
		inv.setItem(15, removeOne);
		ItemStack removeTen = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta removeTenMeta = removeTen.getItemMeta();
		removeTenMeta.setDisplayName(getChat().getMessage(ChatUtils.getCodes(), "inventory.take_ten_levels"));
		removeTen.setItemMeta(removeTenMeta);
		inv.setItem(16, removeTen);
		ItemStack removeAll = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta removeAllMeta = removeAll.getItemMeta();
		removeAllMeta.setDisplayName(getChat().getMessage(ChatUtils.getCodes(), "inventory.take_all_levels"));
		removeAll.setItemMeta(removeAllMeta);
		inv.setItem(17, removeAll);
		if(player.equals(editing) && player.hasPermission("xpbank.mending")) {
			ItemStack mend = new ItemStack(Material.ENCHANTED_BOOK);
			ItemMeta mendMeta = mend.getItemMeta();
			mendMeta.addEnchant(Enchantment.MENDING, 1, false);
			mendMeta.setDisplayName(getChat().getMessage(ChatUtils.getCodes(), "inventory.mending"));
			HashMap<String, Object> costCodes = new HashMap<String, Object>();
			costCodes.put("%cost%", getExperienceToMend(player));
			mendMeta.setLore(getChat().getMessages(costCodes, "inventory.mending_cost"));
			mend.setItemMeta(mendMeta);
			inv.setItem(22, mend);
		}
		opening = false;
	}
	
	public int getExperienceToMend(Player player) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		ItemStack[] armor = player.getInventory().getArmorContents();
		for(int i = 0; i < armor.length; i++)
			if(armor[i] != null) items.add(armor[i]);
		items.add(player.getInventory().getItemInMainHand());
		items.add(player.getInventory().getItemInOffHand());
		
		int exp = 0;
		for(ItemStack item : items)
			if(item != null && item.getItemMeta() != null) if(item.getItemMeta().hasEnchant(Enchantment.MENDING) && item.getItemMeta() instanceof Damageable) exp += (DamageUtils.getDamage(item) + 1) / 2;
		
		return exp;
	}
	
	public int mendItems(Player player, int exp) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		ItemStack[] armor = player.getInventory().getArmorContents();
		for(int i = 0; i < armor.length; i++)
			if(armor[i] != null) items.add(armor[i]);
		items.add(player.getInventory().getItemInMainHand());
		items.add(player.getInventory().getItemInOffHand());
		
		for(ItemStack item : items)
			if(item != null && item.getItemMeta() != null) if(item.getItemMeta().hasEnchant(Enchantment.MENDING) && item.getItemMeta() instanceof Damageable) {
				int durability = DamageUtils.getDamage(item);
				while(exp > 0 && durability > 0) {
					durability -= 2;
					exp--;
				}
				if(durability < 0) durability = 0;
				DamageUtils.setDamage(item, durability);
			}
		
		return exp;
	}

	public boolean isOpening() {
		return opening;
	}

	@Override
	public Inventory open(Inventory inv) {
		opening = true;
		if (inventory == null) {
			inventory = inv;
			player.openInventory(inv);
		} else if (inv.getSize() == inventory.getSize()) {
			inv = player.getOpenInventory().getTopInventory();
			inventory = inv;
		} else {
			inventory = inv;
			player.openInventory(inv);
		}
		for(int i = 0; i < inventory.getSize(); i++)
			inventory.setItem(i, new ItemStack(Material.AIR));
		if (opening) opening = false;
		return inv;
	}

	@Override
	public void close(boolean external) {
		if (XpBank.getPlugin().hasInventory(this)) {
			if (getItems() != null) for(ItemStack item: getItems())
				ItemUtils.giveItemToPlayer(player, item, player.getLocation(), false);
			XpBank.getPlugin().removeInventory(this);
			if (!external) player.getOpenInventory().close();
		}
	}

	@Override
	public Block getBlock() {
		return null;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public List<ItemStack> getItems() {
		return null;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void setInventory(List<ItemStack> arg0) {
		setInventory();
	}

	@Override
	public void setItemName(String arg0) {}

	public OfflinePlayer getEditing() {
		return editing;
	}
}
