package org.ctp.xpbank.utils.commands;

import org.bukkit.command.CommandSender;
import org.ctp.crashapi.commands.CrashCommandCallable;

public class XpCommandCallable implements CrashCommandCallable {

	private final XpCommand command;
	private final String[] args;
	private final CommandSender sender;

	public XpCommandCallable(XpCommand command, CommandSender sender, String[] args) {
		this.command = command;
		this.sender = sender;
		this.args = args;
	}

	@Override
	public XpCommand getCommand() {
		return command;
	}

	@Override
	public String[] getArgs() {
		return args;
	}

	@Override
	public CommandSender getSender() {
		return sender;
	}

	@Override
	public Boolean call() throws Exception {
		boolean run = false;
		try {
			run = fromCommand();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!run && sender.hasPermission(command.getPermission())) CommandUtils.printHelp(sender, command.getCommand());
		return true;
	}

	@Override
	public Boolean fromCommand() {
		switch (command.getCommand()) {
			case "xpadmin":
				return CommandUtils.admin(sender, command, args);
			case "xpopen":
				return CommandUtils.open(sender, command, args);
		}
		return null;
	}

}
