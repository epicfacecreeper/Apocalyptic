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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CavePopulator extends BlockPopulator {
	//Taken from Nordic plugin for bukkit, with modifications.

	public void populate(World world, Random random, Chunk source)
	  {
	    if (random.nextInt(25) == 0) {
	      int x = 4 + random.nextInt(8) + source.getX() * 16;
	      int z = 4 + random.nextInt(8) + source.getZ() * 16;
	      int maxY = world.getHighestBlockYAt(x, z);
	      if (maxY < 16) {
	        maxY = 32;
	      }

	      int y = random.nextInt(maxY);
	      Set<Location> snake = selectBlocksForCave(world, random, x, y, z);
	      buildCave(world, (Location[])snake.toArray(new Location[0]));
	      for (Location block : snake)
	        world.unloadChunk(((int)block.getX() / 16), ((int)block.getZ() / 16));
	    }
	  }

	  static Set<Location> selectBlocksForCave(World world, Random random, int blockX, int blockY, int blockZ)
	  {
	    Set<Location> snakeBlocks = new HashSet<>();

	    int airHits = 0;
	    Location block = new Location(world,0,0,0);

	    while (airHits <= 1200)
	    {
	      if (random.nextInt(20) == 0) {
	        blockY++;
	      }
	      else if (world.getBlockTypeIdAt(blockX, blockY + 2, blockZ) == 0) {
	        blockY += 2;
	      }
	      else if (world.getBlockTypeIdAt(blockX + 2, blockY, blockZ) == 0) {
	        blockX++;
	      }
	      else if (world.getBlockTypeIdAt(blockX - 2, blockY, blockZ) == 0) {
	        blockX--;
	      }
	      else if (world.getBlockTypeIdAt(blockX, blockY, blockZ + 2) == 0) {
	        blockZ++;
	      }
	      else if (world.getBlockTypeIdAt(blockX, blockY, blockZ - 2) == 0) {
	        blockZ--;
	      }
	      else if (world.getBlockTypeIdAt(blockX + 1, blockY, blockZ) == 0) {
	        blockX++;
	      }
	      else if (world.getBlockTypeIdAt(blockX - 1, blockY, blockZ) == 0) {
	        blockX--;
	      }
	      else if (world.getBlockTypeIdAt(blockX, blockY, blockZ + 1) == 0) {
	        blockZ++;
	      }
	      else if (world.getBlockTypeIdAt(blockX, blockY, blockZ - 1) == 0) {
	        blockZ--;
	      }
	      else if (random.nextBoolean()) {
	        if (random.nextBoolean())
	          blockX++;
	        else {
	          blockZ++;
	        }
	      }
	      else if (random.nextBoolean())
	        blockX--;
	      else {
	        blockZ--;
	      }

	      if (world.getBlockTypeIdAt(blockX, blockY, blockZ) != 0) {
	        int radius = 1 + random.nextInt(2);
	        int radius2 = radius * radius + 1;
	        for (int x = -radius; x <= radius; x++) {
	          for (int y = -radius; y <= radius; y++) {
	            for (int z = -radius; z <= radius; z++) {
	              if ((x * x + y * y + z * z <= radius2) && (y >= 0) && (y < 128))
	                if (world.getBlockTypeIdAt(blockX + x, blockY + y, blockZ + z) == 0) {
	                  airHits++;
	                } else {
	                  block.setX(blockX + x);
	                  block.setY(blockY + y);
	                  block.setZ(blockZ + z);
	                  if (snakeBlocks.add(block))
	                    block = new Location(world,0,0,0);
	                }
	            }
	          }
	        }
	      }
	      else
	      {
	        airHits++;
	      }
	    }

	    return snakeBlocks;
	  }

	  static void buildCave(World world, Location[] snakeBlocks) {
	    for (Location loc : snakeBlocks) {
	      Block block = world.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	      if ((!block.isEmpty()) && (!block.isLiquid()) && (block.getType() != Material.BEDROCK))
	        block.setType(Material.AIR);
	    }
	  }

}
