package org.ctp.xpbank.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ctp.xpbank.XpBank;

public class ChatUtils {
	
	public static void sendMessage(Player player, String message){
		player.sendMessage(XpBank.getConfigUtils().getTranslatedStarter() + message);
	}
	
	public static void sendToConsole(String message) {
		Bukkit.getConsoleSender().sendMessage(XpBank.getConfigUtils().getTranslatedStarter() + message);
	}

}
