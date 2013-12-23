package net.cyberninjapiggy.apocalyptic.misc;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ZombieHelper {
	public static boolean canZombieSpawn(Location l) {
		World w = l.getWorld();
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
        boolean playerTooClose = false;
        for (Player p : w.getEntitiesByClass(Player.class)) {
            if (p.getLocation().distance(l) < 16) {
                playerTooClose = true;
                break;
            }
        }
		return 
				w.getBlockAt(l).getLightLevel() <= 7
                && !w.getBlockAt(x, y, z).getType().isTransparent()
				&& w.getBlockAt(x, y+1, z).getType().isTransparent() 
				&& w.getBlockAt(x, y+2, z).getType().isTransparent()
                && !playerTooClose;
	}
}
