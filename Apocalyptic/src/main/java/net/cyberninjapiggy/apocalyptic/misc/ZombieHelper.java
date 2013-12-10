package net.cyberninjapiggy.apocalyptic.misc;

import org.bukkit.Location;
import org.bukkit.World;

public class ZombieHelper {
	public static boolean canZombieSpawn(Location l) {
		World w = l.getWorld();
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		return 
				w.getBlockAt(l).getLightLevel() <= 7
				&& w.getBlockAt(x, y+1, z).getType().isTransparent() 
				&& w.getBlockAt(x, y+2, z).getType().isTransparent();
	}
}
