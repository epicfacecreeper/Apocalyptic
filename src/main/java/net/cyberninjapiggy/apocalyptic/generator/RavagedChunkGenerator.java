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

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Nick
 */
public class RavagedChunkGenerator extends ChunkGenerator {

    private final String genID;
	private final Apocalyptic apocalyptic;

	public RavagedChunkGenerator(Apocalyptic p, String genID) {
		this.genID = genID;
		this.apocalyptic = p;



	}
	public static void setBlock(int x, int y, int z, byte[][] chunk, Material material) {
        if (chunk[y >> 4] == null)
            chunk[y >> 4] = new byte[16 * 16 * 16];
        if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0))
            return;
        try {
            //noinspection deprecation
            chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (byte) material.getId();
        } catch (Exception e) {e.printStackTrace();}
}
    @Override
    public byte[][] generateBlockSections(World world, Random rand, int ChunkX, int ChunkZ, BiomeGrid biome) {
        OctaveGenerator gen1 = new PerlinOctaveGenerator(world,8);
        gen1.setScale(1/64.0);
        byte[][] chunk = new byte[world.getMaxHeight() / 16][];
        for (int x=0; x<16; x++) { 
            for (int z=0; z<16; z++) {
 
                int realX = x + ChunkX * 16; //used so that the noise function gives us
                int realZ = z + ChunkZ * 16; //different values each chunk
                double frequency = 0.5; // the reciprocal of the distance between points
                double amplitude = 0.2; // The distance between largest min and max values
                int multitude = 2;
                int sea_level = 64;
                if (world.getBiome(realX, realZ) == Biome.SMALL_MOUNTAINS || world.getBiome(realX, realZ) == Biome.FOREST_HILLS
                        || world.getBiome(realX, realZ) == Biome.TAIGA_HILLS || world.getBiome(realX, realZ) == Biome.JUNGLE_HILLS
                        || world.getBiome(realX, realZ) == Biome.TAIGA_HILLS || world.getBiome(realX, realZ) == Biome.BIRCH_FOREST_HILLS
                        || world.getBiome(realX, realZ) == Biome.COLD_TAIGA_HILLS || world.getBiome(realX, realZ) == Biome.DESERT_HILLS
                        || world.getBiome(realX, realZ) == Biome.MEGA_TAIGA_HILLS) {
                    multitude = 16; 
                }
                else if (world.getBiome(realX, realZ) == Biome.EXTREME_HILLS || world.getBiome(realX, realZ) == Biome.EXTREME_HILLS_MOUNTAINS
                		|| world.getBiome(realX, realZ) == Biome.BIRCH_FOREST_MOUNTAINS || world.getBiome(realX, realZ) == Biome.TAIGA_MOUNTAINS) {
                    multitude = 32; 
                    amplitude = 0.1;
                }
                else if (world.getBiome(realX, realZ) == Biome.EXTREME_HILLS_PLUS || world.getBiome(realX, realZ) == Biome.EXTREME_HILLS_PLUS_MOUNTAINS) {
                    amplitude = 0.01;
                    multitude = 64;
                    if (sea_level > 96)
                        sea_level = 96;
                }
                else if (world.getBiome(realX, realZ) == Biome.SWAMPLAND) {
                    multitude = 32; 
                    amplitude = 0.001;
                    sea_level = 62;
                }
                else if (world.getBiome(realX, realZ) == Biome.MUSHROOM_ISLAND) {
                    multitude = 64; 
                    amplitude = 0.001;
                    sea_level *= 2;
                    if (sea_level > 128)
                        sea_level = 128;
                }
                else if (world.getBiome(realX, realZ) == Biome.MUSHROOM_SHORE) {
                    multitude = 64; 
                    amplitude = 0.01;
                    sea_level *= 1.5;
                    if (sea_level > 96)
                        sea_level = 96;
                }
                else if (world.getBiome(realX, realZ) == Biome.FROZEN_OCEAN || world.getBiome(realX, realZ) == Biome.OCEAN) {
                    multitude = 16; 
                    amplitude = 0.1;
                    sea_level = 42;
                }
                else if (world.getBiome(realX, realZ) == Biome.DEEP_OCEAN) {
                	multitude = 16; 
                    amplitude = 0.1;
                    sea_level = 30;
                }
 
                double maxHeight = gen1.noise(realX, realZ, frequency, amplitude) * multitude + sea_level;
                for (int y = 1; y <= 6; y++) {
                    if (y == 1) {
                        setBlock(x,y,z,chunk,Material.BEDROCK);
                    } 
                    else {
                        if (rand.nextBoolean()) {
                            setBlock(x,y,z,chunk,Material.BEDROCK);
                        }
                        else {
                            setBlock(x,y,z,chunk,Material.STONE);
                        }
                    }
                }
                for (int y=5;y<=maxHeight+1;y++) {
                    
                    if (y<maxHeight-5) {
                        setBlock(x,y,z,chunk,Material.STONE);
                    }
                    else {
                        if (y>128) {
                            setBlock(x,y,z,chunk,Material.GRAVEL);
                        }
                        else if (y<world.getSeaLevel() - 12) {
                            setBlock(x,y,z,chunk,Material.SAND);
                        }
                        else {
                        	if (world.getBiome(realX, realZ) == Biome.MESA || world.getBiome(realX, realZ) == Biome.MESA_BRYCE) {
                        		setBlock(x,y,z,chunk,Material.HARD_CLAY);
                        	}
                        	else if (false && world.getBiome(realX, realZ) == Biome.SAVANNA) {
                        		// TODO grassless dirt
                        	}
                        	else if (false && world.getBiome(realX, realZ) == Biome.FLOWER_FOREST) {
                        		// TODO podzol
                        	}
                            else if (world.getBiome(realX, realZ) == Biome.JUNGLE) {
                                setBlock(x,y,z,chunk,Material.SAND);
                            }
                        	else {
	                        	if (y < maxHeight) {
	                        		setBlock(x,y,z,chunk,Material.DIRT);
	                        	}
	                        	else {
	                        		setBlock(x,y,z,chunk,Material.MYCEL);
	                        	}
                        	}
                        }
                    }
                    
                } 
            }
                
        }
        
        return chunk;
    }
    
    
        
    
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        File worldsFolder = new File(apocalyptic.getDataFolder()+File.separator+"worlds");
        FileConfiguration config;
        if (!worldsFolder.exists()) {
            worldsFolder.mkdir();
        }
        File worldConfig = new File(worldsFolder.getAbsolutePath() + File.separator + world.getName() + ".yml");

        if (!worldConfig.exists())
        {
            InputStream in = apocalyptic.getResource("world.yml");
            OutputStream out = null;
            try {
                out = new FileOutputStream(worldConfig);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int i;
            try {
                while ((i = in.read()) != -1) {
                    out.write(i);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        config = YamlConfiguration.loadConfiguration(worldConfig);

        ArrayList<BlockPopulator> pops;
        pops = new ArrayList<>();
        pops.add(new DungeonPopulator());
        pops.add(new OasisPopulator(config.getInt("oases.frequency"), config.getInt("oases.size.max"), config.getInt("oases.size.min")));
        pops.add(new TreePopulator());
        pops.add(new OrePopulator(world));
        pops.add(new AbandonedHousePopulator(apocalyptic, config));
        pops.add(new CavePopulator());
        pops.add(new LavaPopulator());
        
        if (genID != null) {
	        String[] schems = genID.split(":");
	        for (String s : schems) {
	        	String name = s.split("@")[0];
	        	int chance = Integer.parseInt(s.split("@")[1]);
	        	pops.add(new SchematicPopulator(apocalyptic, name+".schematic", chance));
	        }
        }
        
        return pops;
    }
    
}
