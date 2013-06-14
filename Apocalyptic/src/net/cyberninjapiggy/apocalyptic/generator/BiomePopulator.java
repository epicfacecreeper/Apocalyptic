package net.cyberninjapiggy.apocalyptic.generator;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

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
