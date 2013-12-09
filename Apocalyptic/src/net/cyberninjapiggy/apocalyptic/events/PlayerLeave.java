package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class PlayerLeave implements Listener {

	private Apocalyptic apocalyptic;

	public PlayerLeave(Apocalyptic a) {
		this.apocalyptic = a;
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		try {
			apocalyptic.saveRadiation(e.getPlayer());
			//apocalyptic.closeDatabase();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

}
