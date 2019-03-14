package com.TumbleweedMC.plugins.Zombies.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.TumbleweedMC.plugins.Zombies.Zombies;
import com.TumbleweedMC.plugins.Zombies.Arena.Arena;
import com.TumbleweedMC.plugins.Zombies.Arena.ArenaManager;

public class GameManager implements Listener{
	
	static Zombies plugin;
	Map<Arena, Integer> wave = new HashMap<Arena, Integer>();
	Map<Arena, Integer> zombieCount = new HashMap<Arena, Integer>();
	static Map<LivingEntity, Arena> zombieEntity = new HashMap<LivingEntity, Arena>();
	static List<String> players = new ArrayList<String>();
	Map<String, ItemStack[]> inventories = new HashMap<String, ItemStack[]>();
	
	private static GameManager gm;
	public GameManager(Zombies plugin){
		GameManager.plugin = plugin;
	}
	
	public GameManager(){
		
	}
	
	public static GameManager getManager(){
		if (gm == null){
			gm = new GameManager();
		}
		return gm;
	}

	
	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		if(!ArenaManager.getManager().isInGame(player)){
		return;
		}
		Location deathLoc = player.getLocation();
		deathLoc.setY(deathLoc.getY() + 8D);
		player.getWorld().strikeLightning(deathLoc);
		player.setHealth(20.0D);
		inventories.put(player.getName(), player.getInventory().getContents());
		Arena a = ArenaManager.getManager().getPlayersArena(player);
		ArenaManager.getManager().sendArenaMessage(ArenaManager.getManager().getPlayersArena(player), ChatColor.RED +  player.getName() + " needs to be revived!");
		setAwaitingRevive(player, a);

		
		event.setDeathMessage("");
		event.setDroppedExp(0);
		event.getDrops().clear();
		
	}
	
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		if (ArenaManager.getManager().isInGame(event.getPlayer())){
			ArenaManager.getManager().removePlayer(event.getPlayer());
		}
		
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		if((event.getEntity() instanceof Zombie)){
			if(ArenaManager.getManager().entityIsInGame(event.getEntity())){		
				if(event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.PROJECTILE || event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION){
				}else{
					event.setCancelled(true);
				}
				 
			}
		}
		

		
	}
	
	
	public void setAwaitingRevive(final Player player, final Arena a){
		Location loc = player.getLocation().getBlock().getLocation();
		
		player.setGameMode(GameMode.CREATIVE);
		player.setAllowFlight(false);
		for(String s : a.getPlayers()){
			Player otherPlayer = Bukkit.getServer().getPlayer(s);
			otherPlayer.hidePlayer(player);
		}
		a.getAwaitingRevive().add(player.getName());
		for(PotionEffect effect : player.getActivePotionEffects()){
			player.removePotionEffect(effect.getType());
		}
		if(a.getJuggernog().contains(player.getName())){
			a.getJuggernog().remove(player.getName());
		}
		player.getInventory().clear();
		ArenaManager.getManager().addKit(player);
		boolean playerStillAlive = true;
		List<String> deadPlayers = new ArrayList<String>();
		for(String s : a.getDeadPlayers()){
			deadPlayers.add(s);
		}
		for(String s : a.getAwaitingRevive()){
			deadPlayers.add(s);
		}
		plugin.debug("Number of in-Game players = " + a.getPlayers().size());
		plugin.debug("Number of dead players = " + deadPlayers.size());
		if(deadPlayers.size() == a.getPlayers().size()){
			plugin.debug("All players dead!");
			playerStillAlive = false;
		}
		if(!playerStillAlive){
			plugin.debug("All players dead!");
			ArenaManager.getManager().sendArenaMessage(a, ChatColor.RED + "All players are dead! GAME OVER!");
				ArenaManager.getManager().endGame(a);
			return;
		}
		for(int i = 1; i <= 100; i++){
		if(loc.getBlock().getType() == Material.AIR){
			plugin.debug("Revive Sign created!");
		loc.getBlock().setType(Material.SIGN_POST);
		i = 110;
		}else{
			plugin.debug("New y set for revive sign!");
			loc.setY(loc.getY() + 1D);
		}
	}	
		
		plugin.debug("Block at sign location = " + loc.getBlock().getType().toString());
		final Sign sign = (Sign) loc.getBlock().getState();
		sign.setLine(0, ChatColor.BOLD + "Revive");
		sign.setLine(1, ChatColor.GREEN + player.getName());
		a.getTimeRemaining().put(player.getName(), 30);
		sign.setLine(3, ChatColor.BOLD + Integer.toString(a.getTimeRemaining().get(player.getName())));
		sign.update();
		a.getSigns().put(player.getName(), loc);
		BarAPI.setMessage(player, ChatColor.RED + "" + ChatColor.BOLD + "Dying", 30);
		new BukkitRunnable() {
			
			@SuppressWarnings("deprecation")
			public void run() {
				if(!a.getPlayers().contains(player.getName())){
					this.cancel();
					return;
				}
				if(a.getTimeRemaining().get(player.getName()) == 0){
					plugin.debug("Player has died!");
					a.getTimeRemaining().remove(player.getName());
					sign.getBlock().setType(Material.AIR);
					ArenaManager.getManager().sendArenaMessage(a, ChatColor.RED + player.getName() + " has died!");
					a.getSigns().get(player.getName()).getBlock().setType(Material.AIR);
					a.getSigns().remove(player.getName());
					player.getInventory().clear();
					player.getInventory().setArmorContents(null);
					plugin.sendMessage(player, ChatColor.GREEN + "You will be automatically revived at the start of the nex wave!");
					BarAPI.removeBar(player);
					if(a.getJuggernog().contains(player.getName())){
						a.getJuggernog().remove(player.getName());
					}
					for(PotionEffect effect : player.getActivePotionEffects()){
						player.removePotionEffect(effect.getType());
					}
					a.getDeadPlayers().add(player.getName());
					a.getAwaitingRevive().remove(player.getName());
					this.cancel();
					return;
				}
				if(sign.getBlock().getType() == Material.AIR){
					
					a.getTimeRemaining().remove(player.getName());
					a.getAwaitingRevive().remove(player.getName());
					ArenaManager.getManager().sendArenaMessage(a, ChatColor.GREEN + player.getName() + " has been revived!");
					BarAPI.removeBar(player);
					player.getInventory().setContents(inventories.get(player.getName()));
					player.updateInventory();
					for(Player otherPlayer : Bukkit.getOnlinePlayers()){
						otherPlayer.showPlayer(player);
					}
					player.setGameMode(GameMode.SURVIVAL);
					this.cancel();
					return;

				}
				if(a.getTimeRemaining().get(player.getName()) == 30 || a.getTimeRemaining().get(player.getName()) == 25 || a.getTimeRemaining().get(player.getName()) == 20 || a.getTimeRemaining().get(player.getName()) == 15 || a.getTimeRemaining().get(player.getName()) == 10 || a.getTimeRemaining().get(player.getName()) == 5){
				Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
				FireworkMeta fwm = fw.getFireworkMeta();
				FireworkEffect FireworkBuilder = null;
				if(a.getTimeRemaining().get(player.getName()) == 30){
				FireworkBuilder =  FireworkEffect.builder().withColor(Color.GREEN).build();
				}
				if(a.getTimeRemaining().get(player.getName()) == 25){
				FireworkBuilder =  FireworkEffect.builder().withColor(Color.GREEN).withColor(Color.YELLOW).build();
				}
				if(a.getTimeRemaining().get(player.getName()) == 20){
				FireworkBuilder = FireworkEffect.builder().withColor(Color.YELLOW).build();
				}
				if(a.getTimeRemaining().get(player.getName()) == 15){
					FireworkBuilder = FireworkEffect.builder().withColor(Color.YELLOW).withColor(Color.RED).build();
					}
				if(a.getTimeRemaining().get(player.getName()) == 10){
				FireworkBuilder = FireworkEffect.builder().withColor(Color.RED).build();
				}
				if(a.getTimeRemaining().get(player.getName()) == 5){
				FireworkBuilder = FireworkEffect.builder().withColor(Color.RED).build();
				}
				fwm.addEffect(FireworkBuilder);
				fwm.setPower(1);
				fw.setFireworkMeta(fwm);
				}
				a.getTimeRemaining().put(player.getName(), a.getTimeRemaining().get(player.getName()) - 1);
				sign.setLine(3, ChatColor.BOLD + Integer.toString(a.getTimeRemaining().get(player.getName())));
				sign.update();
				
				
			}
		}.runTaskTimer(plugin, 1L, 20L);
		
		
		
	}
	

	@EventHandler
	public void onRevive(BlockBreakEvent event){
		if(!ArenaManager.getManager().isInGame(event.getPlayer())){
			return;
		}
		if(event.getBlock().getType() != Material.SIGN_POST){
			if(event.getPlayer().hasPermission("Zombies.edit")){
				return;
			}
			event.setCancelled(true);
			return;
		}
		
		Player player = event.getPlayer();
		Sign sign = (Sign) event.getBlock().getState();
		if(!sign.getLine(0).equalsIgnoreCase(ChatColor.BOLD + "Revive")){
			event.setCancelled(true);
			return;
		}
		if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + player.getName())){
			event.setCancelled(true);
			return;
		}
		if(sign.getLine(0).equalsIgnoreCase(ChatColor.BOLD + "Revive")){
				event.getBlock().setType(Material.AIR);
				event.setCancelled(true);
			
		}
		
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		if(!ArenaManager.getManager().isInGame(event.getPlayer())){
			return;
		}
		if(event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()){
			return;
		}
		Player player = event.getPlayer();
		Arena a = ArenaManager.getManager().getPlayersArena(player);
		
		if(a.getAwaitingRevive().contains(player.getName())){
			player.teleport(event.getFrom());
		}
		if(a.getDeadPlayers().contains(player.getName())){
			player.teleport(event.getFrom());
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event){

		if(((event.getDamager() instanceof Player) || event.getDamager() instanceof Arrow) && (event.getEntity() instanceof Zombie)){

			
			Player player = null;
			
			if (event.getDamager() instanceof Arrow){
				Arrow arrow = (Arrow) event.getDamager();
				if(arrow.getShooter() instanceof Player){
					player = (Player) arrow.getShooter();
				}
				
			}else{
			player = (Player) event.getDamager();
			}
			
			
			if(player == null){
				Location loc = event.getEntity().getLocation();
				dropBlood(loc);
				plugin.sendConsole("Player is null!");
				return;
			}			
			if(ArenaManager.getManager().isSpectating(player)){
				event.setCancelled(true);
				return;
			}
			final Player finalPlayer = player;
			final Entity finalEntity = event.getEntity();
			if(ArenaManager.getManager().isInGame(player) && ArenaManager.getManager().entityIsInGame(event.getEntity())){
				if(event.getCause() == DamageCause.ENTITY_ATTACK){
				if((player.getLocation().distance(event.getEntity().getLocation()) <= 1.75)){
					event.setDamage(25.0D);
					new BukkitRunnable() {
						
						public void run() {
							finalEntity.setVelocity(finalPlayer.getLocation().getDirection().multiply(0.1));
							
						}
					}.runTaskLater(plugin, 1L);
				}else{
					event.setCancelled(true);
					return;
				}
			}
				event.getEntity().getLocation().getWorld().playSound(event.getEntity().getLocation(), Sound.HURT_FLESH, 1, 1);

				new BukkitRunnable() {
					
					public void run() {
						finalEntity.setVelocity(finalPlayer.getLocation().getDirection().multiply(0.1));
						
					}
				}.runTaskLater(plugin, 1L);
				
					Arena a = ArenaManager.getManager().getPlayersArena(player);
					Location loc = event.getEntity().getLocation();
					if(a.isInstakill()){
						((LivingEntity) event.getEntity()).setHealth(0.0D);
					}
					dropBlood(loc);
				if(a.isDoubleScore()){
				plugin.getEconomy().depositPlayer(player.getName(), 20);
				}else{
				plugin.getEconomy().depositPlayer(player.getName(), 10);	
				}
				ArenaManager.getManager().updateScore(a, player);
				
			}
			
		}
		if(event.getDamager() instanceof Zombie && event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			if(ArenaManager.getManager().isInGame(player)){
				Arena a = ArenaManager.getManager().getPlayersArena(player);
				if(a.getJuggernog().contains(player.getName())){
				event.setDamage(4.0D);
				}else{
				event.setDamage(10.0D);	
				}
				
			}
		}
		
	}
	
	@EventHandler
	public void playerPickupEvent(PlayerPickupItemEvent event){
		if(ArenaManager.getManager().isInGame(event.getPlayer())){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerDropEvent(PlayerDropItemEvent event){
		if(ArenaManager.getManager().isInGame(event.getPlayer()) && !event.isCancelled()){
			event.getItemDrop().remove();
		}
	}

	
	public void dropBlood(Location loc){
		loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + 2, loc.getBlockZ());
		
				
       loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

	}
	
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		if((event.getEntity() instanceof Zombie) && (event.getEntity().getKiller() instanceof Player)){
			
			Player player = event.getEntity().getKiller();
			if(ArenaManager.getManager().isInGame(player) && ArenaManager.getManager().entityIsInGame((LivingEntity) event.getEntity())){
				Arena a = ArenaManager.getManager().getZombiesArena(event.getEntity());
				if(event.getEntity().getLastDamageCause().equals(DamageCause.ENTITY_ATTACK)){
					plugin.debug("Entity attack!");
					if(a.isDoubleScore()){
						plugin.getEconomy().depositPlayer(player.getName(), 260);
						}else{
						plugin.getEconomy().depositPlayer(player.getName(), 130);	
						}
				}else{
				
				if(a.isDoubleScore()){
					plugin.getEconomy().depositPlayer(player.getName(), 150);
					}else{
					plugin.getEconomy().depositPlayer(player.getName(), 75);	
					}
				}
				ArenaManager.getManager().updateScore(a, player);
				int kills = 0;
				if(a.getKills().containsKey(player.getName())){
					kills = a.getKills().get(player.getName());
				}
				kills = kills += 1;
				a.getKills().put(player.getName(), kills);
				if(a.getZombiesLeft() == 1){
					for (LivingEntity zombie : a.Zombies){
						SpeedModifier.setSpeed(zombie, a.getSpeed() * 1.5);
					}
				}
				if(event.getEntity().getFireTicks() > 1){
					World world = event.getEntity().getLocation().getWorld();
					world.createExplosion(event.getEntity().getLocation(), 0.0F);
				}
				if(!a.isInstakill() && !a.isDoubleScore()){
				double random = Math.random() * 100;
				int setrandom = (int) random + 1;
				if (setrandom == 46){
					a.instakill = true;
					for(String playerName : a.getPlayers()){
						Player p = Bukkit.getPlayer(playerName);
						BarAPI.setMessage(p, ChatColor.GREEN + "" + ChatColor.BOLD + "Instakill!", 30);
						ArenaManager.getManager().magicTime(a);
					}
					
				}
				else if(setrandom == 92){
					a.doubleScore = true;
					for(String playerName : a.getPlayers()){
						Player p = Bukkit.getPlayer(playerName);
						BarAPI.setMessage(p, ChatColor.GREEN + "" + ChatColor.BOLD + "Score X2!", 30);
						ArenaManager.getManager().magicTime(a);
					}
				}
			}
				
				
			}
			
		}
		if(event.getEntity() instanceof Zombie){
			if(ArenaManager.getManager().entityIsInGame(event.getEntity())){
				Arena a = ArenaManager.getManager().getZombiesArena(event.getEntity());
				a.Zombies.remove(event.getEntity());
				a.ZombiesLeft = a.getZombiesLeft() - 1;
				plugin.debug("Entity death!");
				event.getDrops().clear();
				event.setDroppedExp(0);
				if(a.getZombiesLeft() == 0){
					startWaves(a);
				}
			}
		}
	}
	
	
	
	@EventHandler
	public void onPlayerChestOpen(PlayerInteractEvent event){
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK){
			return;
		}
		if(event.getClickedBlock().getType() != Material.CHEST){
			return;
		}
		Player player = event.getPlayer();
		if(ArenaManager.getManager().isSpectating(player)){
			if((event.getAction() != Action.LEFT_CLICK_AIR) || (event.getAction() != Action.LEFT_CLICK_BLOCK) && (event.getPlayer().getInventory().getItemInHand().getType() != Material.REDSTONE_BLOCK)){
			event.setCancelled(true);
			return;
			}
		}
		if(!ArenaManager.getManager().isInGame(player)){
			return;
		}
		Arena a = ArenaManager.getManager().getPlayersArena(player);
		if(!a.getMysteryBoxes().contains(event.getClickedBlock().getLocation())){
			return;
		}
		if(plugin.getEconomy().getBalance(player.getName()) < 950.0D){
			plugin.sendMessage(event.getPlayer(), "You don't have enough money for the mystery box!");
			event.setCancelled(true);
			return;
		}
		Chest chest = (Chest) event.getClickedBlock().getState();
		if(chest.getInventory().getViewers().size() > 0){
			plugin.sendMessage(player, "You can't use the mystery box while someone else is using it.");
			event.setCancelled(true);
			return;
		}
		plugin.getEconomy().withdrawPlayer(player.getName(), 950.0D);
		
		ArenaManager.getManager().updateScore(a, player);
		ArenaManager.getManager().updateScore(a, player);
		ArenaManager.getManager().addRandomWeapons(event.getClickedBlock());
		
	}
	
	@EventHandler 
	public void InventoryClickEvent(InventoryClickEvent event){
		Player player = null;
		if(event.getInventory().getHolder() instanceof Player){
		player = (Player) event.getInventory().getHolder();
		}else{
			return;
		}
		if(!ArenaManager.getManager().isInGame(player)){
			return;
		}
		Arena a = ArenaManager.getManager().getPlayersArena(player);
		if(a.getDeadPlayers().contains(player.getName()) || a.getAwaitingRevive().contains(player.getName())){
			event.setCancelled(true);
			return;
		}
	}
	

	public void onPlayerWorldTeleport(PlayerTeleportEvent event){
		if(!ArenaManager.getManager().isInGame(event.getPlayer())){
			return;
		}
		Arena a = ArenaManager.getManager().getPlayersArena(event.getPlayer());
		World to = ArenaManager.getManager().deserializeLoc(plugin.getArenaData().getString("Arenas." + a.getID() + ".spawn")).getWorld();
		plugin.debug("World To: " + event.getTo().getWorld().toString());
		plugin.debug("Arena Spawn world: " + to.toString());
		if(event.getTo().getWorld().equals(to)){
			plugin.debug("World Same!");
			return;
		}
		if(!event.getFrom().getWorld().equals(event.getTo().getWorld())){
			plugin.sendMessage(event.getPlayer(), "Please type /zombies leave to exit the arena before teleporting!");
			event.setCancelled(true);
			return;
		}
	}
	
	public void killArenaZombies(Arena a){
		List<LivingEntity> tempList = a.getZombies();
		for(LivingEntity tempZombie : tempList){
			tempZombie.setHealth(0.0D);
			a.getZombies().remove(tempZombie);
		}
		a.getZombies().clear();
		a.ZombiesLeft = 0;
		startWaves(a);
		
	}
	
	public void startWaves(final Arena a){
		plugin.debug("StartWaves waveCount = " + a.waveCount);
		a.waveCount = a.getWaves() + 1;
		for(String p : a.getPlayers()){
			
			Player player = Bukkit.getServer().getPlayer(p);
			if(a.getDeadPlayers().contains(player.getName())){
				a.getDeadPlayers().remove(player.getName());
				ArenaManager.getManager().addKit(player);
				for(Player otherPlayer : Bukkit.getOnlinePlayers()){
					otherPlayer.showPlayer(player);
				}
				plugin.sendMessage(player, ChatColor.GREEN + "You have been resurrected!");
				player.setGameMode(GameMode.SURVIVAL);
			}
			player.setLevel(a.getWaves());
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 2, 0);
		}
		
		new BukkitRunnable() {
			
			public void run() {

				spawnZombies(a);
				
			}
		}.runTaskLater(plugin, 100L);
	}
	
	
	
	public void spawnZombies(Arena a){
		if(a.getPlayers().size() == 0){
			return;
		}
		a.speed = a.getSpeed() + 0.0075F;
		if(a.getRunnerChance() > 7 && a.getWaves() > 3){
		a.runnerChance = a.getRunnerChance() - 2;
		plugin.debug("2 subtracted from runner chance, chance now " + a.getRunnerChance());
		}
		List<Location> ZombieSpawns = a.getZombieSpawns();
		Location[] loc = new Location[ZombieSpawns.size()];
		loc = ZombieSpawns.toArray(loc);
		double spawncount = a.getWaveZombies() * 1.25;
		spawncount = Math.ceil(spawncount);
		int waveDifficulty = (int) spawncount;
		
		a.waveZombies = waveDifficulty;
		double currentHealth = a.getWaveHealth();

		for(int i = 1; i <= waveDifficulty; i++){
			plugin.debug("Location array size:" + loc.length);
			int spawn = (int) (Math.random() * loc.length);
			plugin.debug("Spawn location number: " + spawn);
			Location currentSpawn = loc[spawn];
			World world = currentSpawn.getWorld();
			
			
			LivingEntity currentZombie = (LivingEntity) world.spawnEntity(currentSpawn, EntityType.ZOMBIE);
			SpeedModifier.setSpeed(currentZombie, a.getSpeed());
			PathfindingModifier.setPathfindingRange(currentZombie, 100.0D);
			currentZombie.setMaxHealth(1000.0D);
			if(a.zombieHealth < 900.0D){
			if(a.getWaves() <= 9){
				currentHealth = a.zombieHealth + 10;
				currentZombie.setHealth(currentHealth);
			}else{
				currentHealth = a.zombieHealth * 1.1;
				currentZombie.setHealth(currentHealth);
			}
		}else{
			currentHealth = a.zombieHealth * 1.1;
			currentZombie.setHealth(950.0D);
			plugin.debug("Max Zombie exceeded at" + a.zombieHealth + "! Setting health to 950.0");
		}
			Damageable damageable = (Damageable) currentZombie;
			plugin.debug("Max Health" + Double.toString(damageable.getMaxHealth()));
			plugin.debug("Current Health" + Double.toString(damageable.getHealth()));
			if(a.getRunnerChance() == 4 && a.getWaves() > 3){
				SpeedModifier.setSpeed(currentZombie, a.getSpeed() * 1.5);
			}
			currentZombie.setCanPickupItems(false);
			currentZombie.setRemoveWhenFarAway(false);
			a.Zombies.add((LivingEntity) currentZombie);
			
			a.ZombiesLeft = a.getZombiesLeft() + 1;
			long delay = 100 / a.getWaves();
			if(delay < 10){
				delay = 10L;
			}
			
		}
		a.zombieHealth = currentHealth;

	}
	
}
