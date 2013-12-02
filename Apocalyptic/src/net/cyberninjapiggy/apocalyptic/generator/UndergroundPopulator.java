/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cyberninjapiggy.apocalyptic.generator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 *
 * @author Nick
 */
public class UndergroundPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random rand, Chunk chunk) {
        for (int x=0;x<16;x++) {
            int realX = x + chunk.getX() * 16;
            for (int z=0;z<16;z++) {
                int realZ = z + chunk.getZ() * 16;
                for (int y=7;y<50;y++) {
                    if (y<32) {
                        if (rand.nextInt(2000) == 0) {
                            int size = rand.nextInt(10) + 5;
                            if (size % 2 == 0) {
                                size+=1;
                            }
                            int[][] matrix = new int[size][];
                            double midPoint = (matrix.length-1)/2.0;
                            for (int col = 0; col < matrix.length; col++)
                            {
                                int[] row = new int[matrix.length];
                                double yy = col-midPoint;
                                for (int x2=0; 2<row.length; x2++)
                                {
                                   double xx = x2-midPoint;
                                   if (Math.sqrt(xx*xx+yy*yy)<=midPoint)
                                     row[x2] = 1;
                                }
                                matrix[col] = row;
                            }
                            for (int x2=0;x2<size;x2++) {
                                for (int y2=0;y2<size;y2++) {
                                    if (matrix[x2][y2] == 1 && world.getBlockAt(realX+x2, world.getHighestBlockYAt(realX+x2, realZ+y2), realZ+y2).getType() == Material.STONE) {
                                        world.getBlockAt(realX+x2, world.getHighestBlockYAt(realX+x2, realZ+y2), realZ+y2).setType(Material.LAVA);                                      
                                    }
                                }
                            }
                        }
                    }
                    else if (y<64) {
                        if (rand.nextInt(3000) == 0) {
                            int size = rand.nextInt(10) + 5;
                            if (size % 2 == 0) {
                                size+=1;
                            }
                            int[][] matrix = new int[size][];
                            double midPoint = (matrix.length-1)/2.0;
                            for (int col = 0; col < matrix.length; col++)
                            {
                                int[] row = new int[matrix.length];
                                double yy = col-midPoint;
                                for (int x2=0; 2<row.length; x2++)
                                {
                                   double xx = x2-midPoint;
                                   if (Math.sqrt(xx*xx+yy*yy)<=midPoint)
                                     row[x2] = 1;
                                }
                                matrix[col] = row;
                            }
                            for (int x2=0;x2<size;x2++) {
                                for (int y2=0;y2<size;y2++) {
                                    if (matrix[x2][y2] == 1 && world.getBlockAt(realX+x2, world.getHighestBlockYAt(realX+x2, realZ+y2), realZ+y2).getType() == Material.STONE) {
                                        world.getBlockAt(realX+x2, world.getHighestBlockYAt(realX+x2, realZ+y2), realZ+y2).setType(Material.WATER);                                      
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
}
