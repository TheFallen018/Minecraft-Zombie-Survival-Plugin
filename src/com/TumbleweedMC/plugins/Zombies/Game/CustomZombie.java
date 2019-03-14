package com.TumbleweedMC.plugins.Zombies.Game;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityIronGolem;
import net.minecraft.server.v1_7_R3.EntityVillager;
import net.minecraft.server.v1_7_R3.GenericAttributes;
import net.minecraft.server.v1_7_R3.PathfinderGoalBreakDoor;
import net.minecraft.server.v1_7_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_7_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_7_R3.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_7_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_7_R3.PathfinderGoalRandomLookaround;

import org.bukkit.craftbukkit.v1_7_R3.util.UnsafeList;



public class CustomZombie extends net.minecraft.server.v1_7_R3.EntityZombie{
	public int damage;
	private float bw;
	
	@Override
	protected void aC() {
	super.aC();
	this.getAttributeInstance(GenericAttributes.b).setValue(70.0D);
	}
	
	@SuppressWarnings("rawtypes")
	public CustomZombie(net.minecraft.server.v1_7_R3.World world) {
		super(world);
		this.bw = 1.5F;
		this.damage = 15;
		
		

		
		
		
		try{
			Field gsa = net.minecraft.server.v1_7_R3.PathfinderGoalSelector.class.getDeclaredField("a");
			gsa.setAccessible(true);
			gsa.set(this.goalSelector, new UnsafeList());
			gsa.set(this.targetSelector, new UnsafeList());
		} catch (SecurityException e){
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		
		this.goalSelector.a(0, new PathfinderGoalFloat(this));
		this.goalSelector.a(1, new PathfinderGoalBreakDoor(this));
		this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, (float) (this.bw) , false)); // this one to attack human
		this.goalSelector.a(3, new PathfinderGoalMeleeAttack(this, EntityIronGolem.class, (float) this.bw, true));
		this.goalSelector.a(3, new PathfinderGoalMeleeAttack(this, EntityVillager.class, (float) this.bw, true));
		this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, (float) this.bw));
		this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F)); // this one to look at human
		this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
		this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
		this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 0, true)); // this one to target human
		this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, 0, false));
		this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, 0, false));
		
	}
	
	
	
	
}



