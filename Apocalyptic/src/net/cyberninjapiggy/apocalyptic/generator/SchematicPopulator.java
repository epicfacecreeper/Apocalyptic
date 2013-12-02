package net.cyberninjapiggy.apocalyptic.generator;

import net.cyberninjapiggy.apocalyptic.misc.Schematic;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class SchematicPopulator extends BlockPopulator {

	private Schematic schematic;
	private int chance;

	public SchematicPopulator(Plugin p, String schemName, int chance) {
		try {
			this.schematic = new Schematic(p.getDataFolder().getAbsolutePath()+File.separator+"schematics"+File.separator+schemName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.chance = chance;
	}

	@Override
	public void populate(World world, Random rand, Chunk chunk) {
		if (rand.nextInt(chance) != 0) {
			return;
		}
		int xPos = chunk.getX()*16;
		int zPos = chunk.getZ()*16;
		int yPos = world.getHighestBlockYAt(xPos, zPos);
		schematic.paste(new Location(world, xPos, yPos, zPos));
	}

}
