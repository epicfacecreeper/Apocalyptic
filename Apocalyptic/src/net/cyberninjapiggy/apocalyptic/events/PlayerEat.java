package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Nick
 */
public class PlayerEat implements Listener {
    private final Apocalyptic a;
    @EventHandler
    public void onPlayerEat(final PlayerItemConsumeEvent e) {
    	boolean isCure = false;
        for (String s : a.getConfig().getStringList("radiationCure")) {
        	if (e.getItem().getType().equals(Material.matchMaterial(s))) {
        		isCure = true;
        		break;
        	}
        }
        if (isCure) {
        	a.setPlayerRadiation(e.getPlayer(), 0.0);
        }
        if (a.getPlayerRadiation(e.getPlayer()) >= 6.0 && !e.isCancelled()) {
            final int oldLevel = e.getPlayer().getFoodLevel();
            a.getServer().getScheduler().scheduleSyncDelayedTask(a, new Runnable() {
                @Override
                public void run() {
                    e.getPlayer().setFoodLevel(oldLevel);
                    Item dropped = e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), new ItemStack(Material.ROTTEN_FLESH));
                    dropped.setVelocity(e.getPlayer().getLocation().add(0, 1, 0).getDirection().normalize().multiply(1));
                }
            }, 3);
        }
        
    }
    public PlayerEat(Apocalyptic a) {
        this.a = a;
    }
}
