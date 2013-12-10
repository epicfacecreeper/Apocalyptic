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
public class TreePopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random rand, Chunk chunk) {
        for (int x2=0;x2<16;x2++) {
            int x = x2 + chunk.getX() * 16;
            for (int z2=0;z2<16;z2++) {
                int z = z2 + chunk.getZ() * 16;
                if (world.getBlockAt(x, world.getHighestBlockYAt(x, z)-1, z).getType() == Material.MYCEL) {
                    
                    if (rand.nextInt(1500) == 0) {
                        genTree(x, world.getHighestBlockYAt(x, z), z, world, rand);
                    }
                    
                }
            }
        }
    }
    public void genTree(int x, int y, int z, World world, Random rand) {
        int limbs = rand.nextInt(5)+ 2;
        int height = rand.nextInt(6) + 7;
        for (int i=0;i<height;i++) {
            world.getBlockAt(x, y+i, z).setType(Material.LOG);
        }
        for (int i=0;i<limbs;i++) {
            int x2 = x;
            int y2 = y;
            int z2 = z;
            int spot = rand.nextInt(height-3)+2;
            int dir = rand.nextInt(4); //N/S is z
            int metadata;
            switch(dir) {
                case 0:
                    z2+=1;
                    metadata = 8;
                    break;
                case 2:
                    z2-=1;
                    metadata = 8;
                    break;
                case 1:
                    x2-=1;
                    metadata = 4;
                    break;
                case 3:
                    x2+=1;
                    metadata = 4;
                    break;
                default:
                    metadata=0;
            }
            int len  = rand.nextInt(3)+2;
            for (int o=0;o<len;o++) {
                switch(dir) {
                    case 0:
                        world.getBlockAt(x2, y2+spot, z2+o).setTypeIdAndData(Material.LOG.getId(), (byte) metadata, true);
                        break;
                    case 2:
                        world.getBlockAt(x2, y2+spot, z2-o).setTypeIdAndData(Material.LOG.getId(), (byte) metadata, true);
                        break;
                    case 1:
                        world.getBlockAt(x2-o, y2+spot, z2).setTypeIdAndData(Material.LOG.getId(), (byte) metadata, true);
                        break;
                    case 3:
                        world.getBlockAt(x2+o, y2+spot, z2).setTypeIdAndData(Material.LOG.getId(), (byte) metadata, true);
                        break;
                }
                if (rand.nextInt(7) == 0) {
                    y2+=1;
                }
            }
        }
    }
}
