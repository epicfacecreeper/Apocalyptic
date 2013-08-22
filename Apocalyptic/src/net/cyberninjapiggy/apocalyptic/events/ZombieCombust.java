package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;

public class ZombieCombust implements Listener {
	private Apocalyptic plugin;
	@EventHandler
	public void onZombieBurn(EntityCombustEvent e) {
		if (plugin.worldEnabledZombie(e.getEntity().getWorld().getName()) 
				&& plugin.getConfig().getWorld(e.getEntity().getWorld()).getBoolean("mobs.zombies.burnInDaylight")) {
			if (!(e instanceof EntityCombustByEntityEvent) && !(e instanceof EntityCombustByBlockEvent)) {
				e.setCancelled(true);
			}
		}
	}
	public ZombieCombust(Apocalyptic p) {
		this.plugin = p;
	}
}
