package org.ctp.xpbank;

import org.ctp.crashapi.utils.ChatUtils;

public interface Chatable {

	default ChatUtils getChat() {
		return XpBank.getPlugin().getChat();
	}

	public static ChatUtils get() {
		return XpBank.getPlugin().getChat();
	}
	
}
