package com.TumbleweedMC.plugins.Zombies.Arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Arena {
	
	public String id = "";
	Location spawn = null;
	Location lobby = null;
	Location spectator = null;
	Location end = null;
	List<String> players = new ArrayList<String>();
	List<String> spectators = new ArrayList<String>();
	List<Location> ZombieSpawns = new ArrayList<Location>();
	List<Location> MysteryBoxes = new ArrayList<Location>();
	List<String> inGamePlayers = new ArrayList<String>();
	Map<String, String> career = new HashMap<String, String>();
	List<String> Juggernog = new ArrayList<String>();
	Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
	List<BlockState> doors = new ArrayList<BlockState>();
	Map<String, Integer> kills = new HashMap<String, Integer>();
	List<String> awatingRevive = new ArrayList<String>();
	List<String> dead = new ArrayList<String>();
	Map<String, Integer> timeRemaining = new HashMap<String, Integer>();
	Map<String, Location> signs = new HashMap<String, Location>();
	public List<LivingEntity> Zombies = new ArrayList<LivingEntity>();
	public int ZombiesLeft = 0;
	public int waveCount = 0;
	public int waveZombies = 4;
	public double zombieHealth = 5;
	public int timer = 6;
	public float speed = 0.18F;
	public boolean instakill = false;
	public boolean doubleScore = false;
	public int runnerChance = ((int) Math.random() * 50) + 1;
	
	Objective objective = board.registerNewObjective("Zombies", "Board");

	
	
	public Arena(Location spawn, Location lobby, Location spectator, Location end, List<Location> ZombieSpawns, List<Location> MysteryBoxes, String id){
		spawn = this.spawn;
		lobby = this.lobby;
		spectator = this.spectator;
		end = this.end;
		id = this.id;
		ZombieSpawns = this.ZombieSpawns;
		MysteryBoxes = this.MysteryBoxes;

	}
	
	public String getID(){
		return this.id;
	}
	
	public List<BlockState> getDoors(){
		return this.doors;
	}
	public List<String> getAwaitingRevive(){
		return this.awatingRevive;
	}
	public List<String> getDeadPlayers(){
		return this.dead;
	}
	
	public Map<String, String> getCareer(){
		return this.career;
	}
	
	public int getRunnerChance(){
		return this.runnerChance;
	}
	
	public Map<String, Location> getSigns(){
		return this.signs;
	}
	
	public Map<String, Integer> getTimeRemaining(){
		return this.timeRemaining;
	}
	
	public boolean isInstakill(){
		return this.instakill;
	}
	
	public boolean isDoubleScore(){
		return this.doubleScore;
	}
	
	public Map<String, Integer> getKills(){
		return this.kills;
	}
	
	public List<String> getPlayers(){
		return this.players;
	}
	
	public List<String> getJuggernog(){
		return this.Juggernog;
	}
	
	public List<String> getSpectators(){
		return this.spectators;
	}
	
	public List<String> getInGamePlayers(){
		return this.inGamePlayers;
	}
	
	public List<Location> getZombieSpawns(){
		return this.ZombieSpawns;
	}
	
	public int getWaveZombies(){
		return this.waveZombies;
	}
	
	public Scoreboard getScoreBoard(){
		return this.board;
	}
	
	public Objective getObjective(){
		return this.objective;
	}
	public int getTimer(){
		return this.timer;
	}
	
	public float getSpeed(){
		return this.speed;
	}
	
	public List<Location> getMysteryBoxes(){
		return this.MysteryBoxes;
	}
	
	public List<LivingEntity> getZombies(){
		return this.Zombies;
	}
	public Integer getWaves(){
		return this.waveCount;
	}
	
	public double getWaveHealth(){
		return this.zombieHealth;
	}
	
	
	public Integer getZombiesLeft(){
		return this.ZombiesLeft;
	}
	

}
