package org.ctp.xpbank.utils.commands;

import org.ctp.crashapi.CrashAPIPlugin;
import org.ctp.crashapi.commands.CrashCommand;

public class XpCommand extends CrashCommand {

	public XpCommand(CrashAPIPlugin plugin, String command, String aliasesPath, String descriptionPath, String usagePath, String permission) {
		super(plugin, command, aliasesPath, descriptionPath, usagePath, permission);
	}

}
