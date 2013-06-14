package net.cyberninjapiggy.apocalyptic.generator;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;

/**
 *
 * @author Nick
 */
public class LaboratoryPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        
    }
    public void genEntrance(World world, Random rand, Chunk chunk) {
        int trueX = rand.nextInt(5) + chunk.getX() * 16;
        int trueZ = rand.nextInt(5) + chunk.getZ() * 16;
        
        
    }
}
