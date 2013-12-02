/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Nick
 */
public class PlayerMove implements Listener {
    private final Apocalyptic a;
    public PlayerMove(Apocalyptic a) {
        this.a = a;
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (a.worldEnabledFallout(e.getPlayer().getWorld().getName())) {
            if (e.getTo().getWorld().getBlockAt(e.getTo()).getType() == Material.WATER || e.getTo().getWorld().getBlockAt(e.getTo()).getType() == Material.STATIONARY_WATER) {
                a.setPlayerRadiation(e.getPlayer(), 0.0);
                
            }
        }
    }
    
}
