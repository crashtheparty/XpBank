package org.ctp.xpbank;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.ctp.crashapi.CrashAPIPlugin;
import org.ctp.crashapi.config.yaml.YamlConfig;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.item.ItemSerialization;
import org.ctp.crashapi.utils.ChatUtils;
import org.ctp.crashapi.version.PluginVersion;
import org.ctp.crashapi.version.Version;
import org.ctp.crashapi.version.Version.VersionType;
import org.ctp.crashapi.version.VersionCheck;
import org.ctp.xpbank.commands.XpBankCommand;
import org.ctp.xpbank.database.XpBackup;
import org.ctp.xpbank.database.XpDatabase;
import org.ctp.xpbank.listeners.InventoryClick;
import org.ctp.xpbank.listeners.InventoryClose;
import org.ctp.xpbank.utils.Configurations;
import org.ctp.xpbank.utils.commands.XpCommand;

import net.milkbowl.vault.economy.Economy;

public class XpBank extends CrashAPIPlugin {

	private static Economy ECON = null;
	private static XpDatabase DB;
	private static XpBackup BACKUP;
	private static XpBank PLUGIN;
	private Configurations configurations;
	private PluginVersion pluginVersion;
	private List<InventoryData> inventories = new ArrayList<InventoryData>();
	private VersionCheck check;

	@Override
	public void onEnable() {
		PLUGIN = this;
		pluginVersion = new PluginVersion(this, new Version(getDescription().getVersion(), VersionType.UNKNOWN));
		if (!getDataFolder().exists()) getDataFolder().mkdirs();

		BACKUP = new XpBackup(this);
		BACKUP.load();

		configurations = Configurations.getConfigurations();
		configurations.onEnable();

		XpBankCommand c = new XpBankCommand();
		getCommand("XpBank").setExecutor(c);
		getCommand("XpBank").setTabCompleter(c);
		for(XpCommand s: XpBankCommand.getCommands()) {
			PluginCommand command = getCommand(s.getCommand());
			if (command != null) {
				command.setExecutor(c);
				command.setTabCompleter(c);
				command.setAliases(s.getAliases());
			} else
				Chatable.get().sendWarning("Couldn't find command '" + s.getCommand() + ".'");
		}
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new InventoryClick(), this);
		pm.registerEvents(new InventoryClose(), this);
		DB = new XpDatabase(this);
		DB.load();

		check = new VersionCheck(pluginVersion, "https://raw.githubusercontent.com/crashtheparty/XpBank/master/VersionHistory", "https://www.spigotmc.org/resources/xpbank.59580/", "https://github.com/crashtheparty/XpBank", getConfigurations().getConfig().getBoolean("get_latest_version"), false);
		getServer().getPluginManager().registerEvents(check, this);
		checkVersion();

	}

	public static XpBank getPlugin() {
		return PLUGIN;
	}

	public boolean hasVault() {
		if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) return false;
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) return false;
		ECON = rsp.getProvider();
		return ECON != null;
	}

	public static Economy getEconomy() {
		return ECON;
	}

	private void checkVersion() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(PLUGIN, check, 20l, 20 * 60 * 60 * 4l);
	}

	@Override
	public PluginVersion getPluginVersion() {
		return pluginVersion;
	}

	@Override
	public ChatUtils getChat() {
		return ChatUtils.getUtils(PLUGIN);
	}

	@Override
	public Configurations getConfigurations() {
		return configurations;
	}

	@Override
	public ItemSerialization getItemSerial() {
		return ItemSerialization.getItemSerial(PLUGIN);
	}

	@Override
	public YamlConfig getLanguageFile() {
		return configurations.getLanguageConfig().getConfig();
	}

	@Override
	public String getStarter() {
		return getLanguageFile().getString("starter");
	}

	public InventoryData getInventory(Player player) {
		for(InventoryData inv: inventories)
			if (inv.getPlayer().getUniqueId().equals(player.getUniqueId())) return inv;

		return null;
	}

	public boolean hasInventory(InventoryData inv) {
		return inventories.contains(inv);
	}

	public void addInventory(InventoryData inv) {
		inventories.add(inv);
	}

	public void removeInventory(InventoryData inv) {
		inventories.remove(inv);
	}

	public static XpDatabase getDB() {
		return DB;
	}

	public static XpBackup getBackup() {
		return BACKUP;
	}
}
