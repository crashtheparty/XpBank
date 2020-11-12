package org.ctp.xpbank.utils.config;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.ctp.crashapi.CrashAPIPlugin;
import org.ctp.crashapi.config.Configuration;
import org.ctp.crashapi.config.yaml.YamlConfigBackup;
import org.ctp.crashapi.db.BackupDB;
import org.ctp.crashapi.item.ItemSerialization;
import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.utils.Configurations;

public class MainConfiguration extends Configuration {

	public MainConfiguration(CrashAPIPlugin plugin, File dataFolder, BackupDB backup) {
		super(plugin, new File(dataFolder + "/config.yml"), backup, new String[] { "Xp Bank", "Plugin by", "crashtheparty" });

		migrateVersion();
	}

	@Override
	public void migrateVersion() {}

	@Override
	public void setDefaults() {
		if (Configurations.isInitializing()) getChat().sendInfo("Initializing default config...");

		YamlConfigBackup config = getConfig();

		config.getFromConfig();

		config.addDefault("vault", XpBank.getPlugin().hasVault(), new String[] { "Whether to use Vault for economy plugins" });
		config.addDefault("price", 1000, new String[] { "Vault price to open a bank" });
		config.addDefault("price_item", ItemSerialization.getItemSerial(getPlugin()).itemToString(new ItemStack(Material.DIAMOND, 4)), new String[] { "Non-vault price to open a bank" });
		config.addDefault("one_time", true, new String[] { "Whether each open costs money", "If not, plugin uses access time" });
		config.addDefault("access_time", 60, new String[] { "Time (in seconds) before player must pay to enter their bank again." });
		config.addDefault("get_latest_version", true, new String[] { "Check for the latest version of this plugin." });
		config.addDefault("language", "en_us", new String[] { "Default language for the language file" });
		config.addDefault("language_file", "language.yml", new String[] { "Default language file name" });
		config.addDefault("use_comments", true, new String[] { "See helpful comments in this file" });

		config.writeDefaults();

		if (Configurations.isInitializing()) getChat().sendInfo("Default config initialized!");
	}

}
