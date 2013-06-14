package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Nick
 */
public class MonsterSpawn implements Listener {
    private final Apocalyptic a;
    @EventHandler
    public void onMonsterSpawn(CreatureSpawnEvent e) {
        
        if (e.getEntityType() == EntityType.ZOMBIE) {
            if (a.getConfig().getBoolean("worlds." + e.getLocation().getWorld().getName() + ".mobs.mutants.zombie")) {
                Location l = e.getLocation();
                if (Apocalyptic.rand.nextInt(300) == 0) {
                    e.setCancelled(true);
                    l.getWorld().spawnEntity(l, EntityType.GIANT);
                    return;
                }
                if (e.getSpawnReason() != SpawnReason.CUSTOM) {
                    int hordeSize = Apocalyptic.rand.nextInt(
                            a.getConfig().getWorld(e.getEntity().getWorld()).getInt("mobs.zombies.hordeSize.max") - 
                            a.getConfig().getWorld(e.getEntity().getWorld()).getInt("mobs.zombies.hordeSize.min")) + 
                            a.getConfig().getWorld(e.getEntity().getWorld()).getInt("mobs.zombies.hordeSize.min");
                    for (int i=0;i<hordeSize;i++) {
                        int spotX = 7-Apocalyptic.rand.nextInt(14);
                        int spotZ = 7-Apocalyptic.rand.nextInt(14);
                        Location spawnPoint = l.add(spotX, 0, spotZ);
                        spawnPoint.setY(l.getWorld().getHighestBlockYAt(spotX, spotZ));
                        
                        l.getWorld().spawnEntity(spawnPoint, EntityType.ZOMBIE);
                        
                    }
                }
            }
        }
        if (e.getEntityType() == EntityType.CREEPER) {
            if (a.getConfig().getBoolean("worlds." + e.getLocation().getWorld().getName() + ".mobs.mutants.creeper")) {
                if (Apocalyptic.rand.nextInt(100) == 0) {
                    ((Creeper) e.getEntity()).setPowered(true);
                    return;
                }
            }
        }
        if (e.getEntityType() == EntityType.SKELETON) {
            if (a.getConfig().getBoolean("worlds." + e.getLocation().getWorld().getName() + ".mobs.mutants.skeleton")) {
                if (Apocalyptic.rand.nextInt(100) == 0) {
                    ((Skeleton) e.getEntity()).setSkeletonType(Skeleton.SkeletonType.WITHER);
                    ((Skeleton) e.getEntity()).getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD, 0));
                }
            }
        }
    }
    public MonsterSpawn(Apocalyptic a) {
        this.a = a;
    }
}
