package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.entity.*;
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
        if (!(e.getEntityType() == EntityType.ZOMBIE)
        		|| !(a.worldEnabledZombie(e.getEntity().getWorld().getName()))) {
            return;
        }
        if (e.getTarget() == null) {
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
        else {
            if (e.getTarget() instanceof Player) {
                Player humanEntity = (Player)e.getTarget();
                if (humanEntity.isSneaking() && humanEntity.getLocation().distance(e.getEntity().getLocation()) > 8) {
                    e.setTarget(null);
                }
            }
        }
    }
    public ZombieTarget(Apocalyptic a) {
        this.a = a;
    }
}
