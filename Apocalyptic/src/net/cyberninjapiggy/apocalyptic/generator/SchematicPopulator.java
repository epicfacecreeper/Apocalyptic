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
			this.schematic = Schematic.loadSchematic(new File(p.getDataFolder().getAbsolutePath()+File.separator+"schematics"+File.separator+schemName));
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
		int xPos = chunk.getX()*16 + rand.nextInt(16-schematic.getWidth());
		int zPos = chunk.getZ()*16 + rand.nextInt(16-schematic.getLenght());
		int yPos = world.getHighestBlockYAt(xPos, zPos);
        if (yPos < 63) {
            return;
        }
        Schematic.pasteSchematic(world, new Location(world, xPos, yPos, zPos), schematic);
	}

}
