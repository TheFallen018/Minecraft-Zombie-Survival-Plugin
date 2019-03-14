package com.TumbleweedMC.plugins.Zombies.Arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Openable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.TumbleweedMC.plugins.Zombies.Zombies;

public class ArenaManager {
	
	static Zombies plugin;
	public ArenaManager(Zombies plugin){
		ArenaManager.plugin = plugin;
	}
	
	public Map<String, Location> locs = new HashMap<String, Location>();
	
	private static ArenaManager am;
	List<String> allowedTeleport = new ArrayList<String>();
	public Map<String, Double> econ = new HashMap<String, Double>();
	public Map<String, ItemStack[]> inv = new HashMap<String, ItemStack[]>();
	public Map<String, ItemStack[]> armor = new HashMap<String, ItemStack[]>();
	public Map<String, Float> exp = new HashMap<String, Float>();
	public Map<String, GameMode> gamemode = new HashMap<String, GameMode>();
	public List<ItemStack> weapons = new ArrayList<ItemStack>();
	public List<String> gunNames = new ArrayList<String>();
	public Map<String, ItemStack> guns = new HashMap<String, ItemStack>();
	public Map<String, ItemStack> ammunition = new HashMap<String, ItemStack>();
	public List<Sign> JoinSigns = new ArrayList<Sign>();
	
	//list arenas
	List<Arena> arenas = new ArrayList<Arena>();
	int arenaSize = 0;
	
	public ArenaManager(){
		
	}
	
	public static ArenaManager getManager(){
		if (am == null){
			am = new ArenaManager();
		}
		return am;
	}
	
	public Arena getArena(String s){
		for(Arena a: arenas){
			if(a.getID().equalsIgnoreCase(s)){
				return a;
			}
		}
		return null;
	}
	
	public List<Arena> getArenas(){
		return this.arenas;
	}
	
	public void addPlayer(Player player, String s){
		Arena a = getArena(s);		
		if(a == null){
			plugin.sendMessage(player, ChatColor.RED + "Invalid Arena!");
			return;
		}
		if(a.spawn == null){
			Bukkit.getConsoleSender().sendMessage("Spawn invalid!");
			return;
		}
		if(ArenaManager.getManager().isInGame(player)){
			plugin.sendMessage(player, ChatColor.RED + "You are already in-game. please use " + ChatColor.AQUA + "/zombies leave" + ChatColor.RED + " to exit the current arena.");
			return;
		}
		if(a.getWaves() > 5){
			plugin.sendMessage(player, "You can no longer join this arena!");
			return;
		}
		if(a.getPlayers().size() > 5){
			plugin.sendMessage(player, "You can no longer join this arena!");
			return;
		}
		allowedTeleport.add(player.getName());
		player.teleport(a.lobby);

		player.setHealth(20.0);
		player.setFoodLevel(20);
		gamemode.put(player.getName(), player.getGameMode());
		a.getPlayers().add(player.getName());	
		a.getInGamePlayers().add(player.getName());
		inv.put(player.getName(), player.getInventory().getContents());
		armor.put(player.getName(), player.getInventory().getArmorContents());
		exp.put(player.getName(), player.getExp());
		econ.put(player.getName(), plugin.getEconomy().getBalance(player.getName()));
		for(PotionEffect effect : player.getActivePotionEffects()){
			player.removePotionEffect(effect.getType());
		}
		double bal = plugin.getEconomy().getBalance(player.getName());
		plugin.getEconomy().withdrawPlayer(player.getName(), bal);
		plugin.getEconomy().depositPlayer(player.getName(), 500.0D);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setExp(0);
		
		locs.put(player.getName(), player.getLocation());
		
		player.setGameMode(GameMode.SURVIVAL);
		if(a.getWaves() == 0 && a.getPlayers().size() == 1){
			player.teleport(a.lobby);
			startGame(a);
			}else if(a.getWaves() != 0 && a.getPlayers().size() > 1){
				player.teleport(a.spawn);
				manageScoreboard(a, player);
				addKit(player);
				sendArenaMessage(a, ChatColor.GREEN + player.getName() + " has joined the arena!");
			}else if(a.getWaves() == 0 && a.getPlayers().size() > 1){
				player.teleport(a.lobby);
			}
		
	
		
		updateSigns();
		plugin.sendMessage(player, ChatColor.GREEN + "You have joined arena " + s);
	}
	
	
	
	public boolean allowTeleport(String playerName){
		if(allowedTeleport.contains(playerName)){
			return true;
			
		}
		
		return false;
	}
	
	
	public void spectatePlayer(Player player){
		Arena a = null;
		
		for(Arena arena : arenas){
			if (arena.getPlayers().contains(player.getName())){
				a = arena;
			}
		}
			if(a == null || !a.getPlayers().contains(player.getName())){
				return;
			}
			if(a.getPlayers().size() - a.getSpectators().size() <= 1){
				endGame(a);
				
			}
			a.getSpectators().add(player.getName());
			a.getInGamePlayers().remove(player.getName());
			player.setAllowFlight(true);
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.setHealth(20.0);
			player.setFireTicks(0);
			player.setGameMode(GameMode.CREATIVE);
			player.hidePlayer(player);
			
			player.teleport(a.spectator);
			plugin.sendMessage(player, ChatColor.YELLOW + "You are now spectating!");
			
		}
	
	
	
	public void sendArenaMessage(Arena a, String message){
		for(String playerName : a.getPlayers()){
			Player player = Bukkit.getPlayer(playerName);
			player.sendMessage(message);
		}
	}
	
	public void removePlayer(Player player){
		
		if(!isInGame(player)){
			plugin.debug("Player not in arena!");
			return;
		}
		
		Arena a = getPlayersArena(player);
		
		if(a == null || !a.getPlayers().contains(player.getName())){
			plugin.sendMessage(player, ChatColor.RED + "Invalid Operation!");
			return;
		}
		
		if(isSpectating(player)){
			a.getSpectators().remove(player.getName());
		}else{
			a.getInGamePlayers().remove(player.getName());
		}
		if(a.getJuggernog().contains(player.getName())){
			a.getJuggernog().remove(player.getName());
		}
		if(a.getAwaitingRevive().contains(player.getName())){
			a.getAwaitingRevive().remove(player.getName());
		}
		if(a.getDeadPlayers().contains(player.getName())){
			a.getDeadPlayers().remove(player.getName());
		}
		for(Player otherPlayer : Bukkit.getOnlinePlayers()){
			otherPlayer.showPlayer(player);
		}
		if(a.getSigns().containsKey(player.getName())){
			a.getSigns().get(player.getName()).getBlock().setType(Material.AIR);
			a.getSigns().remove(player.getName());
		}
		writeCareers(player);
		BarAPI.removeBar(player);
		a.getPlayers().remove(player.getName());
		sendArenaMessage(a, ChatColor.RED + player.getName() + " has left the arena!");
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setExp(0);
		player.setLevel(0);
		for(PotionEffect effect : player.getActivePotionEffects()){
			player.removePotionEffect(effect.getType());
		}
		plugin.getEconomy().withdrawPlayer(player.getName(), plugin.getEconomy().getBalance(player.getName()));
		plugin.getEconomy().depositPlayer(player.getName(), econ.get(player.getName()));
		player.getInventory().setContents(inv.get(player.getName()));
		player.getInventory().setArmorContents(armor.get(player.getName()));
		player.setExp(exp.get(player.getName()));
		player.setGameMode(gamemode.get(player.getName()));
		
		inv.remove(player.getName());
		armor.remove(player.getName());
		gamemode.remove(player.getName());
		removeScoreboard(player, a);
		player.setFireTicks(0);
		player.teleport(a.end);
		locs.remove(player.getName());
		updateSigns();
		if(a.getPlayers().isEmpty()){
			endGame(a);
			
		}
	}
	
	public void writeCareers(Player player){
		if(!isInGame(player)){
			plugin.debug("Player not ingame. Writing career data failed.");
			return;
		}
		Arena a = getPlayersArena(player);
		String uuid = player.getUniqueId().toString();
		
		int kills = plugin.getCareerData().getInt("Careers." + uuid + ".Kills");
		int gamesPlayed = plugin.getCareerData().getInt("Careers." + uuid + ".Games");
		int deaths = plugin.getCareerData().getInt("Careers." + uuid + ".Deaths");
		int revives = plugin.getCareerData().getInt("Careers." + uuid + ".Revives");
		int teamRevives = plugin.getCareerData().getInt("Careers." + uuid + ".TeamRevives");
		int averageWave = 0;
		
		
		if(a.getKills().containsKey(player.getName()))
		kills = a.getKills().get(player.getName()) + plugin.getCareerData().getInt("Careers." + uuid + ".Kills");
		gamesPlayed = gamesPlayed += 1;
		if(a.getCareer().containsKey(player.getName() + "deaths"))
		deaths = Integer.valueOf(a.getCareer().get(player.getName() + "deaths")) + plugin.getCareerData().getInt("Careers." + uuid + ".Deaths");
		if(a.getCareer().containsKey(player.getName() + "revives"))
		revives = Integer.valueOf(a.getCareer().get(player.getName() + "revives")) + plugin.getCareerData().getInt("Careers." + uuid + ".Revives");
		if(a.getCareer().containsKey(player.getName() + "teamRevives"))
		teamRevives = Integer.valueOf(a.getCareer().get(player.getName() + "teamRevives")) + plugin.getCareerData().getInt("Careers." + uuid + ".TeamRevives");
		if(plugin.getCareerData().getInt("Careers." + uuid + ".AverageWave") == 0){
			averageWave = a.getWaves();
		}else{
		averageWave = (((plugin.getCareerData().getInt("Careers." + uuid + ".AverageWave") * plugin.getCareerData().getInt("Careers." + uuid + ".Games")) + a.getWaves()) / plugin.getCareerData().getInt("Careers." + uuid + ".Games"));
		}
		if(plugin.getCareerData().getInt("Careers." + uuid + ".MaxWave") < a.getWaves()){
			plugin.getCareerData().set("Careers." + uuid + ".MaxWave", a.getWaves());
		}
		if(a.getKills().containsKey(player.getName())){
		if(a.getKills().get(player.getName()) > plugin.getCareerData().getInt("Careers." + uuid + ".MaxKills")){
			plugin.getCareerData().set("Careers." + uuid + ".MaxKills", a.getKills().get(player.getName()));
		}
	}
		plugin.getCareerData().set("Careers." + uuid + ".Kills", kills);
		plugin.getCareerData().set("Careers." + uuid + ".Games", gamesPlayed);
		plugin.getCareerData().set("Careers." + uuid + ".Deaths", deaths);
		plugin.getCareerData().set("Careers." + uuid + ".Revives", revives);
		plugin.getCareerData().set("Careers." + uuid + ".TeamRevives", teamRevives);
		plugin.getCareerData().set("Careers." + uuid + ".AverageWave", averageWave);
		
		plugin.saveFiles();
		a.getKills().remove(player.getName());
		
		
	}
	
	
	@SuppressWarnings("deprecation")
	public void manageScoreboard(Arena a, Player player){
	
		Scoreboard board = a.getScoreBoard();
		Objective objective = a.getObjective();
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Points");
		Score score = objective.getScore(player);
		score.setScore((int) (plugin.getEconomy().getBalance(player.getName())));
		player.setScoreboard(board);
		a.objective = objective;
		a.board = board;
		
	
}
	@SuppressWarnings("deprecation")
	public void updateScore(Arena a, Player player){
		if(!isInGame(player)){
			plugin.debug("Failed to update scoreboard, player not in-game");
			return;
		}
		Scoreboard board = a.getScoreBoard();
		Objective objective = a.getObjective();
		Score score = objective.getScore(player);
		score.setScore((int) (plugin.getEconomy().getBalance(player.getName())));
		player.setScoreboard(board);
	}
	
	@SuppressWarnings("deprecation")
	public void removeScoreboard(Player player, Arena a){
		Scoreboard board = a.getScoreBoard();
		board.resetScores(player);
		a.board = board;
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}
	
	public void endGame(Arena a){
		for(LivingEntity zombie : a.Zombies){
			zombie.remove();
		}
		
		for(String playerName : a.getPlayers()){
			Player player = Bukkit.getServer().getPlayer(playerName);
			

			if(isSpectating(player)){
				a.getSpectators().remove(player.getName());
			}else{
				a.getInGamePlayers().remove(player.getName());
			}
			if(a.getJuggernog().contains(player.getName())){
				a.getJuggernog().remove(player.getName());
			}
			if(a.getAwaitingRevive().contains(player.getName())){
				a.getAwaitingRevive().remove(player.getName());
			}
			if(a.getDeadPlayers().contains(player.getName())){
				a.getDeadPlayers().remove(player.getName());
			}
			for(Player otherPlayer : Bukkit.getOnlinePlayers()){
				otherPlayer.showPlayer(player);
			}
			if(a.getSigns().containsKey(player.getName())){
				a.getSigns().get(player.getName()).getBlock().setType(Material.AIR);
				a.getSigns().remove(player.getName());
			}
			writeCareers(player);
			BarAPI.removeBar(player);
			player.setHealth(20.0);
			player.setFoodLevel(20);
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.setExp(0);
			player.setLevel(0);
			for(PotionEffect effect : player.getActivePotionEffects()){
				player.removePotionEffect(effect.getType());
			}
			double bal = plugin.getEconomy().getBalance(player.getName());
			plugin.getEconomy().withdrawPlayer(player.getName(), bal);
			plugin.getEconomy().depositPlayer(player.getName(), econ.get(player.getName()));
			player.getInventory().setContents(inv.get(player.getName()));
			player.getInventory().setArmorContents(armor.get(player.getName()));
			player.setExp(exp.get(player.getName()));
			player.setGameMode(gamemode.get(player.getName()));
			
			inv.remove(player.getName());
			armor.remove(player.getName());
			gamemode.remove(player.getName());
			removeScoreboard(player, a);
			player.setFireTicks(0);
			player.teleport(a.end);
			locs.remove(player.getName());
			updateSigns();
			
		}

		for(BlockState bs : a.getDoors()){
			Openable door = (Openable) bs.getData();
			door.setOpen(false);
			bs.update();
		}
		List<String> zombieSpawnString = plugin.getArenaData().getStringList("Arenas." + a.getID() + ".ZombieSpawns.nonConditional");
		a.ZombieSpawns.clear();
		for(String s : zombieSpawnString){
			Location loc = deserializeLoc(s);
			a.ZombieSpawns.add(loc);
		}
		a.ZombiesLeft = 0;
		a.waveCount = 0;
		a.waveZombies = 4;
		a.zombieHealth = 5;
		a.Zombies.clear();
		a.players.clear();
		a.spectators.clear();
		a.timer = 6;
		a.speed = 0.18F;
		a.Juggernog.clear();
		a.doors.clear();
		a.awatingRevive.clear();
		a.dead.clear();
		
	}
	
	public void endAllGames(){
		for(Arena a : arenas){
			endGame(a);
		}
	}
	
	public void removeAllPlayers(){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(isInGame(player)){
				for(Arena arena : arenas){
					if(arena.getPlayers().contains(player.getName())){
						plugin.sendConsole("Removing " + player.getName() + " from " + arena.getID());
						removePlayer(player);
					}
				}
				
			}
		}
	}
	
	public void addZombieSpawn(Player player, String s, String conditionString){
		
		if(!arenaExists(s)){
			plugin.sendMessage(player, ChatColor.RED + "Arena does not exist!");
			return;
		}
		List<String> ZombieSpawnList = new ArrayList<String>();
		int condition = Integer.valueOf(conditionString);
		if(condition == 0){
		ZombieSpawnList = plugin.getArenaData().getStringList("Arenas." + s + ".ZombieSpawns.nonConditional");
		}else{
		ZombieSpawnList = plugin.getArenaData().getStringList("Arenas." + s + ".ZombieSpawns.Condition_" + condition);
		}
		String location = serializeLoc(player.getLocation());
		ZombieSpawnList.add(location);
		if(condition == 0){
		plugin.getArenaData().set("Arenas." + s + ".ZombieSpawns.nonConditional", ZombieSpawnList);
		}else{
		plugin.getArenaData().set("Arenas." + s + ".ZombieSpawns.Condition_" + condition, ZombieSpawnList);
		}
		
		plugin.saveFiles();
		
		plugin.sendMessage(player, ChatColor.GREEN + "You have added a new Zombie Spawn!");
		
		
	}
	
	public void addMysteryBox(Player player, String s){
		Location chestLoc = new Location(player.getLocation().getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		if(chestLoc.getBlock().getType() != Material.CHEST){
		plugin.sendMessage(player, ChatColor.RED + "You need to be standing on a chest!");
		return;
		}
		if(!arenaExists(s)){
			plugin.sendMessage(player, ChatColor.RED + "Arena does not exists!");
			return;
		}
		
		String location = serializeLoc(chestLoc);
		List<String> locationList = new ArrayList<String>();
		locationList.add(location);
		plugin.getArenaData().set("Arenas." + s + ".MysteryBoxes", locationList);
		plugin.saveFiles();
		
		plugin.sendMessage(player, ChatColor.GREEN + "You have added a MysteryBox!");
	}
	
	public Arena reloadArena(Location spawn, Location lobby, Location spectator, Location end, List<Location> ZombieSpawns, List<Location> MysteryBoxes, String s){
		
		Arena a = new Arena(spawn, lobby, spectator, end, ZombieSpawns, MysteryBoxes, s);
		arenas.add(a);
		
		return a;
		
	}
	
	public void removeArena(Player player, String s){
		Arena a = getArena(s);
		if(a == null){
			plugin.sendMessage(player, ChatColor.RED + "Arena does not exist!");
			return;
		}
		
		arenas.remove(a);
		plugin.getArenaData().set("Arenas." + s, null);
		List<String> list = plugin.getArenaData().getStringList("Lists.Arenas");
		list.remove(s);
		
		plugin.sendMessage(player, ChatColor.YELLOW + s + " removed!");
		plugin.getArenaData().set("Lists.Arenas", list);
		
		plugin.saveFiles();
		
		
	}

	public void magicTime(final Arena a){
		new BukkitRunnable() {
			
			public void run() {
				if(a.isInstakill()){
				sendArenaMessage(a, ChatColor.RED + "Instakill has worn off!");
				a.instakill = false;
				}
				else if(a.isDoubleScore()){
					sendArenaMessage(a, ChatColor.RED + "Double Score has worn off!");
					a.doubleScore = false;
				}
			}
		}.runTaskLater(plugin, 600L);
	}
	
	public Arena createArena(Location spawn, Location lobby, Location spectator, Location end, List<Location> ZombieSpawns, List<Location> MysteryBoxes, String s){
		Arena a = new Arena(spawn, lobby, spectator, end, ZombieSpawns, MysteryBoxes, s);
		arenas.add(a);
		
		plugin.getArenaData().set("Arenas." + s + ".spawn", serializeLoc(spawn));
		plugin.getArenaData().set("Arenas." + s + ".lobby", serializeLoc(lobby));
		plugin.getArenaData().set("Arenas." + s + ".spectator", serializeLoc(spectator));
		plugin.getArenaData().set("Arenas." + s + ".end", serializeLoc(end));
		plugin.getArenaData().createSection("Arenas." + s + ".ZombieSpawns");
		plugin.getArenaData().createSection("Arenas." + s + ".ZombieSpawns.nonConditional");
		plugin.getArenaData().createSection("Arenas." + s + ".MysteryBoxes");
		
		List<String> list = plugin.getArenaData().getStringList("Lists.Arenas");
		list.add(s);
		plugin.getArenaData().set("Lists.Arenas", list);
		plugin.saveFiles();
		loadGames();
		
		return a;
	}
	
	public boolean isInGame(Player player){
		for(Arena a : arenas){
			if(a.getPlayers().contains(player.getName())){
				return true;
			}
		}
		return false;
	}
	
	public Arena getPlayersArena(Player player){
		for(Arena a : arenas){
			if(a.getPlayers().contains(player.getName())){
				return a;
			}
		}
		return null;
	}
	
	public boolean entityIsInGame(Entity zombie){
		for(Arena a : arenas){
			if(a.getZombies().contains(zombie)){
				return true;
			}
		}
		return false;
	}
	
	public Arena getZombiesArena(Entity zombie){
		for(Arena a : arenas){
			if (a.getZombies().contains(zombie)){
				return a;
			}
		}
		return null;
	}
	
	public boolean isSpectating(Player player){
		for(Arena a : arenas){
			if(a.getPlayers().contains(player.getName()) && a.getSpectators().contains(player.getName())){
				return true;
			}
		}
		return false;
	}
	
	public boolean arenaExists(String s){
		List<String> list = plugin.getArenaData().getStringList("Lists.Arenas");
		if(list.contains(s)){
			return true;
		}
		
		return false;
	}
	
	public void loadGames(){
		if(plugin.getArenaData().getStringList("Lists.Arenas").isEmpty()){
			return;
		}
		
		for(String s : plugin.getArenaData().getStringList("Lists.Arenas")){
			Location spawn = deserializeLoc(plugin.getArenaData().getString("Arenas." + s + ".spawn"));
			Location lobby = deserializeLoc(plugin.getArenaData().getString("Arenas." + s + ".lobby"));
			Location spectator = deserializeLoc(plugin.getArenaData().getString("Arenas." + s + ".spectator"));
			Location end = deserializeLoc(plugin.getArenaData().getString("Arenas." + s + ".end"));
			List<String> ZombieList = plugin.getArenaData().getStringList("Arenas." + s + ".ZombieSpawns.nonConditional");
			List<String> MysteryBoxList = plugin.getArenaData().getStringList("Arenas." + s + ".MysteryBoxes");
			List<Location> ZombieSpawns = new ArrayList<Location>();
			List<Location> MysteryBoxLocs = new ArrayList<Location>();
			for (String z : ZombieList){
				Location loc = deserializeLoc(z);
				ZombieSpawns.add(loc);
			}
			for(String m : MysteryBoxList){
				Location loc = plugin.getEventListener().deserializeLoc(m);
				MysteryBoxLocs.add(loc);
			}
			Arena a = reloadArena(spawn, lobby, spectator, end, ZombieSpawns, MysteryBoxLocs, s);
			a.id = s;	
			a.spawn = spawn;
			a.lobby = lobby;
			a.spectator = spectator;
			a.end = end;
			a.ZombieSpawns = ZombieSpawns;
			a.MysteryBoxes = MysteryBoxLocs;
		}
	}
	
	public void loadSigns(){
		List<String> SignString = plugin.getArenaData().getStringList("Arenas.Signs.Join");
		
		for(String s : SignString){
			Location loc = plugin.getEventListener().deserializeLoc(s);
			if(loc.getBlock().getType() == Material.WALL_SIGN){
				Sign sign = (Sign) loc.getBlock().getState();
				JoinSigns.add(sign);
			}
		}
	}
	
	
	
	public void updateSigns(){
		for(Sign sign : JoinSigns){
			if(sign.getLine(0).equals(ChatColor.BOLD + "Zombies")){
				String arenaName = sign.getLine(1);
				arenaName = ChatColor.stripColor(arenaName);
				if(!arenaExists(arenaName)){
					plugin.debug("Join sign at " + sign.getLocation().toString() + " has an invalid arena name!");
					return;
				}
				Arena a = getArena(arenaName);
				if(a.getWaves() <= 5 && a.getPlayers().size() < 6){
					sign.setLine(2, ChatColor.GREEN + "Joinable!");
				}else{
					sign.setLine(2, ChatColor.RED + "Joinable!");
				}
				sign.setLine(3, ChatColor.AQUA + Integer.toString(a.getPlayers().size()) + " / 6");
				sign.update();
			}
		}
	}
	
	public boolean isJoinable(Arena a){
		if(a.getWaves() > 5){
			return false;
		}
		if(a.getPlayers().size() > 5){
			return false;
		}
		return true;
	}
	
	public void startGame(final Arena a){
		
		new BukkitRunnable() {
			
			public void run() {
				if(a.getPlayers().isEmpty()){
					this.cancel();
					return;
				}
				
				int level = a.getTimer();
				level -= 1;
				a.timer = level;
				if(level > 0 && level < 4){
					for(String s : a.getPlayers()){
						Player player = Bukkit.getPlayer(s);
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 2, 0);
					}
				}
				if (level == 1){
					for(String s : a.getPlayers()){
						Player player = Bukkit.getServer().getPlayer(s);
						
						player.teleport(a.spawn);
						manageScoreboard(a, player);
						player.sendMessage(ChatColor.GREEN + "Arena started!");
						addKit(player);
						player.setExp(0);
					}
					plugin.startWaves(a);
					this.cancel();
					return;
				}
				for(String s : a.getPlayers()){
					Player player = Bukkit.getServer().getPlayer(s);
					
					player.setLevel(level);
				}
				
			}
		}.runTaskTimer(plugin, 1L, 20L);
			
		}
	
	public void addKit(Player player){
		ItemStack kit = new ItemStack(Material.WOOD_SPADE);
		ItemMeta meta = kit.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "M1911"  + ChatColor.YELLOW + " ▪ «" + 8 + "»");
		List<String> lore = new ArrayList<String>();
		lore.add("Low power pistol ");
		lore.add("Poor weapon overall.");
		meta.setLore(lore);
		kit.setItemMeta(meta);
		
		ItemStack ammo = new ItemStack(Material.INK_SACK, 32);
		ItemMeta ammoMeta = ammo.getItemMeta();
		ammoMeta.setDisplayName(ChatColor.YELLOW + "M1911 Ammunition");
		ammo.setItemMeta(ammoMeta);
		
		
		player.getInventory().addItem(kit);
		player.getInventory().addItem(ammo);
	}
	
	public void addRandomWeapons(Block block){
		if (block.getType() != Material.CHEST){
			return;
		}
		if (getGunNames().isEmpty()){
			fillWeaponsList();
		}
		
		if (getGuns().isEmpty()){
			plugin.debug("Guns list is empty!");
			return;
		}
		
		Random randomizer = new Random();
		int random = randomizer.nextInt(gunNames.size());
		String weaponName = getGunNames().get(random);
		ItemStack weapon = getGuns().get(weaponName);
		
		
		Chest chest = (Chest) block.getState();
		
		if(weapon == null){
			return;
		}
		chest.getInventory().clear();
		chest.getInventory().addItem(weapon);
		int count = plugin.getConfig().getInt("MysteryBox.guns." + weaponName + ".Ammo.Count");
		ItemStack ammo = getAmmunition().get(weaponName);
		ammo.setAmount(count);
		if(!plugin.getConfig().getString("MysteryBox.guns." + weaponName + ".Ammo.Material").equalsIgnoreCase("air")){
		plugin.debug(ChatColor.stripColor(getAmmunition().get(weaponName).getItemMeta().getDisplayName()).replace(" ammunition", "") + " Ammunition count =" + ammo.getAmount());
		}else{
			plugin.debug("No Ammo for Grenade!");
		}
		chest.getInventory().addItem(ammo);
		chest.update();
		
		
		
		
	}
	
	@SuppressWarnings("deprecation")
	public void getWeapon(Player player, String weaponName){
		plugin.debug(weaponName);
		ItemStack weapon = getGuns().get(weaponName);
		ItemStack ammo = getAmmunition().get(weaponName);
		if(weapon == null){
			plugin.debug("weapon is null!");
			return;
		}
		if(ammo == null){
			plugin.debug("Ammo is null!");
			return;
		}
		plugin.debug(weapon.toString());
		player.getInventory().addItem(weapon);
		player.getInventory().addItem(ammo);
		player.updateInventory();
		plugin.sendMessage(player, ChatColor.GREEN + "You have purchased the " + weaponName);
		
		
	}
	
	public void fillWeaponsList(){
		this.gunNames = plugin.getConfig().getStringList("MysteryBox.WeaponList");
		for(String s : getGunNames()){
			ItemStack gun = new ItemStack(Material.getMaterial(plugin.getConfig().getString("MysteryBox.guns." + s + ".Material")));
			if(plugin.getConfig().contains("MysteryBox.guns." + s + ".Count")){
				int count = plugin.getConfig().getInt("MysteryBox.guns." + s + ".Count");
				gun = new ItemStack(Material.getMaterial(plugin.getConfig().getString("MysteryBox.guns." + s + ".Material")), count);
			}
			ItemMeta meta = gun.getItemMeta();
			
			String startingAmmo = plugin.getConfig().getString("MysteryBox.guns." + s + ".StartingAmmo");
			String displayName = plugin.getConfig().getString("MysteryBox.guns." + s + ".DisplayName");
			displayName = ChatColor.YELLOW + displayName + ChatColor.YELLOW + " ▪ «" + startingAmmo + "»";
			meta.setDisplayName(displayName);
			List<String> lore = plugin.getConfig().getStringList("MysteryBox.guns." + s + ".Lore");
			meta.setLore(lore);
			gun.setItemMeta(meta);
			guns.put(s, gun);
			ItemStack ammo = new ItemStack(Material.getMaterial(plugin.getConfig().getString("MysteryBox.guns." + s + ".Ammo.Material")), plugin.getConfig().getInt("MysteryBox.guns." + s + ".Ammo.Count"), (short) plugin.getConfig().getInt("MysteryBox.guns." + s + ".Ammo.Data"));
			if(!plugin.getConfig().getString("MysteryBox.guns." + s + ".Ammo.Material").equalsIgnoreCase("air")){
			ItemMeta ammoMeta = ammo.getItemMeta();
			ammoMeta.setDisplayName(ChatColor.YELLOW + s + " ammunition");
			ammo.setItemMeta(ammoMeta);
			}
			getAmmunition().put(s, ammo);
			
		}
		
	}
	
	
	public Map<String, ItemStack> getAmmunition(){
		return this.ammunition;
	}
	
	public Map<String, ItemStack> getGuns(){
		return this.guns;
	}
	
	public List<String> getGunNames(){
		return this.gunNames;
	}
	
	public List<ItemStack> getWeapons(){
		return this.weapons;
	}
	
	public String serializeLoc(Location l){
		return l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
	}
	
	public Location deserializeLoc(String s){
		String[] st = s.split(",");
		return new Location(Bukkit.getWorld(st[0]), Double.parseDouble(st[1]), Double.parseDouble(st[2]), Double.parseDouble(st[3]), Float.parseFloat(st[4]), Float.parseFloat(st[5]));
	}

}
