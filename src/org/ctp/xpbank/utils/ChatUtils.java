package org.ctp.xpbank.utils;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ctp.xpbank.XpBank;

public class ChatUtils {
	
	public static void sendMessage(Player player, String message){
		player.sendMessage(XpBank.getConfigUtils().getTranslatedStarter() + message);
	}

	public static void sendToConsole(Level level, String message) {
		XpBank.PLUGIN.getLogger().log(level, message);
	}
	
	public static void sendMessage(Player player, String message, String url) {
		Bukkit.getServer().dispatchCommand(
		        Bukkit.getConsoleSender(),
		        "tellraw " + player.getName() + 
		        " [{\"text\":\"" + XpBank.getConfigUtils().getTranslatedStarter() + message + "\"},{\"text\":\"" + url + "\", \"underlined\": true, \"clickEvent\":{\"action\":\"open_url\",\"value\":\"" +
		        url + "\"}}]");
	}

}
