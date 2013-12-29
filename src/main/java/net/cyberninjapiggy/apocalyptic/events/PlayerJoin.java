/*
    Copyright 2013 Nick Schatz

    This file is part of Apocalyptic.

    Apocalyptic is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Apocalyptic is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Apocalyptic. If not, see <http://www.gnu.org/licenses/>.

*/

package net.cyberninjapiggy.apocalyptic.events;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Nick
 */
public class PlayerJoin implements Listener {
    private final Apocalyptic a;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        a.sendApocalypticTexturePack(e.getPlayer());
        a.getRadiationManager().loadRadiation(e.getPlayer());
    }
    public PlayerJoin(Apocalyptic a) {
        this.a = a;
    }
}
