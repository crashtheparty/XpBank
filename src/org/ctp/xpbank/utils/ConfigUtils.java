package org.ctp.xpbank.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.ctp.xpbank.utils.config.YamlConfig;

public class ConfigUtils {
	
	private YamlConfig config;
	private ItemStack priceItem;
	private double price;
	private boolean oneTime, vault, latestVersion;
	private int accessTime;
	private String bankName, starter;
	
	public ConfigUtils(YamlConfig config) {
		this.config = config;
		String priceItemString = config.getString("price_item");
		setPriceItem(ItemSerialization.stringToItem(priceItemString));
		setPrice(config.getDouble("price"));
		setOneTime(config.getBoolean("one_time"));
		setAccessTime(config.getInt("access_time"));
		bankName = config.getString("bank_name");
		starter = config.getString("starter");
		vault = config.getBoolean("vault");
		latestVersion = config.getBoolean("get_latest_version");
	}

	public ItemStack getPriceItem() {
		return priceItem;
	}

	public void setPriceItem(ItemStack priceItem) {
		config.set("price_item", ItemSerialization.itemToString(priceItem));
		config.saveConfig();
		this.priceItem = priceItem;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		config.set("price", price);
		config.saveConfig();
		this.price = price;
	}

	public boolean isOneTime() {
		return oneTime;
	}

	public void setOneTime(boolean oneTime) {
		config.set("one_time", oneTime);
		config.saveConfig();
		this.oneTime = oneTime;
	}

	public int getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(int accessTime) {
		config.set("access_time", accessTime);
		config.saveConfig();
		this.accessTime = accessTime;
	}
	
	public String getBankName() {
		return bankName;
	}
	
	public String getTranslatedBankName() {
		return ChatColor.translateAlternateColorCodes('&', bankName);
	}
	
	public String getStarter() {
		return starter;
	}
	
	public String getTranslatedStarter() {
		return ChatColor.translateAlternateColorCodes('&', starter) + " " + ChatColor.WHITE;
	}
	
	public boolean usingEconomy() {
		return vault;
	}
	
	public void setEconomy(boolean econ) {
		config.set("vault", econ);
		config.saveConfig();
		vault = econ;
	}

	public boolean checkLatestVersion() {
		return latestVersion;
	}

	public void setCheckLatestVersion(boolean latestVersion) {
		this.latestVersion = latestVersion;
	}

}
