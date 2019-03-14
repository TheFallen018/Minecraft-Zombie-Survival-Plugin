package com.TumbleweedMC.plugins.Zombies;


import java.io.File;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.TumbleweedMC.plugins.Zombies.Arena.Arena;
import com.TumbleweedMC.plugins.Zombies.Arena.ArenaManager;
import com.TumbleweedMC.plugins.Zombies.Game.GameManager;
import com.TumbleweedMC.plugins.Zombies.Handler.CommandListener;
import com.TumbleweedMC.plugins.Zombies.Handler.EventListener;
import com.TumbleweedMC.plugins.Zombies.Handler.FileHandler;

public class Zombies extends JavaPlugin{
	
	private FileHandler  fileHandler = new FileHandler(this);
	private GameManager  gameManager = new GameManager(this);
	private EventListener eventListener = new EventListener(this);
	public static Economy econ = null;
	public boolean debugEnabled = false;
	
	@Override
	public void onEnable(){
		if(setupEconomy() == false){
			sendConsole(ChatColor.RED + "Plugin disabled due to vault not being present");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if(setupBarApi() == false){
			sendConsole(ChatColor.RED + "Plugin disabled due to BarAPI not being present");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		if(!this.getDataFolder().exists()){
			this.getDataFolder().mkdir();
		}
		File file = new File(getDataFolder() + File.separator + "config.yml");
		if(!file.exists()){
			saveDefaultConfig();
		}
		
		fileHandler.reloadFiles();
		
		
		getCommand("Zombies").setExecutor(new CommandListener(this));
		
		new ArenaManager(this);
		ArenaManager.getManager().loadGames();
		ArenaManager.getManager().loadSigns();
		ArenaManager.getManager().updateSigns();
		ArenaManager.getManager().fillWeaponsList();
		fileHandler.loadFiles();
		getServer().getPluginManager().registerEvents(new GameManager(this), this);
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Zombies has been enabled!");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Zombies is developed by JeremyPark123999 for play.tumbleweed-mc.net");
		
		
	}
	
	@Override
	public void onDisable(){
		ArenaManager.getManager().endAllGames();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Zombies has been disabled!");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Zombies is developed by JeremyPark123999 for play.tumbleweed-mc.net");
	}
	
	private boolean setupEconomy(){
		if(getServer().getPluginManager().getPlugin("Vault") == null){
			sendConsole(ChatColor.RED + "Vault not found, please install it.");
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null){
			return false;
		}
		
		econ = rsp.getProvider();
		return econ != null;
		
	}
	
	public void setDebugEnabled(Boolean tempBoolean){
		if(tempBoolean){
			debugEnabled = true;
			System.out.println("Debugging Enabled!");
		}else{
			debugEnabled = false;
			System.out.println("Debugging Disabled!");
		}
		
	}
	
	public Boolean getDebugEnabled(){
		return this.debugEnabled;
	}
	
	public void debug(String message){
		if(!debugEnabled){
			return;
		}else{
			System.out.println(message);
		}
		
	}
	
	public boolean setupBarApi(){
		if(getServer().getPluginManager().getPlugin("BarAPI") == null){
			sendConsole(ChatColor.RED + "BarAPI not found, please install it.");
			return false;
		}
		return true;
	}
	public void sendMessage(Player player, String message){
		player.sendMessage(ChatColor.RED + message);
	}
	
	public Economy getEconomy(){
		return Zombies.econ;
	}
	
	public void sendConsole(String message){
		Bukkit.getConsoleSender().sendMessage(message);
	}
	
	public FileConfiguration getArenaData(){
		return fileHandler.arenaData;
	}
	
	public EventListener getEventListener(){
		return this.eventListener;
	}
	
	public FileConfiguration getCareerData(){
		return fileHandler.careersData;
	}
	
	public void saveFiles(){
		saveConfig();
		fileHandler.saveFiles();
	}
	
	public FileHandler getFH(){
		return fileHandler;
	}
	
	public void startWaves(Arena a){
		gameManager.startWaves(a);
	}

}
