package org.ctp.xpbank.commands;

import java.util.*;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.ctp.xpbank.Chatable;
import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.utils.commands.CommandUtils;
import org.ctp.xpbank.utils.commands.XpCommand;
import org.ctp.xpbank.utils.commands.XpCommandCallable;

public class XpBankCommand implements CommandExecutor, TabCompleter {

	private static List<XpCommand> commands;
	private final XpCommand admin = new XpCommand(XpBank.getPlugin(), "xpadmin", "commands.aliases.xpadmin", "commands.descriptions.xpadmin", "commands.usage.xpadmin", "xpbank.admin");
	private final XpCommand open = new XpCommand(XpBank.getPlugin(), "xpopen", "commands.aliases.xpopen", "commands.descriptions.xpopen", "commands.usage.xpopen", "xpbank.open");
	private final XpCommand help = new XpCommand(XpBank.getPlugin(), "xphelp", "commands.aliases.xphelp", "commands.descriptions.xphelp", "commands.usage.xphelp", "xpbank.help");

	public XpBankCommand() {
		commands = new ArrayList<XpCommand>();
		commands.add(admin);
		commands.add(open);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		label = label.substring(label.indexOf(':') + 1);
		for(XpCommand c: commands) {
			String[] check;
			if (containsCommand(c, label)) {
				check = new String[args.length + 1];
				check[0] = label;
				for(int i = 0; i < args.length; i++)
					check[i + 1] = args[i];
				args = check;
				break;
			}
		}
		if (args.length == 0 || args.length == 1 && containsCommand(help, args[0])) return CommandUtils.printHelp(sender, 1);
		else if (args.length == 2 && containsCommand(help, args[0])) {
			int page = 0;
			try {
				page = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {

			}
			if (page > 0) return CommandUtils.printHelp(sender, page);
			else
				return CommandUtils.printHelp(sender, args[1]);
		}
		final String[] finalArgs = args;
		Player player = null;
		if (sender instanceof Player) player = (Player) sender;
		for(XpCommand command: commands) {
			if (command == help) continue;
			if (containsCommand(command, args[0])) try {
				return new XpCommandCallable(command, sender, finalArgs).call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%command%", args[0]);
		Chatable.get().sendMessage(sender, player, Chatable.get().getMessage(codes, "commands.no-command"), Level.WARNING);
		return true;
	}

	public static boolean containsCommand(XpCommand details, String s) {
		return s.equals(details.getCommand()) || details.getAliases().contains(s);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> all = new ArrayList<String>();

		label = label.substring(label.indexOf(':') + 1);
		for(XpCommand c: commands) {
			String[] check;
			if (containsCommand(c, label)) {
				check = new String[args.length + 1];
				check[0] = label;
				for(int i = 0; i < args.length; i++)
					check[i + 1] = args[i];
				args = check;
				break;
			}
		}
		int i = args.length - 1;

		if (i == 0) all.addAll(help(sender, args[i]));
		if (i > 0 && noArgCommands(0).contains(args[0])) return all;
		if (i == 1 && containsCommand(help, args[0]) && sender.hasPermission(help.getPermission())) all.addAll(help(sender, args[i]));
		if (i == 1 && containsCommand(admin, args[0]) && sender.hasPermission(admin.getPermission())) all.addAll(admin(args[i]));
		if (i == 2 && containsCommand(admin, args[0]) && sender.hasPermission(admin.getPermission())) all.addAll(admin(args[1], args[i]));
		if (i == 3 && containsCommand(admin, args[0]) && sender.hasPermission(admin.getPermission())) all.addAll(admin(args[1], args[2], args[i]));

		return all;
	}

	private Collection<String> admin(String startsWith) {
		List<String> strings = new ArrayList<String>();
		strings.addAll(Arrays.asList("add", "remove", "setpriceitem", "setprice", "toggleonetime", "setaccesstime", "togglevault"));
		return removeComplete(strings, startsWith);
	}

	private Collection<String> admin(String first, String startsWith) {
		List<String> strings = new ArrayList<String>();
		switch (first) {
			case "add":
			case "remove":
			case "setaccesstime":
				return integer();
			case "setpriceitem":
			case "toggleonetime":
			case "togglevault":
				return strings;
			case "setprice":
				return numDouble();
		}
		return removeComplete(strings, startsWith);
	}

	private Collection<String> admin(String first, String second, String startsWith) {
		List<String> strings = new ArrayList<String>();
		switch (first) {
			case "add":
			case "remove":
				return players(startsWith);
		}
		return removeComplete(strings, startsWith);
	}

	private List<String> removeComplete(List<String> strings, String startsWith) {
		Iterator<String> iter = strings.iterator();
		while (iter.hasNext()) {
			String entry = iter.next();
			boolean remove = true;
			if (entry.startsWith(startsWith)) remove = false;// is fine
			if (entry.indexOf('_') > -1) {
				String split = entry.substring(entry.indexOf('_') + 1);
				while (split.length() > 0) {
					if (split.startsWith(startsWith)) remove = false;// is fine
					if (split.indexOf('_') > -1) split = split.substring(split.indexOf('_') + 1);
					else
						split = "";
				}
			}
			if (remove) iter.remove();
		}
		return strings;
	}

	private List<String> integer() {
		List<String> strings = new ArrayList<String>();
		strings.add("[<int>]");
		return strings;
	}

	private List<String> numDouble() {
		List<String> strings = new ArrayList<String>();
		strings.add("[<double>]");
		return strings;
	}

	private List<String> players(String startsWith) {
		List<String> strings = new ArrayList<String>();
		for(Player player: Bukkit.getOnlinePlayers())
			strings.add(player.getName());
		return removeComplete(strings, startsWith);
	}

	private List<String> help(CommandSender sender, String startsWith) {
		List<String> strings = new ArrayList<String>();
		for(XpCommand command: commands)
			if (sender.hasPermission(command.getPermission())) {
				strings.add(command.getCommand());
				strings.addAll(command.getAliases());
			}
		return removeComplete(strings, startsWith);
	}

	private List<String> noArgCommands(int i) {
		List<String> strings = new ArrayList<String>();
		if (i == 0) {
			strings.add(open.getCommand());
			strings.addAll(open.getAliases());
		}
		return strings;
	}

	public static List<XpCommand> getCommands() {
		return commands;
	}
}
