package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 *
 * @author Nick
 */
public class ZombieTarget implements Listener {
    private final Apocalyptic a;
    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        if (!(e.getEntityType() == EntityType.ZOMBIE) || e.getTarget() == null || a.worldEnabledZombie(e.getEntity().getWorld().getName())) {
            return;
        }
        double searchRadius = a.getConfig().getWorld(e.getEntity().getWorld()).getDouble("mobs.zombies.targetRange") * 2;
        for (Entity ent : e.getEntity().getNearbyEntities(searchRadius, searchRadius, searchRadius)) {
            if (ent.getType() != EntityType.ZOMBIE) {
                continue;
            }
            Zombie z = (Zombie) ent;
            if (z.getTarget() == null) {
                z.setTarget((LivingEntity) e.getTarget());
            }
        }
    }
    public ZombieTarget(Apocalyptic a) {
        this.a = a;
    }
}
