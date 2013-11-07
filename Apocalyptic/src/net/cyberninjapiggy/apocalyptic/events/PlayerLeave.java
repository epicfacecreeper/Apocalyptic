package net.cyberninjapiggy.apocalyptic.events;

import java.sql.SQLException;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {

	private Apocalyptic apocalyptic;

	public PlayerLeave(Apocalyptic a) {
		this.apocalyptic = a;
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		try {
			apocalyptic.saveRadiation(e.getPlayer());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

}
