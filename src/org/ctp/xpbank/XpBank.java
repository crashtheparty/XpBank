package org.ctp.xpbank;

import net.milkbowl.vault.economy.Economy;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.ctp.xpbank.commands.Admin;
import org.ctp.xpbank.commands.Open;
import org.ctp.xpbank.database.SQLite;
import org.ctp.xpbank.listeners.InventoryClick;
import org.ctp.xpbank.utils.ConfigUtils;
import org.ctp.xpbank.utils.ItemSerialization;
import org.ctp.xpbank.utils.SimpleConfig;
import org.ctp.xpbank.utils.SimpleConfigManager;

public class XpBank extends JavaPlugin {

	private static SimpleConfigManager MANAGER;
	private static SimpleConfig CONFIG;
	private static Economy ECON = null;
	private static ConfigUtils CONFIG_UTILS;
	public static SQLite db;
	public static Plugin PLUGIN;

	public void onEnable() {
		PLUGIN = this;
		MANAGER = new SimpleConfigManager(this);
		CONFIG = MANAGER.getNewConfig("config.yml");
		CONFIG.addDefault("starter", (ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Experience Bank" + ChatColor.DARK_GRAY + "]").replace(ChatColor.COLOR_CHAR, '&'));
		CONFIG.addDefault("bank_name", (ChatColor.BLUE + "Experience Bank").replace(ChatColor.COLOR_CHAR, '&'));
		if (hasVault()) {
			CONFIG.addDefault("vault", true);
		} else {
			CONFIG.addDefault("vault", false);
		}
		CONFIG.addDefault("price", 1000);
		CONFIG.addDefault("price_item", ItemSerialization
				.itemToString(new ItemStack(Material.DIAMOND, 4)));
		CONFIG.addDefault("one_time", true);
		CONFIG.addDefault("access_time", 60);
		CONFIG.saveConfig();
		
		getCommand("xpopen").setExecutor(new Open());
		getCommand("xpadmin").setExecutor(new Admin());
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new InventoryClick(), this);
		CONFIG_UTILS = new ConfigUtils(CONFIG);
		if(CONFIG_UTILS.usingEconomy() && ECON == null) {
			CONFIG.set("vault", false);
			this.getLogger().log(Level.WARNING, "Economy plugin was not found. Please set up an economy plugin before enabling the vault feature.");
			CONFIG.saveConfig();
		}
		
		db = new SQLite(this);
		db.load();
	}

	public boolean hasVault() {
		if(!Bukkit.getPluginManager().isPluginEnabled("Vault")){
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		ECON = rsp.getProvider();
		return ECON != null;
	}
	
	public static ConfigUtils getConfigUtils() {
		return CONFIG_UTILS;
	}
	
	public static Economy getEconomy() {
		return ECON;
	}
}
