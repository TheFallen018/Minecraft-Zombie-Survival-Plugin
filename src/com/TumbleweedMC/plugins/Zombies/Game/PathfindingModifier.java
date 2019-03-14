package com.TumbleweedMC.plugins.Zombies.Game;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class PathfindingModifier {
	
	private static Object pathfindingField;
	private static Method getLivingEntityHandle;
	private static Method getAttributeInstance;
	private static Method setValue;
 
	static{
		try{
			pathfindingField = getMCClass("GenericAttributes").getDeclaredField("b").get(null);
			getLivingEntityHandle = getCraftClass("entity.CraftLivingEntity").getMethod("getHandle");
			getAttributeInstance = getMCClass("EntityLiving").getMethod("getAttributeInstance", getMCClass("IAttribute"));
			setValue = getMCClass("AttributeInstance").getMethod("setValue", double.class);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void setPathfindingRange(LivingEntity e, double range){
		try {
			Object handle = getLivingEntityHandle.invoke(e);
			Object attributeInstance = getAttributeInstance.invoke(handle, pathfindingField);
			setValue.invoke(attributeInstance, range);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
    private static Class<?> getMCClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String className = "net.minecraft.server." + version + name;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }
 
    private static Class<?> getCraftClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String className = "org.bukkit.craftbukkit." + version + name;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }
 
}