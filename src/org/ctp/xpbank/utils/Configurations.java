package org.ctp.xpbank.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.ctp.crashapi.config.CrashConfigurations;
import org.ctp.crashapi.config.Language;
import org.ctp.crashapi.db.BackupDB;
import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.utils.config.LanguageConfiguration;
import org.ctp.xpbank.utils.config.MainConfiguration;
import org.ctp.xpbank.utils.config.XpLanguageFile;

public class Configurations implements CrashConfigurations {

	private static boolean INITIALIZING = true;
	private final static Configurations CONFIGURATIONS = new Configurations();
	private MainConfiguration CONFIG;
	private LanguageConfiguration LANGUAGE;

	private List<XpLanguageFile> LANGUAGE_FILES = new ArrayList<XpLanguageFile>();

	private Configurations() {

	}

	public static Configurations getConfigurations() {
		return CONFIGURATIONS;
	}

	@Override
	public void onEnable() {
		File dataFolder = XpBank.getPlugin().getDataFolder();

		try {
			if (!dataFolder.exists()) dataFolder.mkdirs();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		BackupDB db = XpBank.getBackup();

		CONFIG = new MainConfiguration(XpBank.getPlugin(), dataFolder, db);

		String languageFile = CONFIG.getString("language_file");
		Language lang = Language.getLanguage(CONFIG.getString("language"));
		if (!lang.getLocale().equals(CONFIG.getString("language"))) CONFIG.updatePath("language", lang.getLocale());

		File languages = new File(dataFolder + "/language");

		if (!languages.exists()) languages.mkdirs();

		LANGUAGE_FILES.add(new XpLanguageFile(dataFolder, Language.US));

		for(XpLanguageFile file: LANGUAGE_FILES)
			if (file.getLanguage() == lang) LANGUAGE = new LanguageConfiguration(dataFolder, languageFile, file, db);

		if (LANGUAGE == null) LANGUAGE = new LanguageConfiguration(dataFolder, languageFile, LANGUAGE_FILES.get(0), db);
		INITIALIZING = false;
		save();
	}

	@Override
	public void save() {
		save(null);
	}

	public void save(CommandSender sender) {
		CONFIG.setComments(CONFIG.getBoolean("use_comments"));
		LANGUAGE.setComments(CONFIG.getBoolean("use_comments"));
		CONFIG.save();
		LANGUAGE.save();
	}

	public void reload(CommandSender sender) {
		CONFIG.reload();
		LANGUAGE.reload();

		save(sender);
	}

	public MainConfiguration getConfig() {
		return CONFIG;
	}

	public LanguageConfiguration getLanguageConfig() {
		return LANGUAGE;
	}

	public static boolean isInitializing() {
		return INITIALIZING;
	}

}
