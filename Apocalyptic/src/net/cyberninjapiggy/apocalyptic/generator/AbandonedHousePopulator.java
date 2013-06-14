package net.cyberninjapiggy.apocalyptic.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Nick
 */
public class AbandonedHousePopulator extends BlockPopulator {
    Map<ItemStack, Integer> lootTable = new HashMap<>();

    @Override
    public void populate(World world, Random rand, Chunk chunk) {
    	genLootTable(rand);
        if (world.canGenerateStructures()) {
            if (rand.nextInt(400) == 0) {
                int realX;
                int realZ;
                outer_loop:
                for (int i=0;i<16;i++) {
                    realX = i + chunk.getX() * 16;
                    for (int o=0;o<16;o++) {
                        realZ = o + chunk.getZ() * 16;
                        if (world.getHighestBlockYAt(realX, realZ) > 64) {
                            int door = rand.nextInt(3);
                            buildHouse(world, rand, realX, realZ, door);
                            break outer_loop;
                        }
                    }
                }
            }
        }
    }
    public void buildHouse(World world, Random rand, int realX, int realZ, int door) {
        int houseX = rand.nextInt(4) + 4;
        int houseZ = rand.nextInt(4) + 4;
        
        

        int realY = world.getHighestBlockYAt(realX, realZ)-1;
        
        int midX;
        if (houseX % 2 == 0) {
            midX = houseX/2;
        }
        else {
            midX = (houseX-1)/2;
        }
        int midZ;
        if (houseZ % 2 == 0) {
            midZ = houseZ/2;
        }
        else {
            midZ = (houseZ-1)/2;
        }
        boolean chest = false;
        for (int i=0;i<=houseX;i++) {
            for (int o=0;o<=houseZ;o++) {
                int top = world.getHighestBlockYAt(realX, realZ);
                Material m = world.getHighestBlockAt(realX, realZ).getType();
                for (int p=top;p<realY;p++) {
                    world.getBlockAt(realX+i, p, realZ+o).setType(m);
                }
            }
        }
        
        for (int i=0;i<=houseX;i++) {
            for (int o=0;o<=houseZ;o++) {
                for (int p=0;p<5;p++) {
                	if (rand.nextInt(10) == 0) {
                		continue;
                	}
                    boolean isWall = i == 0 || o == 0 || i == houseX || o == houseZ;
                    boolean isCorner = (i == 0 && o == 0) 
                            || (i == 0 && o == houseZ)
                            || (i == houseX && o == 0)
                            || (i == houseX && o == houseZ);
                    boolean isDoorSpot = 
                            (door == 0 && i == midX && o == 0) || 
                            (door == 1 && i == midX && o == houseZ) ||
                            (door == 2 && i == 0 && o == midZ) ||
                            (door == 3 && i == houseX && o == 0);
                    if (p == 0) {
                        if (isWall && !isDoorSpot) {
                            world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.COBBLESTONE);
                        }
                        else if (rand.nextBoolean())
                            world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.WOOD);
                        else
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.WOOD.getId(), (byte) 3, false);
                    }
                    else if (p == 1 && isWall && !isDoorSpot) {
                        world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.COBBLESTONE);
                    }
                    else if (p == 2 && isWall && !isDoorSpot) {
                        if (isCorner) {
                            world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.COBBLESTONE);
                        }
                        else if (rand.nextBoolean())
                            world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.WOOD);
                        else
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.WOOD.getId(), (byte) 3, false);
                    }
                    else if (p == 3 && isWall) {
                        if (isCorner) {
                            world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.COBBLESTONE);
                        }
                        else if (o < (houseZ/2)-2 && o > 1 && (i == 0 || i == houseX) && !isDoorSpot) {
                            if (rand.nextBoolean())
                                world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.GLASS);
                            else 
                                world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.AIR);
                        }
                        else if (rand.nextBoolean())
                            world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.WOOD);
                        else
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.WOOD.getId(), (byte) 3, false);
                    }
                    
                    else if (p == 4) {
                        if (i == 0) {
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.COBBLESTONE_STAIRS.getId(), (byte) 0, false);
                        }
                        else if (i == houseX) {
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.COBBLESTONE_STAIRS.getId(), (byte) 1, false);
                        }
                        else if (o == 0) {
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.COBBLESTONE_STAIRS.getId(), (byte) 2, false);
                        }
                        else if (o == houseZ) {
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.COBBLESTONE_STAIRS.getId(), (byte) 3, false);
                        }
                        else {
                            if (rand.nextBoolean())
                                world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.WOOD);
                            else
                                world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.WOOD.getId(), (byte) 3, false);
                        }
                    }

                    else if (p == 1 && (i == 1 || o == 1 || i == houseX-1 || o == houseZ-1)) {
                        if (!chest && rand.nextInt((houseX-1)/2 * (houseZ-1)/2) == 0) {
                            world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.CHEST);
                            Chest c = (Chest) world.getBlockAt(realX+i, realY+p, realZ+o).getState();
                            Inventory inv = c.getBlockInventory();

                            for (Entry<ItemStack, Integer> e : lootTable.entrySet()) {
                                if (rand.nextInt(e.getValue()) == 0) {
                                    inv.setItem(rand.nextInt(inv.getSize()-1), e.getKey());
                                }
                            }
                            chest = true;
                        }
                        else {
                            world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.AIR);
                        }
                    }
                    else {
                        world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.AIR);
                    }
                }
            }
        }
    }
    public void genLootTable(Random rand) {
    	lootTable.put(Apocalyptic.hazmatBoots, 2);
        lootTable.put(Apocalyptic.hazmatPants, 2);
        lootTable.put(Apocalyptic.hazmatSuit, 2);
        lootTable.put(Apocalyptic.hazmatHood, 2);
        lootTable.put(new ItemStack(Material.SPONGE, rand.nextInt(4) + 1), 1);
        lootTable.put(new ItemStack(Material.SADDLE, 1), 1);
        lootTable.put(new ItemStack(Material.OBSIDIAN, rand.nextInt(4) + 1), 4);
        lootTable.put(new ItemStack(Material.LAVA_BUCKET), 5);
    }
}
