package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class StopHazmatCrafting implements Listener {
	private Apocalyptic plugin;
	@EventHandler
	public void onPlayerPrepareCraft(CraftItemEvent e) {
		if (plugin.worldEnabledFallout(e.getWhoClicked().getWorld().getName()) &&
				(e.getRecipe().getResult() == Apocalyptic.hazmatBoots 
				|| e.getRecipe().getResult() == Apocalyptic.hazmatPants
				|| e.getRecipe().getResult() == Apocalyptic.hazmatSuit
				|| e.getRecipe().getResult() == Apocalyptic.hazmatHood)) {
			e.setCancelled(true);
			((Player)e.getWhoClicked()).sendMessage(ChatColor.RESET + "Cannot craft that." + ChatColor.RESET);
		}
	}
	public StopHazmatCrafting(Apocalyptic a) {
		this.plugin = a;
	}
}
