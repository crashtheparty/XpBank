package org.ctp.xpbank.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.utils.ChatUtils;
import org.ctp.xpbank.utils.ConfigUtils;

public class Admin implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		OfflinePlayer player = null;
		Player commandSender = null;
		if(sender instanceof Player){
			player = (Player) sender;
			commandSender = (Player) sender;
		}
		if(!sender.hasPermission("xpbank.admin")) {
			if(commandSender != null) {
				ChatUtils.sendMessage(commandSender, "You do not have permission to use this command.");
			} else {
				sender.sendMessage("You do not have permission to use this command.");
			}
			return false;
		}
		if(args.length > 0) {
			switch(args[0].toLowerCase()) {
				case "add":
					if(args.length > 1) {
						int num = getInt(args[1]);
						if(num == -1) {
							if(player != null) {
								ChatUtils.sendMessage(commandSender, "Not a valid number: " + args[1] + ".");
							} else {
								sender.sendMessage("Not a valid number: " + args[1] + ".");
							}
							return false;
						}
						
						String playerName = null;
						if(args.length > 2) {
							playerName = args[2];
						}
						player = getPlayer(sender, commandSender, playerName);
						if(player == null) return false;
						
						int experience = XpBank.db.getInteger("xpbank", player.getUniqueId().toString(), "xp") + num;
						XpBank.db.setInteger("xpbank", player.getUniqueId().toString(), "xp", experience);
						if(commandSender == null) {
							sender.sendMessage("Added " + num + " experience to " + player.getName() + "'s account.");
						} else {
							if(player.equals(commandSender)) {
								ChatUtils.sendMessage(commandSender, "Added " + num + " experience to your account.");
							} else {
								ChatUtils.sendMessage(commandSender, "Added " + num + " experience to " + player.getName() + "'s account.");
							}
						}
					} else {
						if(commandSender != null) {
							ChatUtils.sendMessage(commandSender, "Specify a number to add.");
						} else {
							sender.sendMessage("Specify a number to add.");
						}
						return false;
					}
					break;
				case "remove":
					if(args.length > 1) {
						int num = getInt(args[1]);
						if(num == -1) {
							if(player != null) {
								ChatUtils.sendMessage(commandSender, "Not a valid number: " + args[1] + ".");
							} else {
								sender.sendMessage("Not a valid number: " + args[1] + ".");
							}
							return false;
						}
						
						String playerName = null;
						if(args.length > 2) {
							playerName = args[2];
						}
						player = getPlayer(sender, commandSender, playerName);
						if(player == null) return false;
						
						int experience = XpBank.db.getInteger("xpbank", player.getUniqueId().toString(), "xp") - num;
						if(experience < 0) {
							experience = 0;
						}
						XpBank.db.setInteger("xpbank", player.getUniqueId().toString(), "xp", experience);
						if(commandSender == null) {
							sender.sendMessage("Removed " + num + " experience from " + player.getName() + "'s account.");
						} else {
							if(player.equals(commandSender)) {
								ChatUtils.sendMessage(commandSender, "Removed " + num + " experience from your account.");
							} else {
								ChatUtils.sendMessage(commandSender, "Removed " + num + " experience from " + player.getName() + "'s account.");
							}
						}
					} else {
						if(commandSender != null) {
							ChatUtils.sendMessage(commandSender, "Specify a number to remove.");
						} else {
							sender.sendMessage("Specify a number to remove.");
						}
						return false;
					}
					break;
				case "setpriceitem":
					if(commandSender != null) {
						ItemStack item = commandSender.getInventory().getItemInMainHand();
						if(item != null) {
							ConfigUtils utils = XpBank.getConfigUtils();
							utils.setPriceItem(item);
							ChatUtils.sendMessage(commandSender, "Set the item price to your hand.");
						} else {
							ChatUtils.sendMessage(commandSender, "You cannot set the item to null.");
						}
					} else {
						sender.sendMessage("Cannot run command from console.");
					}
					break;
				case "setprice":
					if(args.length > 1) {
						double num = getDouble(args[1]);
						if(num < 0) {
							if(player != null) {
								ChatUtils.sendMessage(commandSender, "Not a valid price: " + args[1] + ".");
							} else {
								sender.sendMessage("Not a valid price: " + args[1] + ".");
							}
							return false;
						}

						ConfigUtils utils = XpBank.getConfigUtils();
						utils.setPrice(num);
						if(commandSender == null) {
							sender.sendMessage("Set the price to " + num + ".");
						} else {
							ChatUtils.sendMessage(commandSender, "Set the price to " + num + ".");
						}
					} else {
						if(commandSender != null) {
							ChatUtils.sendMessage(commandSender, "You must specify a price.");
						} else {
							sender.sendMessage("You must specify a price.");
						}
						return false;
					}
					break;
				case "toggleonetime":
					if(true) {
						ConfigUtils utils = XpBank.getConfigUtils();
						boolean oneTime = !utils.isOneTime();
						utils.setOneTime(oneTime);
						if(commandSender != null) {
							ChatUtils.sendMessage(commandSender, "Toggled one time to " + oneTime + ".");
						} else {
							sender.sendMessage("Toggled one time to " + oneTime + ".");
						}
					}
					break;
				case "setaccesstime":
					if(args.length > 1) {
						int num = getInt(args[1]);
						if(num <= 0) {
							if(player != null) {
								ChatUtils.sendMessage(commandSender, "Not a valid access time: " + args[1] + ".");
							} else {
								sender.sendMessage("Not a valid access time: " + args[1] + ".");
							}
							return false;
						}

						ConfigUtils utils = XpBank.getConfigUtils();
						utils.setAccessTime(num);
						if(commandSender == null) {
							sender.sendMessage("Set the access time to " + num + ".");
						} else {
							ChatUtils.sendMessage(commandSender, "Set the access time to " + num + ".");
						}
					} else {
						if(commandSender != null) {
							ChatUtils.sendMessage(commandSender, "You must specify an access time.");
						} else {
							sender.sendMessage("You must specify an access time.");
						}
						return false;
					}
					break;
				case "togglevault":
					if(XpBank.getEconomy() != null) {
						ConfigUtils utils = XpBank.getConfigUtils();
						boolean vault = utils.usingEconomy();
						utils.setEconomy(!vault);
						if(commandSender != null) {
							ChatUtils.sendMessage(commandSender, "Toggled vault to " + vault + ".");
						} else {
							sender.sendMessage("Toggled vault to " + vault + ".");
						}
					} else {
						if(commandSender != null) {
							ChatUtils.sendMessage(commandSender, "No economy plugin found.");
						} else {
							sender.sendMessage("No economy plugin found.");
						}
					}
					break;
				default:
					if(commandSender != null) {
						ChatUtils.sendMessage(commandSender, "Not a valid subcommand: " + args[0] + ".");
					} else {
						sender.sendMessage("Not a valid subcommand: " + args[0] + ".");
					}
					break;
			}
		} else {
			if(commandSender != null) {
				ChatUtils.sendMessage(commandSender, "Specify a subcommand (add, remove).");
			} else {
				sender.sendMessage("Specify a subcommand (add, remove, setpriceitem, setprice, toggleonetime, setaccesstime, togglevault).");
			}
		}
		return false;
	}
	
	private int getInt(String arg) {
		int num = 0;
		try{
			num = Integer.parseInt(arg);
		}catch(NumberFormatException ex){
			return -1;
		}
		return num;
	}
	
	private double getDouble(String arg) {
		int num = 0;
		try{
			num = Integer.parseInt(arg);
		}catch(NumberFormatException ex){
			return -1;
		}
		return num;
	}
	
	@SuppressWarnings("deprecation")
	private OfflinePlayer getPlayer(CommandSender sender, Player commandSender, String playerName) {
		OfflinePlayer player = commandSender;
		if(player == null) {
			if(playerName != null){
				if(Bukkit.getOfflinePlayer(playerName) != null) {
					player = Bukkit.getOfflinePlayer(playerName);
				} else {
					sender.sendMessage("Not a valid player: " + playerName + ".");
				}
			} else {
				sender.sendMessage("You must specify a player.");
			}
		} else {
			if(playerName != null){
				if(Bukkit.getOfflinePlayer(playerName) != null) {
					player = Bukkit.getOfflinePlayer(playerName);
				} else {
					ChatUtils.sendMessage(commandSender, "Not a valid player: " + playerName + ".");
				}
			}
		}
		return player;
	}
}
