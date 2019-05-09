package org.ctp.xpbank;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.ctp.xpbank.commands.Admin;
import org.ctp.xpbank.commands.Open;
import org.ctp.xpbank.database.SQLite;
import org.ctp.xpbank.listeners.InventoryClick;
import org.ctp.xpbank.listeners.InventoryClose;
import org.ctp.xpbank.listeners.VersionCheck;
import org.ctp.xpbank.utils.ConfigUtils;
import org.ctp.xpbank.utils.ItemSerialization;
import org.ctp.xpbank.utils.config.YamlConfig;
import org.ctp.xpbank.version.PluginVersion;

public class XpBank extends JavaPlugin {

	private static YamlConfig CONFIG;
	private static Economy ECON = null;
	private static ConfigUtils CONFIG_UTILS;
	public static SQLite db;
	public static Plugin PLUGIN;
	public static boolean NEWEST_VERSION = true;
	private static PluginVersion PLUGIN_VERSION;
	private VersionCheck check;

	public void onEnable() {
		PLUGIN = this;
		PLUGIN_VERSION = new PluginVersion(this, getDescription().getVersion());
		File file = new File(getDataFolder() + "/config.yml");
		try {
			if (!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		YamlConfiguration.loadConfiguration(file);
		CONFIG = new YamlConfig(file, null);
		
		CONFIG.getFromConfig();
		
		CONFIG.addDefault("starter", (ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Experience Bank" + ChatColor.DARK_GRAY + "]").replace(ChatColor.COLOR_CHAR, '&'), new String[] {"Starting string in front of messages sent by this plugin"});
		CONFIG.addDefault("bank_name", (ChatColor.BLUE + "Experience Bank").replace(ChatColor.COLOR_CHAR, '&'), new String[] {"Name of the banks"});
		CONFIG.addDefault("vault", hasVault(), new String[] {"Whether to use Vault for economy plugins"});
		CONFIG.addDefault("price", 1000, new String[] {"Vault price to open a bank"});
		CONFIG.addDefault("price_item", ItemSerialization
				.itemToString(new ItemStack(Material.DIAMOND, 4)), new String[] {"Non-vault price to open a bank"});
		CONFIG.addDefault("one_time", true, new String[] {"Whether each open costs money", "If not, plugin uses access time"});
		CONFIG.addDefault("access_time", 60, new String[] {"Time (in seconds) before player must pay to enter their bank again."});
		CONFIG.addDefault("get_latest_version", true, new String[] {"Check for the latest version of this plugin."});
		CONFIG.saveConfig();
		
		getCommand("xpopen").setExecutor(new Open());
		getCommand("xpadmin").setExecutor(new Admin());
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new InventoryClick(), this);
		pm.registerEvents(new InventoryClose(), this);
		CONFIG_UTILS = new ConfigUtils(CONFIG);
		if(CONFIG_UTILS.usingEconomy() && ECON == null) {
			CONFIG.set("vault", false);
			this.getLogger().log(Level.WARNING, "Economy plugin was not found. Please set up an economy plugin before enabling the vault feature.");
			CONFIG.saveConfig();
		}
		
		db = new SQLite(this);
		db.load();
		
		check = new VersionCheck(PLUGIN_VERSION, "https://raw.githubusercontent.com/crashtheparty/XpBank/master/VersionHistory", 
				"https://www.spigotmc.org/resources/xpbank.59580/", "https://github.com/crashtheparty/XpBank", 
				CONFIG.getBoolean("get_latest_version"));
		getServer().getPluginManager().registerEvents(check, this);
		checkVersion();
		
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
	
	private void checkVersion(){
		Bukkit.getScheduler().runTaskTimerAsynchronously(PLUGIN, check, 20l, 20 * 60 * 60 * 4l);
    }

	public static PluginVersion getPluginVersion() {
		return PLUGIN_VERSION;
	}
}
