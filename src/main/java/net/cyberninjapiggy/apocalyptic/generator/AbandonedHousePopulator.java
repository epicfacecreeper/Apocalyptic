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

package net.cyberninjapiggy.apocalyptic.generator;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Nick
 */
public class AbandonedHousePopulator extends BlockPopulator {
    private final Apocalyptic plugin;
    private final Map<LootTableEntry, Integer> lootTable = new HashMap<>();
    private int chance;

    public AbandonedHousePopulator(Apocalyptic plugin, FileConfiguration config) {
        this.plugin = plugin;
        chance = config.getInt("houses.frequency");
        genLootTable(config);
    }

    @Override
    public void populate(World world, Random rand, Chunk chunk) {
    	//genLootTable();
        if (world.canGenerateStructures()) {
            if (rand.nextInt(chance) == 0) {
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
    private void buildHouse(World world, Random rand, int realX, int realZ, int door) {
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
                            //noinspection deprecation,deprecation
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
                            //noinspection deprecation,deprecation
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
                            //noinspection deprecation,deprecation
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.WOOD.getId(), (byte) 3, false);
                    }
                    
                    else if (p == 4) {
                        if (i == 0) {
                            //noinspection deprecation,deprecation
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.COBBLESTONE_STAIRS.getId(), (byte) 0, false);
                        }
                        else if (i == houseX) {
                            //noinspection deprecation,deprecation
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.COBBLESTONE_STAIRS.getId(), (byte) 1, false);
                        }
                        else if (o == 0) {
                            //noinspection deprecation,deprecation
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.COBBLESTONE_STAIRS.getId(), (byte) 2, false);
                        }
                        else if (o == houseZ) {
                            //noinspection deprecation,deprecation
                            world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.COBBLESTONE_STAIRS.getId(), (byte) 3, false);
                        }
                        else {
                            if (rand.nextBoolean())
                                world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.WOOD);
                            else
                                //noinspection deprecation,deprecation
                                world.getBlockAt(realX+i, realY+p, realZ+o).setTypeIdAndData(Material.WOOD.getId(), (byte) 3, false);
                        }
                    }

                    else if (p == 1 && (i == 1 || o == 1 || i == houseX-1 || o == houseZ-1)) {
                        if (!chest && rand.nextInt((houseX-1)/2 * (houseZ-1)/2) == 0) {
                            world.getBlockAt(realX+i, realY+p, realZ+o).setType(Material.CHEST);
                            Chest c = (Chest) world.getBlockAt(realX+i, realY+p, realZ+o).getState();
                            Inventory inv = c.getBlockInventory();

                            for (Entry<LootTableEntry, Integer> e : lootTable.entrySet()) {
                                if (rand.nextInt(e.getValue()) == 0) {
                                    ItemStack stack = null;
                                    if (e.getKey().isSpecialItem()) {
                                        switch (e.getKey().getSpecialItem()) {
                                            case HAZMAT_HOOD:
                                                stack = new ItemStack(plugin.getHazmatHood()); break;
                                            case HAZMAT_SUIT:
                                                stack = new ItemStack(plugin.getHazmatSuit()); break;
                                            case HAZMAT_PANTS:
                                                stack = new ItemStack(plugin.getHazmatPants()); break;
                                            case HAZMAT_BOOTS:
                                                stack = new ItemStack(plugin.getHazmatBoots()); break;
                                        }
                                    }
                                    else {
                                        stack = new ItemStack(e.getKey().getMaterial());
                                    }
                                    plugin.getLogger().info(""+(e.getKey().getMax()-e.getKey().getMin()));
                                    stack.setAmount(rand.nextInt(e.getKey().getMax()-e.getKey().getMin()+1)+e.getKey().getMin());
                                    inv.setItem(rand.nextInt(inv.getSize()-1), stack);
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
    private void genLootTable(FileConfiguration worldConfig) {
    	/*lootTable.put(plugin.getHazmatBoots(), 2);
        lootTable.put(plugin.getHazmatPants(), 2);
        lootTable.put(plugin.getHazmatSuit(), 2);
        lootTable.put(plugin.getHazmatHood(), 2);
        lootTable.put(new ItemStack(Material.SPONGE, rand.nextInt(4) + 1), 1);
        lootTable.put(new ItemStack(Material.SADDLE, 1), 1);
        lootTable.put(new ItemStack(Material.OBSIDIAN, rand.nextInt(4) + 1), 4);
        lootTable.put(new ItemStack(Material.LAVA_BUCKET), 5);
        lootTable.put(new ItemStack(Material.SUGAR_CANE), 4);*/

        ConfigurationSection loot = worldConfig.getConfigurationSection("houses.loot");
        Set<String> keys = loot.getKeys(false);
        for (String key : keys) {
            LootTableEntry entry;
            ConfigurationSection item = worldConfig.getConfigurationSection("houses.loot."+key);
            SpecialItem special = SpecialItem.match(item.getName());
            if (special != null) {
                entry = new LootTableEntry(special, item.getInt("min"), item.getInt("max"));
            }
            else {
                entry = new LootTableEntry(Material.matchMaterial(item.getName()), item.getInt("min"), item.getInt("max"));
            }
            lootTable.put(entry, item.getInt("frequency"));
        }
    }
    private class LootTableEntry {
        private final Material mat;
        private final int min;
        private final int max;
        private final SpecialItem item;
        private boolean isSpecialItem = false;

        public LootTableEntry(Material mat, int min, int max) {

            this.mat = mat;
            this.min = min;
            this.max = max;
            item = null;
        }
        public LootTableEntry(SpecialItem item, int min, int max) {
            isSpecialItem = true;
            this.item = item;
            this.min = min;
            this.max = max;
            mat = null;
        }

        public Material getMaterial() {
            return mat;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public boolean isSpecialItem() {
            return isSpecialItem;
        }

        public SpecialItem getSpecialItem() {
            return item;
        }
    }
    private enum SpecialItem {
        HAZMAT_HOOD,
        HAZMAT_SUIT,
        HAZMAT_PANTS,
        HAZMAT_BOOTS;
        public static SpecialItem match(String s) {
            SpecialItem item;
            try {
                item = SpecialItem.valueOf(s.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                return null;
            }
            return item;
        }
    }
}
