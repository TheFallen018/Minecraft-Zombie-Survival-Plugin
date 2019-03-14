package com.TumbleweedMC.plugins.Zombies.Handler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.TumbleweedMC.plugins.Zombies.Zombies;
import com.TumbleweedMC.plugins.Zombies.Arena.Arena;
import com.TumbleweedMC.plugins.Zombies.Arena.ArenaManager;


public class EventListener implements Listener{
	
	static Zombies plugin;
	List<String> Join = new ArrayList<String>();
	
	public EventListener(Zombies plugin){
		EventListener.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		String uuid = player.getUniqueId().toString();
		if(!plugin.getCareerData().contains("Careers." + uuid)){
			plugin.getCareerData().createSection("Careers." + uuid);
			plugin.getCareerData().set("Careers." + uuid + ".Kills", 0);
			plugin.getCareerData().set("Careers." + uuid + ".Games", 0);
			plugin.getCareerData().set("Careers." + uuid + ".Deaths", 0);
			plugin.getCareerData().set("Careers." + uuid + ".Revives", 0);
			plugin.getCareerData().set("Careers." + uuid + ".TeamRevives", 0);
			plugin.getCareerData().set("Careers." + uuid + ".AverageWave", 0);
			plugin.getCareerData().set("Careers." + uuid + ".MaxKillStreak", 0);
			plugin.getCareerData().set("Careers." + uuid + ".MaxWave", 0);
			plugin.getCareerData().set("Careers." + uuid + ".FavoriteWeapon", "Unknown");
			plugin.saveFiles();
			plugin.sendConsole("[Zombies] Created new career data.");
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK){
			return;
		}
		
		if(event.getClickedBlock().getType() == Material.WALL_SIGN){
			
		
		Player player = event.getPlayer();
		if(Join.contains(player.getName())){
			Location loc = event.getClickedBlock().getLocation();
			plugin.getArenaData().set("Arenas.Signs.Join", serializeLoc(loc));
			plugin.saveFiles();
		}
		
		Sign sign = (Sign) event.getClickedBlock().getState();
		if(sign.getLine(0).equalsIgnoreCase(ChatColor.BOLD + "Zombies")){
			String s = sign.getLine(1);
			s = ChatColor.stripColor(s);
			if(!ArenaManager.getManager().arenaExists(s)){
				plugin.sendMessage(player, ChatColor.RED + "That arena doesn't exist!");
				return;
			}
			
			ArenaManager.getManager().addPlayer(event.getPlayer(), s);
			return;
		}
		
		if(!ArenaManager.getManager().isInGame(player)){
			return;
		}
		if(sign.getLine(0).equalsIgnoreCase(ChatColor.BOLD + "Juggernog")){
			if(!ArenaManager.getManager().isInGame(player)){
				return;
			}
			if(plugin.getEconomy().getBalance(player.getName()) < 2500.0D){
				plugin.sendMessage(player, "You don't have enough money for that perk!");
				return;
			}
			Arena a = ArenaManager.getManager().getPlayersArena(player);
			plugin.getEconomy().withdrawPlayer(player.getName(), 2500.0D);
			a.getJuggernog().add(player.getName());
			plugin.sendMessage(player, ChatColor.GREEN + "Juggernog perk added!");
			ArenaManager.getManager().updateScore(a, player);
			return;
			
		}
		
		if(sign.getLine(0).equalsIgnoreCase(ChatColor.BOLD + "Unlock Door")){
			if(!ArenaManager.getManager().isInGame(player)){
				plugin.sendMessage(player, "You can't use this sign unless you are in a game!");
				return;
			}
			
			double price = Double.valueOf(ChatColor.stripColor(sign.getLine(2)).replace("$", ""));
			if(plugin.getEconomy().getBalance(player.getName()) < price){
				plugin.sendMessage(player, "You don't have enough money to unlock that door!");
				return;
			}
			org.bukkit.material.Sign signMaterial = (org.bukkit.material.Sign) event.getClickedBlock().getState().getData();
			Block doorBlock = event.getClickedBlock().getRelative(signMaterial.getAttachedFace());
			if(doorBlock.getType() != Material.IRON_DOOR_BLOCK){
				plugin.debug(doorBlock.getType().toString());
				plugin.sendMessage(player, "not valid door");
				return;
			}
			BlockState state = doorBlock.getState();
			Openable door = (Openable) state.getData();
			if(door.isOpen()){
				plugin.sendMessage(player, "That door is already unlocked!");
				return;
			}
			plugin.getEconomy().withdrawPlayer(player.getName(), price);
			
			
			int condition = 0;
			if(sign.getLine(3).equals("")){
			}else{
				String line3 = sign.getLine(3);
				line3 = ChatColor.stripColor(line3).replace("$", "");
				line3 = line3.replace(" ", "").replace("Door", "");
				condition = Integer.valueOf(line3);
			}
			Arena a = ArenaManager.getManager().getPlayersArena(player);

			
			door.setOpen(true);
			state.setData((MaterialData) door);
			state.update();
			a.getDoors().add(state);
			plugin.sendMessage(player, ChatColor.GREEN + "You have unlocked a door!");

			if(doorBlock.getRelative(BlockFace.EAST).getType() == Material.IRON_DOOR_BLOCK){
			Block doorBlock2 = doorBlock.getRelative(BlockFace.EAST);
			BlockState state2 = doorBlock2.getState();
			Openable door2 = (Openable) state2.getData();
			if(!door2.isOpen())
			door2.setOpen(true);
			state2.setData((MaterialData) door2);
			state2.update();
			a.getDoors().add(state2);
			}
			if(doorBlock.getRelative(BlockFace.NORTH).getType() == Material.IRON_DOOR_BLOCK){
				Block doorBlock2 = doorBlock.getRelative(BlockFace.NORTH);
				BlockState state2 = doorBlock2.getState();
				Openable door2 = (Openable) state2.getData();
				if(!door2.isOpen())
				door2.setOpen(true);
				state2.setData((MaterialData) door2);
				state2.update();
				a.getDoors().add(state2);
				}
			if(doorBlock.getRelative(BlockFace.SOUTH).getType() == Material.IRON_DOOR_BLOCK){
				Block doorBlock2 = doorBlock.getRelative(BlockFace.SOUTH);
				BlockState state2 = doorBlock2.getState();
				Openable door2 = (Openable) state2.getData();
				if(!door2.isOpen())
				door2.setOpen(true);
				state2.setData((MaterialData) door2);
				state2.update();
				a.getDoors().add(state2);
				}
			if(doorBlock.getRelative(BlockFace.WEST).getType() == Material.IRON_DOOR_BLOCK){
				Block doorBlock2 = doorBlock.getRelative(BlockFace.WEST);
				BlockState state2 = doorBlock2.getState();
				Openable door2 = (Openable) state2.getData();
				if(!door2.isOpen())
				door2.setOpen(true);
				state2.setData((MaterialData) door2);
				state2.update();
				a.getDoors().add(state2);
				}
			if(condition != 0){
			List<String> ZombieSpawns = plugin.getArenaData().getStringList("Arenas." + a.getID() + ".ZombieSpawns.Condition_" + condition);
			List<String> doors = plugin.getArenaData().getStringList("Arenas." + a.getID() + ".Doors.Condition_" + condition);
			if(ZombieSpawns.isEmpty()){
				plugin.debug("Conditional Zombie spawns (" + condition + ") are empty for " + a.getID());
				return;
			}
			for(String s : ZombieSpawns){
				Location loc = ArenaManager.getManager().deserializeLoc(s);
				a.getZombieSpawns().add(loc);
			}
			for(String s : doors){
				Location loc = deserializeLoc(s);
				Block block = loc.getBlock();
				if(block.getType() == Material.IRON_DOOR_BLOCK){
					BlockState stateDoor = block.getState();
					Openable ConditionDoor = (Openable) stateDoor.getData();
					if(!ConditionDoor.isOpen()){
						ConditionDoor.setOpen(true);
						stateDoor.setData((MaterialData) ConditionDoor);
						stateDoor.update();
						a.getDoors().add(stateDoor);
					}else{
						plugin.debug("Door is already open!");
					}
						
					
				}else{
					plugin.debug("Condition door at " + s + " is not an IRON_DOOR_BLOCK!");
				}
			}
		}
	}
		
		if(sign.getLine(0).equalsIgnoreCase(ChatColor.BOLD + "Weapons")){
			if(!ArenaManager.getManager().isInGame(player)){
				plugin.sendMessage(player, "You can't use this sign unless you are in a game!");
				return;
			}
			String weaponName = sign.getLine(2);
			weaponName = ChatColor.stripColor(weaponName);
			
			String priceString = sign.getLine(3);
			priceString = ChatColor.stripColor(priceString).replace("$", "");
			double price = Double.valueOf(priceString);
			
			if(plugin.getEconomy().getBalance(player.getName()) < price){
				plugin.sendMessage(player, "You don't have enough money for that weapon!");
				return;
			}
			plugin.getEconomy().withdrawPlayer(player.getName(), price);
			ArenaManager.getManager().getWeapon(player, weaponName);
		}
		
		if(sign.getLine(0).equalsIgnoreCase(ChatColor.BOLD + "Speed-Cola")){
			if(!ArenaManager.getManager().isInGame(player)){
				plugin.sendMessage(player, "You can't use this sign unless you are in a game!");
				return;
			}
			double price = Double.valueOf(ChatColor.stripColor(sign.getLine(2)).replace("$", ""));
			if(plugin.getEconomy().getBalance(player.getName()) < price){
				plugin.sendMessage(player, "You don't have enough money for that weapon!");
				return;
			}
			plugin.getEconomy().withdrawPlayer(player.getName(), price);
			PotionEffect potioneffect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);
			potioneffect.apply(player);
			plugin.sendMessage(player, ChatColor.GREEN + "You have bought a Speed-Cola!");
		}
		

		
		Arena a = ArenaManager.getManager().getPlayersArena(player);
		ArenaManager.getManager().updateScore(a, player);
	}
		
	}
	
	@EventHandler
	public void onBlockDestroy(BlockBreakEvent event){
		if(event.getBlock().getType() == Material.WALL_SIGN){
			List<String> signs = plugin.getArenaData().getStringList("Arenas.Signs.Join");
			for(String s : signs){
				String loc = serializeLoc(event.getBlock().getLocation());
				if(s.equalsIgnoreCase(loc)){
					signs.remove(s);
					plugin.sendMessage(event.getPlayer(), ChatColor.GREEN + "You have just removed a join sign for Zombies!");
					plugin.getArenaData().set("Arenas.Signs.Join", signs);
					plugin.saveFiles();
					return;
				}
			}
			
		}
		
		if(event.getBlock().getType() == Material.CHEST){
			String s = serializeLoc(event.getBlock().getLocation());
			for(Arena a : ArenaManager.getManager().getArenas()){
				List<String> chests = plugin.getArenaData().getStringList("Arenas." + a.getID() + ".MysteryBoxes");
				if(chests.contains(s)){
					chests.remove(s);
					plugin.sendMessage(event.getPlayer(), "You have removed a mystery box!");
					plugin.getArenaData().set("Arenas." + a.getID() + ".MysteryBoxes", chests);
					plugin.saveFiles();
				}
				
			}
		}
	}
		@EventHandler
		public void SignChangeEvent(SignChangeEvent sign){
			if(!sign.getPlayer().hasPermission("Zombies.sign")){
				return;
			}
			Player player = sign.getPlayer();
			if(sign.getLine(0).equalsIgnoreCase("[Zombies]")){
				
				String s = sign.getLine(1);
				sign.setLine(0, ChatColor.BOLD + "Zombies");
				s = ChatColor.stripColor(s);
				if(!ArenaManager.getManager().arenaExists(s)){
					sign.getBlock().breakNaturally();
					plugin.sendMessage(player, "That arena doesn't exist!");
					return;
				}
				plugin.sendMessage(player, ChatColor.GREEN + "New join sign added!");
				Location loc = sign.getBlock().getLocation();
				List<String> signs = new ArrayList<String>();
				sign.setLine(1, ChatColor.DARK_BLUE + s);
				signs = plugin.getArenaData().getStringList("Arenas.Signs.Join");
				signs.add(serializeLoc(loc));
				plugin.getArenaData().set("Arenas.Signs.Join", signs);
				plugin.saveFiles();
				
			}
			if(sign.getLine(0).equalsIgnoreCase("[Door]")){
				if(!ArenaManager.getManager().arenaExists(sign.getLine(1))){
					plugin.sendMessage(player, "Please add an arena name on line 2");
					sign.getBlock().breakNaturally();
					return;
				}
				if(sign.getLine(2).equalsIgnoreCase("")){
					plugin.sendMessage(player, "Please add a condition number on line 3");
					sign.getBlock().breakNaturally();
					return;
				}
				org.bukkit.material.Sign signMaterial = (org.bukkit.material.Sign) sign.getBlock().getState().getData();
				Block doorBlock = sign.getBlock().getRelative(signMaterial.getAttachedFace());
				if(doorBlock.getType() != Material.IRON_DOOR_BLOCK){
					plugin.sendMessage(player, "Sign needs to be on an iron door!");
					sign.getBlock().breakNaturally();
					return;
				}
				Arena a = ArenaManager.getManager().getArena(sign.getLine(1));
				String s = a.getID();
				int condition = Integer.valueOf(sign.getLine(2));
				String loc = serializeLoc(doorBlock.getLocation());
				List<String> doors = plugin.getArenaData().getStringList("Arenas." + s + ".Doors.Condition_" + condition);
				doors.add(loc);
				plugin.sendMessage(player, ChatColor.GREEN + "Conditional door added!");
				sign.getBlock().breakNaturally();

				plugin.getArenaData().set("Arenas." + s + ".Doors.Condition_" + condition, doors);
				plugin.saveFiles();
			}
			
			if(sign.getLine(0).equalsIgnoreCase("[MysteryBox]")){
				String arenaName = ChatColor.stripColor(sign.getLine(1));
				if(!ArenaManager.getManager().arenaExists(arenaName)){
					plugin.sendMessage(player, "That isn't a valid arena name!");
					sign.getBlock().breakNaturally();
					return;
				}

				org.bukkit.material.Sign signMaterial = (org.bukkit.material.Sign) sign.getBlock().getState().getData();
				Block chestBlock = sign.getBlock().getRelative(signMaterial.getAttachedFace());
				String chestLoc = serializeLoc(chestBlock.getLocation());
				List<String> chests = plugin.getArenaData().getStringList("Arenas." + arenaName + ".MysteryBoxes");
				chests.add(chestLoc);
				plugin.getArenaData().set("Arenas." + arenaName + ".MysteryBoxes", chests);
				plugin.saveConfig();
				plugin.sendMessage(player, ChatColor.GREEN + "MysteryBox added!");
				sign.setLine(1, "");
				sign.setLine(0, ChatColor.BOLD + "Mystery Box");
			}
			
		}
		
		@EventHandler
		public void onCommand(PlayerCommandPreprocessEvent event){
			if(!ArenaManager.getManager().isInGame(event.getPlayer())){
				return;
			}
			if(event.getPlayer().hasPermission("zombies.bypass")){
				return;
			}
			List commands = plugin.getConfig().getStringList("AllowedCommands");
			String[] args = event.getMessage().toLowerCase().replace("/", "").split(" ");
			if(!commands.contains(args[0])){
				plugin.debug("Command sent = " + args[0]);
				plugin.sendMessage(event.getPlayer(), ChatColor.RED + "That command is blocked while you are in-game. please use " + ChatColor.AQUA + "/zombies leave" + ChatColor.RED + " to exit the current arena.");
				event.setCancelled(true);
			}
		}
		
	
	
	
	public List<String> getJoinList(){
		return this.Join;
	}

	public String serializeLoc(Location l){
		return l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
	}
	
	public Location deserializeLoc(String s){
		String[] st = s.split(",");
		return new Location(Bukkit.getWorld(st[0]), Integer.parseInt(st[1]), Integer.parseInt(st[2]), Integer.parseInt(st[3]));
	}

}
