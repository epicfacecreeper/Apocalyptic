package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 *
 * @author Nick
 */
public class PlayerJoin implements Listener {
    private final Apocalyptic a;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        a.sendApocalypticTexturePack(e.getPlayer());
        a.loadRadiation(e.getPlayer());
    }
    public PlayerJoin(Apocalyptic a) {
        this.a = a;
    }
}
