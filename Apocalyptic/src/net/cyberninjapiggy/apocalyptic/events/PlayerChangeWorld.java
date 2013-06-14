package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

/**
 *
 * @author Nick
 */
public class PlayerChangeWorld implements Listener {
    private final Apocalyptic a;
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        a.sendApocalypticTexturePack(e.getPlayer());
    }
    public PlayerChangeWorld(Apocalyptic a) {
        this.a = a;
    }
}
