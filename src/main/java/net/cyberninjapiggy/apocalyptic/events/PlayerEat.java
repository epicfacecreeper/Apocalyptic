/*
 * Copyright (C) 2014 Nick Schatz
 *
 *     This file is part of Apocalyptic.
 *
 *     Apocalyptic is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Apocalyptic is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Apocalyptic.  If not, see <http://www.gnu.org/licenses/>.
 */

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
        for (String s : a.getConfig().getStringList("radiationCures")) {
        	if (e.getItem().getType().equals(Material.matchMaterial(s))) {
        		isCure = true;
        		break;
        	}
        }
        if (isCure) {
        	a.getRadiationManager().setPlayerRadiation(e.getPlayer(), 0.0);
        }
        if (a.getRadiationManager().getPlayerRadiation(e.getPlayer()) >= 6.0 && !e.isCancelled()) {
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
