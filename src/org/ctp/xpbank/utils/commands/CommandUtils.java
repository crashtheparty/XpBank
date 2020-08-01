package org.ctp.xpbank.utils.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ctp.crashapi.commands.CrashCommand;
import org.ctp.crashapi.config.yaml.YamlConfig;
import org.ctp.crashapi.item.ItemSerialization;
import org.ctp.crashapi.item.MatData;
import org.ctp.crashapi.utils.ChatUtils;
import org.ctp.crashapi.utils.StringUtils;
import org.ctp.xpbank.Chatable;
import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.commands.XpBankCommand;
import org.ctp.xpbank.inventories.ExperienceInventory;
import org.ctp.xpbank.threads.OpenTime;
import org.ctp.xpbank.utils.DBUtils;
import org.ctp.xpbank.utils.config.MainConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.milkbowl.vault.economy.Economy;

public class CommandUtils {

	private static HashMap<String, OpenTime> ACCESS = new HashMap<String, OpenTime>();

	public static boolean open(CommandSender sender, CrashCommand details, String[] args) {
		if (sender instanceof Player) {
			MainConfiguration utils = XpBank.getPlugin().getConfigurations().getConfig();
			Player player = (Player) sender;
			if (!sender.hasPermission(details.getPermission())) {
				Chatable.get().sendMessage(sender, player, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.no_permission"), Level.WARNING);
				return false;
			}
			player.closeInventory();
			if (!openedForFree(player)) {
				if (utils.getConfig().getBoolean("one_time")) return oneTime(player);
				else
					return timedAccess(player);
			} else {
				ExperienceInventory inv = new ExperienceInventory(player);
				XpBank.getPlugin().addInventory(inv);
				inv.setInventory();
				return true;
			}
		}
		return false;
	}

	private static boolean openedForFree(Player player) {
		MainConfiguration utils = XpBank.getPlugin().getConfigurations().getConfig();
		if (utils.getBoolean("vault") && XpBank.getPlugin().hasVault() && utils.getDouble("price") <= 0 || MatData.isAir(ItemSerialization.getItemSerial(XpBank.getPlugin()).stringToItem(utils.getString("price_item")).getType())) {
			Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "open.account.open"));
			return true;
		}
		if (player.hasPermission("xpbank.free")) {
			Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "open.account.free"));
			return true;
		}
		return false;
	}

	private static boolean oneTime(Player player) {
		MainConfiguration utils = XpBank.getPlugin().getConfigurations().getConfig();
		if (utils.getBoolean("vault") && XpBank.getPlugin().hasVault()) {
			Economy econ = XpBank.getEconomy();
			double price = utils.getDouble("price");
			HashMap<String, Object> codes = ChatUtils.getCodes();
			codes.put("%price%", price);
			if (econ.getBalance(player) >= price) {
				econ.withdrawPlayer(player, price);
				Chatable.get().sendMessage(player, Chatable.get().getMessage(codes, "open.paid_account.vault"));
				ExperienceInventory inv = new ExperienceInventory(player);
				XpBank.getPlugin().addInventory(inv);
				inv.setInventory();
			} else
				Chatable.get().sendMessage(player, Chatable.get().getMessage(codes, "open.invalid_funds.vault"));
		} else {
			int reward = 0;
			ItemStack item = XpBank.getPlugin().getItemSerial().stringToItem(utils.getString("price_item"));
			HashMap<String, Object> codes = ChatUtils.getCodes();
			codes.put("%price%", item.toString().replace(ChatColor.COLOR_CHAR, '&'));
			for(int j = 1; j <= 64; j++) {
				ItemStack rewardItem = new ItemStack(item.getType(), j);
				rewardItem.setItemMeta(item.getItemMeta());
				if (player.getInventory().contains(rewardItem)) reward += j;
			}
			if (reward >= item.getAmount()) {
				player.getInventory().removeItem(item);
				Chatable.get().sendMessage(player, Chatable.get().getMessage(codes, "open.paid_account.item"));
				ExperienceInventory inv = new ExperienceInventory(player);
				XpBank.getPlugin().addInventory(inv);
				inv.setInventory();
			} else
				Chatable.get().sendMessage(player, Chatable.get().getMessage(codes, "open.invalid_funds.item"));
		}
		return true;
	}

	private static boolean timedAccess(Player player) {
		MainConfiguration utils = XpBank.getPlugin().getConfigurations().getConfig();
		if (ACCESS.containsKey(player.getUniqueId().toString())) {
			OpenTime access = ACCESS.get(player.getUniqueId().toString());
			Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "open.account_lock.open"));
			HashMap<String, Object> codes = ChatUtils.getCodes();
			codes.put("%num%", access.getRunTime());
			Chatable.get().sendMessage(player, Chatable.get().getMessage(codes, "open.account_lock.continue_lock"));
			ExperienceInventory inv = new ExperienceInventory(player);
			XpBank.getPlugin().addInventory(inv);
			inv.setInventory();
		}
		if (utils.getBoolean("vault") && XpBank.getPlugin().hasVault()) {
			double price = utils.getDouble("price");
			Economy econ = XpBank.getEconomy();
			if (econ.getBalance(player) >= price) {
				OpenTime access = new OpenTime(player.getUniqueId());
				int scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(XpBank.getPlugin(), access, 20l, 20l);
				access.setScheduler(scheduler);
				ACCESS.put(player.getUniqueId().toString(), access);
				econ.withdrawPlayer(player, price);
				HashMap<String, Object> priceCodes = ChatUtils.getCodes();
				HashMap<String, Object> accessCodes = ChatUtils.getCodes();
				priceCodes.put("%price%", price);
				accessCodes.put("%num%", utils.getInt("access_time"));
				Chatable.get().sendMessage(player, Chatable.get().getMessage(priceCodes, "open.paid_account.vault"));
				Chatable.get().sendMessage(player, Chatable.get().getMessage(accessCodes, "open.account_lock.start_lock"));
				ExperienceInventory inv = new ExperienceInventory(player);
				XpBank.getPlugin().addInventory(inv);
				inv.setInventory();
			} else {
				HashMap<String, Object> priceCodes = ChatUtils.getCodes();
				priceCodes.put("%price%", price);
				Chatable.get().sendMessage(player, Chatable.get().getMessage(priceCodes, "open.invalid_funds.vault"));
			}
		} else {
			int reward = 0;
			ItemStack item = XpBank.getPlugin().getItemSerial().stringToItem(utils.getString("price_item"));
			for(int j = 1; j <= 64; j++) {
				ItemStack rewardItem = new ItemStack(item.getType(), j);
				rewardItem.setItemMeta(item.getItemMeta());
				if (player.getInventory().contains(rewardItem)) reward += j;
			}
			HashMap<String, Object> codes = ChatUtils.getCodes();
			codes.put("%price%", item.toString().replace(ChatColor.COLOR_CHAR, '&'));
			if (reward >= item.getAmount()) {
				OpenTime access = new OpenTime(player.getUniqueId());
				int scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(XpBank.getPlugin(), access, 20l, 20l);
				access.setScheduler(scheduler);
				ACCESS.put(player.getUniqueId().toString(), access);
				player.getInventory().removeItem(item);
				Chatable.get().sendMessage(player, Chatable.get().getMessage(codes, "open.paid_account.item"));
				ExperienceInventory inv = new ExperienceInventory(player);
				XpBank.getPlugin().addInventory(inv);
				inv.setInventory();
			} else
				Chatable.get().sendMessage(player, Chatable.get().getMessage(codes, "open.invalid_funds.item"));
		}
		return true;
	}

	public static void revokeAccess(OpenTime access) {
		ACCESS.remove(access.getPlayer().toString());
		Bukkit.getScheduler().cancelTask(access.getScheduler());
		Player player = Bukkit.getServer().getPlayer(access.getPlayer());
		if (player != null) Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "open.account_lock.end_lock"));
	}

	public static boolean admin(CommandSender sender, CrashCommand details, String[] args) {
		OfflinePlayer player = null;
		Player commandSender = null;
		if (sender instanceof Player) {
			player = (Player) sender;
			commandSender = (Player) sender;
		}
		if (!sender.hasPermission(details.getPermission())) {
			Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.no_permission"), Level.WARNING);
			return false;
		}
		if (args.length > 0) switch (args[0].toLowerCase()) {
			case "add":
				if (args.length > 1) {
					int num = getInt(args[1]);
					if (num == -1) {
						HashMap<String, Object> codes = ChatUtils.getCodes();
						codes.put("%num%", args[1]);
						Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(codes, "commands.invalid_number"), Level.WARNING);
						return false;
					}

					String playerName = null;
					if (args.length > 2) playerName = args[2];
					player = getPlayer(sender, commandSender, playerName);
					if (player == null) return false;

					int experience = DBUtils.getExperience(player);
					DBUtils.setExperience(player, experience);
					HashMap<String, Object> codes = ChatUtils.getCodes();
					codes.put("%num%", experience);
					if (player.equals(commandSender)) Chatable.get().sendMessage(commandSender, Chatable.get().getMessage(codes, "admin.account.add_self"));
					else {
						codes.put("%player%", player.getName());
						Chatable.get().sendMessage(commandSender, Chatable.get().getMessage(codes, "admin.account.add_other"));
					}
				} else {
					Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.specify_number"), Level.WARNING);
					return false;
				}
				break;
			case "remove":
				if (args.length > 1) {
					int num = getInt(args[1]);
					if (num == -1) {
						HashMap<String, Object> codes = ChatUtils.getCodes();
						codes.put("%num%", args[1]);
						Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(codes, "commands.invalid_number"), Level.WARNING);
						return false;
					}

					String playerName = null;
					if (args.length > 2) playerName = args[2];
					player = getPlayer(sender, commandSender, playerName);
					if (player == null) return false;

					int experience = DBUtils.getExperience(player) - num;
					DBUtils.setExperience(player, experience);
					HashMap<String, Object> codes = ChatUtils.getCodes();
					codes.put("%num%", experience);
					if (player.equals(commandSender)) Chatable.get().sendMessage(commandSender, Chatable.get().getMessage(codes, "admin.account.take_self"));
					else {
						codes.put("%player%", player.getName());
						Chatable.get().sendMessage(commandSender, Chatable.get().getMessage(codes, "admin.account.take_other"));
					}
				} else {
					Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.specify_number"), Level.WARNING);
					return false;
				}
				break;
			case "setpriceitem":
				if (commandSender != null) {
					ItemStack item = commandSender.getInventory().getItemInMainHand();
					if (item != null) {
						YamlConfig config = XpBank.getPlugin().getConfigurations().getConfig().getConfig();
						config.set("price_item", XpBank.getPlugin().getItemSerial().itemToString(item));
						XpBank.getPlugin().getConfigurations().save();
						Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(ChatUtils.getCodes(), "admin.price.item_set"), Level.INFO);
					} else
						Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(ChatUtils.getCodes(), "admin.price.item_set_null"), Level.WARNING);
				} else
					Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.no_console"), Level.WARNING);
				break;
			case "setprice":
				if (args.length > 1) {
					double num = getDouble(args[1]);
					HashMap<String, Object> codes = ChatUtils.getCodes();
					codes.put("%price%", args[1]);
					if (num < 0) {
						Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(codes, "admin.price.invalid_price"), Level.WARNING);
						return false;
					}
					codes.put("%price%", num);
					YamlConfig config = XpBank.getPlugin().getConfigurations().getConfig().getConfig();
					config.set("price", num);
					XpBank.getPlugin().getConfigurations().save();
					Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(codes, "admin.price.set"), Level.INFO);
				} else {
					Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(ChatUtils.getCodes(), "admin.price.specify_price"), Level.WARNING);
					return false;
				}
				break;
			case "toggleonetime":
				if (true) {
					YamlConfig config = XpBank.getPlugin().getConfigurations().getConfig().getConfig();
					boolean oneTime = !config.getBoolean("one_time");
					config.set("one_time", oneTime);
					XpBank.getPlugin().getConfigurations().save();
					HashMap<String, Object> codes = ChatUtils.getCodes();
					codes.put("%time%", oneTime);
					Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(codes, "admin.one_time_toggle"), Level.INFO);
				}
				break;
			case "setaccesstime":
				if (args.length > 1) {
					int num = getInt(args[1]);
					if (num <= 0) {
						HashMap<String, Object> codes = ChatUtils.getCodes();
						codes.put("%time%", args[1]);
						Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(codes, "admin.timed_access.invalid"), Level.WARNING);
						return false;
					}
					YamlConfig config = XpBank.getPlugin().getConfigurations().getConfig().getConfig();
					config.set("access_time", num);
					XpBank.getPlugin().getConfigurations().save();
					HashMap<String, Object> codes = ChatUtils.getCodes();
					codes.put("%time%", num);
					Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(codes, "admin.timed_access.set"), Level.INFO);
				} else {
					Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(ChatUtils.getCodes(), "admin.timed_access.specify"), Level.WARNING);
					return false;
				}
				break;
			case "togglevault":
				if (XpBank.getEconomy() != null) {
					YamlConfig config = XpBank.getPlugin().getConfigurations().getConfig().getConfig();
					boolean vault = !config.getBoolean("vault");
					config.set("vault", vault);
					XpBank.getPlugin().getConfigurations().save();
					HashMap<String, Object> codes = ChatUtils.getCodes();
					codes.put("%vault%", vault);
					Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(codes, "admin.vault.toggle"), Level.INFO);
				} else
					Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(ChatUtils.getCodes(), "admin.vault.no_economy"), Level.WARNING);
				break;
			default:
				HashMap<String, Object> codes = ChatUtils.getCodes();
				codes.put("%string%", args[0]);
				Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(codes, "commands.invalid_subcommand"), Level.WARNING);
				break;
		}
		else
			Chatable.get().sendMessage(sender, commandSender, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.specify_subcommand"), Level.WARNING);
		return false;
	}

	private static int getInt(String arg) {
		int num = 0;
		try {
			num = Integer.parseInt(arg);
		} catch (NumberFormatException ex) {
			return -1;
		}
		return num;
	}

	private static double getDouble(String arg) {
		int num = 0;
		try {
			num = Integer.parseInt(arg);
		} catch (NumberFormatException ex) {
			return -1;
		}
		return num;
	}

	@SuppressWarnings("deprecation")
	private static OfflinePlayer getPlayer(CommandSender sender, Player commandSender, String playerName) {
		OfflinePlayer player = commandSender;
		if (player == null) {
			if (playerName != null) {
				if (Bukkit.getOfflinePlayer(playerName) != null) player = Bukkit.getOfflinePlayer(playerName);
				else {
					HashMap<String, Object> codes = ChatUtils.getCodes();
					codes.put("%player%", playerName);
					sender.sendMessage(Chatable.get().getMessage(codes, "commands.invalid_player"));
				}
			} else
				sender.sendMessage(Chatable.get().getMessage(ChatUtils.getCodes(), "commands.specify_player"));
		} else if (playerName != null) if (Bukkit.getOfflinePlayer(playerName) != null) player = Bukkit.getOfflinePlayer(playerName);
		else {
			HashMap<String, Object> codes = ChatUtils.getCodes();
			codes.put("%player%", playerName);
			sender.sendMessage(Chatable.get().getMessage(codes, "commands.invalid_player"));
		}
		return player;
	}

	public static boolean printHelp(CommandSender sender, String label) {
		Player player = null;
		if (sender instanceof Player) player = (Player) sender;
		for(XpCommand command: XpBankCommand.getCommands())
			if (sender.hasPermission(command.getPermission()) && XpBankCommand.containsCommand(command, label)) {
				Chatable.get().sendMessage(sender, player, StringUtils.decodeString("\n" + command.getFullUsage()), Level.INFO);
				return true;
			}
		return printHelp(sender, 1);
	}

	@SuppressWarnings("unchecked")
	public static boolean printHelp(CommandSender sender, int page) {
		Player player = null;
		if (sender instanceof Player) player = (Player) sender;

		List<XpCommand> playerCommands = new ArrayList<XpCommand>();
		for(XpCommand command: XpBankCommand.getCommands())
			if (sender.hasPermission(command.getPermission())) playerCommands.add(command);

		if ((page - 1) * 5 > playerCommands.size()) return printHelp(sender, page - 1);

		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%page%", page);
		String commandsPage = Chatable.get().getMessage(codes, "commands.help.commands_page");
		if (player != null) {
			JSONArray json = new JSONArray();
			JSONObject first = new JSONObject();
			first.put("text", "\n" + ChatColor.DARK_BLUE + "******");
			JSONObject second = new JSONObject();
			if (page > 1) {
				second.put("text", ChatColor.GREEN + "<<<");
				HashMap<Object, Object> action = new HashMap<Object, Object>();
				action.put("action", "run_command");
				action.put("value", "/es help " + (page - 1));
				second.put("clickEvent", action);
			} else
				second.put("text", ChatColor.DARK_BLUE + "***");
			JSONObject third = new JSONObject();
			third.put("text", ChatColor.DARK_BLUE + "****** " + commandsPage + ChatColor.DARK_BLUE + " ******");
			JSONObject fourth = new JSONObject();
			if (playerCommands.size() > page * 5) {
				fourth.put("text", ChatColor.GREEN + ">>>");
				HashMap<Object, Object> action = new HashMap<Object, Object>();
				action.put("action", "run_command");
				action.put("value", "/es help " + (page + 1));
				fourth.put("clickEvent", action);
			} else
				fourth.put("text", ChatColor.DARK_BLUE + "***");
			JSONObject fifth = new JSONObject();
			fifth.put("text", ChatColor.DARK_BLUE + "******" + "\n");
			json.add(first);
			json.add(second);
			json.add(third);
			json.add(fourth);
			json.add(fifth);
			for(int i = 0; i < 5; i++) {
				int num = i + (page - 1) * 5;
				if (num >= playerCommands.size()) break;
				CrashCommand c = playerCommands.get(num);
				JSONObject name = new JSONObject();
				JSONObject desc = new JSONObject();
				JSONObject action = new JSONObject();
				action.put("action", "run_command");
				action.put("value", "/es help " + c.getCommand());
				name.put("text", ChatColor.GOLD + c.getCommand());
				name.put("clickEvent", action);
				json.add(name);
				HashMap<String, Object> descCodes = new HashMap<String, Object>();
				descCodes.put("%description%", Chatable.get().getMessage(ChatUtils.getCodes(), c.getDescriptionPath()));
				desc.put("text", shrink(Chatable.get().getMessage(descCodes, "commands.help.commands_info_shrink")) + "\n");
				json.add(desc);
			}
			json.add(first);
			json.add(second);
			json.add(third);
			json.add(fourth);
			json.add(fifth);
			Chatable.get().sendRawMessage(player, json.toJSONString());
		} else {
			String message = "\n" + ChatColor.DARK_BLUE + "******" + (page > 1 ? "<<<" : "***") + "****** " + commandsPage + ChatColor.DARK_BLUE + " ******" + (playerCommands.size() > page * 5 ? ">>>" : "***") + "******" + "\n";
			for(int i = 0; i < 5; i++) {
				int num = i + (page - 1) * 5;
				if (num >= playerCommands.size()) break;
				CrashCommand c = playerCommands.get(num);
				HashMap<String, Object> descCodes = new HashMap<String, Object>();
				descCodes.put("%command%", c.getCommand());
				descCodes.put("%description%", Chatable.get().getMessage(ChatUtils.getCodes(), c.getDescriptionPath()));
				message += shrink(Chatable.get().getMessage(descCodes, "commands.help.commands_info_shrink")) + "\n";
			}
			message += "\n" + ChatColor.DARK_BLUE + "******" + (page > 1 ? "<<<" : "***") + "****** " + commandsPage + ChatColor.DARK_BLUE + " ******" + (playerCommands.size() > page * 5 ? ">>>" : "***") + "******" + "\n";
			Chatable.get().sendToConsole(Level.INFO, message);
		}

		return true;
	}

	private static String shrink(String s) {
		if (s.length() > 60) return s.substring(0, 58) + "...";
		return s;
	}
}
