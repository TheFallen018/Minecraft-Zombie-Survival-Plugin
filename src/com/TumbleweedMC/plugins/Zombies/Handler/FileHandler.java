package com.TumbleweedMC.plugins.Zombies.Handler;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.TumbleweedMC.plugins.Zombies.Zombies;

public class FileHandler {
	
	static Zombies plugin;
	
	public FileHandler(Zombies plugin){
		FileHandler.plugin = plugin;
	}
	
	private File CareersFile;
	private File arenaFile;
	public FileConfiguration arenaData;
	public FileConfiguration careersData;
		
	public void reloadFiles(){
		if (arenaFile == null){
			this.arenaFile = new File(plugin.getDataFolder(), "Arena.yml");
			
		try{
			if(!this.arenaFile.exists()){
				this.arenaFile.createNewFile();
				plugin.sendConsole(ChatColor.YELLOW + "Arena.yml does not exist! Creating new File...");
			}
			}catch(IOException e){
				
			}
		}
		
		if (CareersFile == null){
			this.CareersFile = new File(plugin.getDataFolder(), "Careers.yml");
			
		try{
			if(!this.CareersFile.exists()){
				this.CareersFile.createNewFile();
				plugin.sendConsole(ChatColor.YELLOW + "Careers.yml does not exist! Creating new File...");
			}
			}catch(IOException e){
				
			}
		}
		

		
		loadFiles();
		saveFiles();
	}
	
	public void loadFiles(){
		this.arenaData = YamlConfiguration.loadConfiguration(arenaFile);
		this.careersData = YamlConfiguration.loadConfiguration(CareersFile);
	}
	
	public void saveFiles(){
		try{
			this.arenaData.save(arenaFile);
			this.careersData.save(CareersFile);
		}catch(IOException e){
			
		}
	}
	

}
