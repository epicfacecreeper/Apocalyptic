package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 *
 * @author Nick
 */
public class PlayerSpawn implements Listener {
    private final Apocalyptic a;
    public PlayerSpawn(Apocalyptic a) {
        this.a = a;
    }
    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent e) {
        a.setPlayerRadiation(e.getPlayer(), 0.0);
    }
}
