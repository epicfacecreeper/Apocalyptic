/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cyberninjapiggy.apocalyptic.generator;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

/**
 * Deprecated. Bedrock, dirt and grass population is now part of RavagedChunkGenerator.java.
 * @author Nick
 */
@Deprecated
public class WorldPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random rand, Chunk chunk) {
        for (int x2=0;x2<16;x2++) {
            int x = x2 + chunk.getX() * 16;
            for (int z2=0;z2<16;z2++) {
                int z = z2 + chunk.getZ() * 16;
                for (int y=0;y<5;y++) {
                    if (y==0) {
                        world.getBlockAt(x, y, z).setType(Material.BEDROCK);
                    } 
                    else {
                        if (rand.nextBoolean()) {
                            world.getBlockAt(x, y, z).setType(Material.BEDROCK);
                        }
                    }
                }
                int top = world.getHighestBlockYAt(x, z);
                for (int y=top-5;y<top;y++) {
                    world.getBlockAt(x, y, z).setType(Material.DIRT);
                }
                if (top < world.getSeaLevel() - 12) {
                     world.getBlockAt(x, top, z).setType(Material.SAND);
                }
                else {
                     world.getBlockAt(x, top, z).setType(Material.MYCEL);
                }
            }
        }
    }
    
}
