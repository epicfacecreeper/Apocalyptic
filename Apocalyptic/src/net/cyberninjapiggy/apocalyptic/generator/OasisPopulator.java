package net.cyberninjapiggy.apocalyptic.generator;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

/**
 *
 * @author Nick
 */
public class OasisPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random rand, Chunk chunk) {
        if (rand.nextInt(200) == 0) {
            int realX = 0;
            int realZ = 0;
            boolean flag = false;
            outer_loop:
            for (int i=0;i<16;i++) {
                realX = i + chunk.getX() * 16;
                for (int o=0;o<16;o++) {
                    realZ = o + chunk.getZ() * 16;
                    if (world.getBlockAt(realX, world.getHighestBlockYAt(realX, realZ)-1, realZ).getType() == Material.MYCEL) {
                        flag = true;
                        break outer_loop;
                    }
                }
            }
            if (!flag)
                return;
            int size = rand.nextInt(5) + 5;
            if (size % 2 == 0) {
                size+=1;
            }
            int[][] matrix = new int[size][];
            double midPoint = (matrix.length-1)/2.0;
            for (int col = 0; col < matrix.length; col++)
            {
                int[] row = new int[matrix.length];
                double yy = col-midPoint;
                for (int x=0; x<row.length; x++)
                {
                   double xx = x-midPoint;
                   if (Math.sqrt(xx*xx+yy*yy)<=midPoint)
                     row[x] = 1;
                }
                matrix[col] = row;
            }
            Location tree = new Location(world, realX+(size-1)/2, world.getHighestBlockYAt(realX+(size-1)/2, realZ+(size-1)/2)-1, realZ+(size-1)/2);
            for (int x=0;x<size;x++) {
                for (int y=0;y<size;y++) {
                    if (matrix[x][y] == 1) {
                        int pondSize = (size-1)/4;
                        if (
                                x < ((size-1)/2)+pondSize && x > ((size-1)/2)-pondSize &&
                                y < ((size-1)/2)+pondSize && y > ((size-1)/2)-pondSize) {
                            world.getBlockAt(realX+x, world.getHighestBlockYAt(realX+x, realZ+y)+30, realZ+y).setType(Material.WATER);
                        }
                        else if (
                                x == ((size-1)/2)+pondSize+1 && x == ((size-1)/2)-pondSize-1 &&
                                y == ((size-1)/2)+pondSize+1 && y == ((size-1)/2)-pondSize-1) {
                            for (int i=0;i<rand.nextInt(2+1);i++) {
                                world.getBlockAt(realX+x, world.getHighestBlockYAt(realX+x, realZ+y)+i+30, realZ+y).setType(Material.WATER);
                            }
                            
                        }
                        else {
                            world.getBlockAt(realX+x, world.getHighestBlockYAt(realX+x, realZ+y)+30, realZ+y).setType(Material.GRASS);
                            if (rand.nextInt(5) == 0) {
                                tree = new Location(world, realX+x, world.getHighestBlockYAt(realX+x, realZ+y), realZ+y);
                            }
                            if (rand.nextInt(2) == 0) {
                                world.getBlockAt(realX+x, world.getHighestBlockYAt(realX+x, realZ+y), realZ+y).setTypeIdAndData(Material.LONG_GRASS.getId(), (byte) 1, true);
                            }
                        }
                    }
                }
            }
             TreeType tt;
            switch(rand.nextInt(4)) {
                case 0:
                    tt = TreeType.BIG_TREE;
                    break;
                case 1:
                    tt = TreeType.TREE;
                    break;
                case 2:
                    tt = TreeType.BIRCH;
                    break;
                case 3:
                    tt = TreeType.SMALL_JUNGLE;
                    break;
                case 4:
                    tt = TreeType.SMALL_JUNGLE;
                    break;
                default:
                    tt = TreeType.TREE;
            }
            
            
            for (int i=1;i<3;i++) {
            	genDirtCircle(world, rand, chunk, size-i, i, realX, realZ);
            }
            world.generateTree(tree, tt);
        }
    }
    public void genDirtCircle(World world, Random rand, Chunk chunk, int size, int level, int realX, int realZ) {
    	int mysize = size - 1;
        int[][] matrix = new int[mysize][mysize];
        double midPoint = (matrix.length-1)/2.0;
        for (int col = 0; col < matrix.length; col++)
        {
            int[] row = new int[matrix.length];
            double yy = col-midPoint;
            for (int x=0; x<row.length; x++)
            {
               double xx = x-midPoint;
               if (Math.sqrt(xx*xx+yy*yy)<=midPoint)
                 row[x] = 1;
            }
            matrix[col] = row;
        }
        
        for (int x=0;x<mysize-1;x++) {
            for (int y=0;y<mysize-1;y++) {
                if (matrix[x][y] == 1) {
                    world.getBlockAt(realX+x+1, world.getHighestBlockYAt(realX+x+1, realZ+y+1)-(level+1), realZ+y+1).setType(Material.DIRT);
                       
                }
            }
        }
    }
}
