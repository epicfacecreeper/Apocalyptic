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

package net.cyberninjapiggy.apocalyptic.generator;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 *
 * @author Nick
 */
public class BiomePopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for (int x=0;x<16;x++) {
            int realX = x + chunk.getX() * 16;
            for (int z=0;z<16;z++) {
                int realZ = z + chunk.getZ() * 16;
                if (!(world.getBiome(realX, realZ) == Biome.OCEAN) 
                        && !(world.getBiome(realX, realZ) == Biome.FOREST)
                        && !(world.getBiome(realX, realZ) == Biome.JUNGLE))
                world.setBiome(realX, realZ, Biome.PLAINS);
            }
        }
    }
    
}
