package net.cyberninjapiggy.apocalyptic.generator;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class LavaPopulator extends BlockPopulator {
	//Taken from Nordic bukkit plugin
	public void populate(World world, Random random, Chunk source)
	  {
	    if (random.nextInt(50) >= 2) {
	      return;
	    }
	    ChunkSnapshot snapshot = source.getChunkSnapshot();

	    int rx16 = random.nextInt(16);
	    int rx = (source.getX() << 4) + rx16;
	    int rz16 = random.nextInt(16);
	    int rz = (source.getZ() << 4) + rz16;
	    if (snapshot.getHighestBlockYAt(rx16, rz16) < 4)
	      return;
	    int ry = random.nextInt(20) + 20;
	    int radius = 2 + random.nextInt(4);

	    Material solidMaterial = Material.STATIONARY_LAVA;

	    ArrayList<Block> lakeBlocks = new ArrayList<>();
	    Vector center;
	    for (int i = -1; i < 4; i++) {
	      center = new BlockVector(rx, ry - i, rz);
	      for (int x = -radius; x <= radius; x++) {
	        for (int z = -radius; z <= radius; z++) {
	          Vector position = center.clone().add(new Vector(x, 0, z));
	          if (center.distance(position) <= radius + 0.5D - i) {
	            lakeBlocks.add(world.getBlockAt(position.toLocation(world)));
	          }
	        }
	      }
	    }

	    for (Block block : lakeBlocks)
	    {
	      if ((!block.isEmpty()) && (!block.isLiquid()))
	        if (block.getY() >= ry) {
	          block.setType(Material.AIR);
	        }
	        else
	          block.setType(solidMaterial);
	    }
	  }

}
