package com.TumbleweedMC.plugins.Zombies.Handler;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.TumbleweedMC.plugins.Zombies.Zombies;
import com.TumbleweedMC.plugins.Zombies.Arena.Arena;
import com.TumbleweedMC.plugins.Zombies.Arena.ArenaManager;
import com.TumbleweedMC.plugins.Zombies.Game.GameManager;

public class CommandListener implements CommandExecutor{
	
	public static Zombies plugin;
	
	public CommandListener(Zombies plugin){
		CommandListener.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Hold up there! You can't send these commands from the console, dude.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("zombies")){
			if (args.length == 0 || args[0].equalsIgnoreCase("help")){
				String message = "";
				if(player.hasPermission("zombies.player") && !player.hasPermission("zombies.admin")){

					message = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Help"  + ChatColor.AQUA + "]====" + "\n";
					message = message + ChatColor.GOLD + "/Zombies" + ChatColor.RESET + ":" + " " + "Root Command for Zombies. Shows help List" + "\n";
					message = message + ChatColor.GOLD + "/Zombies list" + ChatColor.RESET + ":" + " " + "Shows available arenas." + "\n";
					message = message + ChatColor.GOLD + "/Zombies join <Arena Name>" + ChatColor.RESET + ":" + " " + "Joins an arena!" + "\n";
					message = message + ChatColor.GOLD + "/Zombies leave" + ChatColor.RESET + ":" + " " + "Leave an arena!" + "\n";
				}
				
				if(player.hasPermission("Zombies.admin") && args.length == 1){
					message = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Help (Admin)"  + ChatColor.AQUA + "]====" + "\n";
					message = message + ChatColor.GOLD + "/Zombies" + ChatColor.RESET + ":" + " " + "Root Command for Zombies. Shows help List" + "\n";
					message = message + ChatColor.GOLD + "/Zombies list" + ChatColor.RESET + ":" + " " + "Shows available arenas." + "\n";
					message = message + ChatColor.GOLD + "/Zombies join <Arena Name>" + ChatColor.RESET + ":" + " " + "Joins an arena!" + "\n";
					message = message + ChatColor.GOLD + "/Zombies leave" + ChatColor.RESET + ":" + " " + "Leave an arena!" + "\n";
					message = message + ChatColor.GOLD + "/Zombies create <Arena Name>" + ChatColor.RESET + ":" + " " + "Creates an arena!" + "\n";
					message = message + ChatColor.GOLD + "/Zombies setspawn <Spawn Type> <Arena Name>" + ChatColor.RESET + ":" + " " + "Sets a spawn for an arena!" + "\n";
					message = message + ChatColor.GOLD + "/Zombies AddZombieSpawn <Arena Name> <Optional Condition Number>" + ChatColor.RESET + ":" + " " + "Adds a zombie spawn!" + "\n";
					message = message + ChatColor.GOLD + "/Zombies remove <Arena Name>" + ChatColor.RESET + ":" + " " + "Deletes an arena!" + "\n";
					message = message + ChatColor.GOLD + "/Zombies debug" + ChatColor.RESET + ":" + " " + "Enabled Console Debugging" + "\n";
					message = message + ChatColor.GOLD + "/Zombies help signs" + ChatColor.RESET + ":" + " " + "Help for creating arena Signs!" + "\n";
				}
				
				if(args.length > 1 && player.hasPermission("Zombies.admin")){
					if(!args[1].equalsIgnoreCase("signs")){
						message = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Help (Admin)"  + ChatColor.AQUA + "]====" + "\n";
						message = message + ChatColor.GOLD + "/Zombies" + ChatColor.RESET + ":" + " " + "Root Command for Zombies. Shows help List" + "\n";
						message = message + ChatColor.GOLD + "/Zombies list" + ChatColor.RESET + ":" + " " + "Shows available arenas." + "\n";
						message = message + ChatColor.GOLD + "/Zombies join <Arena Name>" + ChatColor.RESET + ":" + " " + "Joins an arena!" + "\n";
						message = message + ChatColor.GOLD + "/Zombies leave" + ChatColor.RESET + ":" + " " + "Leave an arena!" + "\n";
						message = message + ChatColor.GOLD + "/Zombies create <Arena Name>" + ChatColor.RESET + ":" + " " + "Creates an arena!" + "\n";
						message = message + ChatColor.GOLD + "/Zombies setspawn <Spawn Type> <Arena Name>" + ChatColor.RESET + ":" + " " + "Sets a spawn for an arena!" + "\n";
						message = message + ChatColor.GOLD + "/Zombies AddZombieSpawn <Arena Name> <Optional Condition Number>" + ChatColor.RESET + ":" + " " + "Adds a zombie spawn!" + "\n";
						message = message + ChatColor.GOLD + "/Zombies remove <Arena Name>" + ChatColor.RESET + ":" + " " + "Deletes an arena!" + "\n";
						message = message + ChatColor.GOLD + "/Zombies debug" + ChatColor.RESET + ":" + " " + "Enabled Console Debugging" + "\n";
						message = message + ChatColor.GOLD + "/Zombies help signs" + ChatColor.RESET + ":" + " " + "Help for creating arena Signs!" + "\n";
					}
					
					if(args.length == 2){
						message = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Help (Signs)"  + ChatColor.AQUA + "]====" + "\n";
						message = message + ChatColor.GOLD + "/Zombies help signs join" + ChatColor.RESET + ":" + " " + "Help for join signs" + "\n";
						message = message + ChatColor.GOLD + "/Zombies help signs MysteryBox" + ChatColor.RESET + ":" + " " + "Help for Mystery Box sign" + "\n";
						message = message + ChatColor.GOLD + "/Zombies help signs AddDoorSign" + ChatColor.RESET + ":" + " " + "Help for Add Door Sign sign" + "\n";
						message = message + ChatColor.GOLD + "/Zombies help signs UnlockDoorSign" + ChatColor.RESET + ":" + " " + "Help for Unlock Door sign" + "\n";
						message = message + ChatColor.GOLD + "/Zombies help signs Juggernog" + ChatColor.RESET + ":" + " " + "Help for Juggernog Sign" + "\n";
						message = message + ChatColor.GOLD + "/Zombies help signs Speed-Cola" + ChatColor.RESET + ":" + " " + "Help for Speed-Cola sign" + "\n";
						message = message + ChatColor.GOLD + "/Zombies help signs weapons" + ChatColor.RESET + ":" + " " + "Help for Weapon signs" + "\n";

						
					}
					if(args.length == 3 && args[2].equalsIgnoreCase("MysteryBox")){
						message = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Help (Mystery Box Sign)"  + ChatColor.AQUA + "]====" + "\n";
						message = message + ChatColor.GREEN + "Line 1:" + ChatColor.GRAY + "[MysteryBox]" + ChatColor.WHITE + "This will trigger the making of the mystery box." + "\n";
						message = message + ChatColor.GREEN + "Line 2:" + ChatColor.GRAY + "<ArenaName>" + ChatColor.WHITE + "This line must contain the name of the arena the box is for." + "\n";
						message = message + ChatColor.GREEN + "NOTE:" + ChatColor.WHITE + "Currently the mysterybox will always default to $950.00, but it is worlth putting the price on line for, for the sakes of looks." + "\n";


					}
					if(args.length == 3 && args[2].equalsIgnoreCase("AddDoorSign")){
						message = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Help (Add Door Sign)"  + ChatColor.AQUA + "]====" + "\n";
						message = message + ChatColor.GREEN + "Line 1:" + ChatColor.GRAY + "[Door]" + ChatColor.WHITE + "This will trigger the making of the Door." + "\n";
						message = message + ChatColor.GREEN + "Line 2:" + ChatColor.GRAY + "<ArenaName>" + ChatColor.WHITE + "This line must contain the name of the arena the box is for." + "\n";
						message = message + ChatColor.GREEN + "Line 3:" + ChatColor.GRAY + "<Condition Number>" + ChatColor.WHITE + "This line must have a condition. This way, it will unlock alongside other specified doors." + "\n";

					}
					if(args.length == 3 && args[2].equalsIgnoreCase("UnlockDoor")){
						message = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Help (Unlock Door Sign)"  + ChatColor.AQUA + "]====" + "\n";
						message = message + ChatColor.GREEN + "Line 1:" + ChatColor.GRAY + "&lUnlockDoor" + ChatColor.WHITE + "This will trigger the unlocking of the door" + "\n";
						message = message + ChatColor.GREEN + "Line 2:" + ChatColor.GRAY + "(Blank)" + ChatColor.WHITE + "Leave this line blank, or add in extra info." + "\n";
						message = message + ChatColor.GREEN + "Line 3:" + ChatColor.GRAY + "<$Price>" + ChatColor.WHITE + "This line sets the price of the door." + "\n";
						message = message + ChatColor.GREEN + "Line 4:" + ChatColor.GRAY + "<Door + ConditionNumber>" + ChatColor.WHITE + "This line sets the condition of the door, so it can trigger other events when unlocked!" + "\n";

					}
					
					if(args.length == 3 && args[2].equalsIgnoreCase("Juggernog")){
						message = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Help (Juggernog Sign)"  + ChatColor.AQUA + "]====" + "\n";
						message = message + ChatColor.GREEN + "Line 1:" + ChatColor.GRAY + "&lJuggernog" + ChatColor.WHITE + "This will trigger the adding of the perk" + "\n";
						message = message + ChatColor.GREEN + "Line 2:" + ChatColor.GRAY + "(Blank)" + ChatColor.WHITE + "Leave this line blank, or add in extra info." + "\n";
						message = message + ChatColor.GREEN + "Line 3:" + ChatColor.GRAY + "<$Price>" + ChatColor.WHITE + "This line sets the price of the perk." + "\n";
						message = message + ChatColor.GREEN + "Line 4:" + ChatColor.GRAY + "(Blank)" + ChatColor.WHITE + "Leave this line blank, or add in extra info." + "\n";

					}
					
					if(args.length == 3 && args[2].equalsIgnoreCase("Speed-Cola")){
						message = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Help (Speed-Cola Sign)"  + ChatColor.AQUA + "]====" + "\n";
						message = message + ChatColor.GREEN + "Line 1:" + ChatColor.GRAY + "&lSpeed-Cola" + ChatColor.WHITE + "This will trigger the making of the perk." + "\n";
						message = message + ChatColor.GREEN + "Line 2:" + ChatColor.GRAY + "(Blank)" + ChatColor.WHITE + "Leave this line blank, or add in extra info." + "\n";
						message = message + ChatColor.GREEN + "Line 3:" + ChatColor.GRAY + "<$Price>" + ChatColor.WHITE + "This line sets the price of the perk." + "\n";
						message = message + ChatColor.GREEN + "Line 4:" + ChatColor.GRAY + "(Blank)" + ChatColor.WHITE + "Leave this line blank, or add in extra info." + "\n";

					}
					
					if(args.length == 3 && args[2].equalsIgnoreCase("Weapons")){
						message = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Help (Weapon Sign)"  + ChatColor.AQUA + "]====" + "\n";
						message = message + ChatColor.GREEN + "Line 1:" + ChatColor.GRAY + "&lWeapons" + ChatColor.WHITE + "This will trigger the adding of the weapon." + "\n";
						message = message + ChatColor.GREEN + "Line 2:" + ChatColor.GRAY + "(Blank)" + ChatColor.WHITE + "Leave this line blank, or add in extra info." + "\n";
						message = message + ChatColor.GREEN + "Line 3:" + ChatColor.GRAY + "<WeaponName>" + ChatColor.WHITE + "Sets the weapon type. Currently only allows weapons also available in the mystery box." + "\n";
						message = message + ChatColor.GREEN + "Line 4:" + ChatColor.GRAY + "<$Price>" + ChatColor.WHITE + "This line sets the price of the door." + "\n";

					}
					
					if(args.length == 3 && args[2].equalsIgnoreCase("Join")){
						message = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Help (Join Sign)"  + ChatColor.AQUA + "]====" + "\n";
						message = message + ChatColor.GREEN + "Line 1:" + ChatColor.GRAY + "[Zombies]" + ChatColor.WHITE + "This will trigger the adding of the sign." + "\n";
						message = message + ChatColor.GREEN + "Line 2:" + ChatColor.GRAY + "<ArenaName>" + ChatColor.WHITE + "This line must contain the name of the arena the box is for." + "\n";
					}
					
				}
				
				plugin.sendMessage(player, message);
				return true;
			}
		}
		
		if(args[0].equalsIgnoreCase("create")){
			if(!player.hasPermission("Zombies.admin")){
				plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
				return true;
			}
			if(args.length != 2){
				plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
				return true;
			}
			List<Location> ZombieSpawns = new ArrayList<Location>();
			List<Location> MysteryBoxes = new ArrayList<Location>();
			ArenaManager.getManager().createArena(player.getLocation(), player.getLocation(), player.getLocation(), player.getLocation(), ZombieSpawns, MysteryBoxes, args[1]);
			plugin.sendMessage(player, ChatColor.GREEN + "Arena " + '"'+ args[1] + '"' + " created!");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("join")){
			if(!player.hasPermission("Zombies.player")){
				plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
				return true;
			}
			if(args.length != 2){
				plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help, type /Zombies for help");
				return true;
			}
			
			if(!ArenaManager.getManager().arenaExists(args[1])){
				plugin.sendMessage(player, ChatColor.RED + "Arena " + '"' + args[1] + '"' + " does not exist");
				return true;
			}
			
			ArenaManager.getManager().addPlayer(player, args[1]);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("leave")){
			if(!player.hasPermission("Zombies.player")){
				plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
				return true;
			}
			if (args.length != 1){
				plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
				return true;
			}
			
			ArenaManager.getManager().removePlayer(player);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("remove")){
			if(!player.hasPermission("Zombies.admin")){
				plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
				return true;
			}
			if (args.length != 2){
				plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
				return true;
			}
		
		if(!ArenaManager.getManager().arenaExists(args[1])){
			plugin.sendMessage(player, ChatColor.RED + "Arena " + '"' + args[1] + '"' + " does not exist");
			return true;
		}
		
		ArenaManager.getManager().removeArena(player, args[1]);
		return true;
		
		
		}
		
		if(args[0].equalsIgnoreCase("setspawn")){
			if(!player.hasPermission("Zombies.admin")){
				plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
				return true;
			}
			
			if (args.length != 3){
			plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
			return true;
		}
			if(!ArenaManager.getManager().arenaExists(args[2])){
				plugin.sendMessage(player, ChatColor.RED + "Arena " + '"' + args[2] + '"' + " does not exist");
				return true;
			}
			
			if(args[1].equalsIgnoreCase("lobby")){
				plugin.getArenaData().set("Arenas." + args[2] + ".lobby", ArenaManager.getManager().serializeLoc(player.getLocation()));
				plugin.saveFiles();
				ArenaManager.getManager().loadGames();
				plugin.sendMessage(player, ChatColor.GREEN + "Lobby set!");
				return true;
			}
			else if(args[1].equalsIgnoreCase("spawn")){
				plugin.getArenaData().set("Arenas." + args[2] + ".spawn", ArenaManager.getManager().serializeLoc(player.getLocation()));
				plugin.saveFiles();
				ArenaManager.getManager().loadGames();
				plugin.sendMessage(player, ChatColor.GREEN + "Spawn set!");
				return true;
			}
			else if(args[1].equalsIgnoreCase("spectator")){
				plugin.getArenaData().set("Arenas." + args[2] + ".spectator", ArenaManager.getManager().serializeLoc(player.getLocation()));
				plugin.saveFiles();
				ArenaManager.getManager().loadGames();
				plugin.sendMessage(player, ChatColor.GREEN + "Spectator set!");
				return true;
			}
			else if(args[1].equalsIgnoreCase("end")){
				plugin.getArenaData().set("Arenas." + args[2] + ".end", ArenaManager.getManager().serializeLoc(player.getLocation()));
				plugin.saveFiles();
				ArenaManager.getManager().loadGames();
				plugin.sendMessage(player, ChatColor.GREEN + "End set!");
				return true;
			}else{
				plugin.sendMessage(player, ChatColor.RED + "Please use on of the following spawn, lobby, spectator, end.");
			}
			
	}	
		
		if(args[0].equalsIgnoreCase("list")){
			if(!player.hasPermission("Zombies.player")){
				plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
				return true;
			}
			List<Arena> arenas = ArenaManager.getManager().getArenas();
			String arenaList = ChatColor.AQUA + "====[" + ChatColor.DARK_RED + "Zombies Arena List"  + ChatColor.AQUA + "]====" + "\n";

			for(Arena a : arenas){
				if(ArenaManager.getManager().isJoinable(a)){
					arenaList = arenaList + ChatColor.RESET + " , " + ChatColor.GREEN + a.getID();
				}
				else{
					arenaList = arenaList + ChatColor.RESET + " , " + ChatColor.RED + a.getID();
				}
			}
			arenaList = arenaList.replaceFirst(" , ", "");
			plugin.sendMessage(player, arenaList);
			return true;
		}
			if(args[0].equalsIgnoreCase("addzombiespawn")){
				if(!player.hasPermission("Zombies.admin")){
					plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
					return true;
				}
				if(args.length < 2 || args.length > 3){
					plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
					return true;
				}
				
				if (!ArenaManager.getManager().arenaExists(args[1])){
					plugin.sendMessage(player, ChatColor.RED + "Arena does not exist!");
					return true;
				}
				if (args.length == 3){
					ArenaManager.getManager().addZombieSpawn(player, args[1], args[2]);
				}else{
					ArenaManager.getManager().addZombieSpawn(player, args[1], "0");
				}
				
				return true;
				
				
			}
			
			
			if(args[0].equalsIgnoreCase("addMysteryBox")){
				if(!player.hasPermission("Zombies.admin")){
					plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
					return true;
				}
				if(args.length != 2){
					plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
					return true;
				}
				
				ArenaManager.getManager().addMysteryBox(player, args[1]);
				return true;
			}
			

			
			
			
	if(args[0].equalsIgnoreCase("nextwave")){
		if(!player.hasPermission("Zombies.admin")){
			plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
			return true;
		}
				if(args.length != 2){
					plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
					return true;
				
				}
				if(!ArenaManager.getManager().arenaExists(args[1])){
					plugin.sendMessage(player, "Arena doesn't exist!");
					return true;
				}
				Arena a = ArenaManager.getManager().getArena(args[1]);
				GameManager.getManager().killArenaZombies(a);
				plugin.sendMessage(player, "Next wave started!");
					
				
			}

		if(args[0].equalsIgnoreCase("debug")){
			if(!player.hasPermission("Zombies.admin")){
				plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
				return true;
			}
			if(plugin.getDebugEnabled()){
				plugin.setDebugEnabled(false);
				plugin.sendMessage(player, "Debugging disabled!");
			}else{
				plugin.setDebugEnabled(true);
				plugin.sendMessage(player, "Debugging enabled!");
			}
		}
		else if(cmd.getName().equalsIgnoreCase("zombies") && args.length <= 1){
			plugin.sendMessage(player, ChatColor.RED + "Invalid Arguments, type /Zombies for help");
			return true;
		}
		
		return false;
	}

}
