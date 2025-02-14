package dev.kyriji.feature.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SchematicUtils {
	public static void pasteSchematic(File schematicFile, Location location) {
		System.out.println(Bukkit.getWorlds());
		System.out.println(location.getWorld());

		World world = new BukkitWorld(Objects.requireNonNull(location.getWorld()));

		try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
			Clipboard clipboard;

			ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
			if(format == null) throw new IllegalArgumentException("Unknown schematic format");
			try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
				clipboard = reader.read();
			} catch(IOException e) {
				throw new RuntimeException(e);
			}

			Operation operation = new ClipboardHolder(clipboard)
					.createPaste(editSession)
					.to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
					.build();
			Operations.complete(operation);
		}
	}

	public static void setLayerToLava(int y, boolean replace) {
		Location firstCorner = WorldManager.FIRST_CORNER;
		Location secondCorner = WorldManager.SECOND_CORNER;

		org.bukkit.World world = firstCorner.getWorld();

		for(int x = (int) secondCorner.getX(); x <= firstCorner.getX(); x++) {
			for(int z = (int) secondCorner.getZ(); z <= firstCorner.getZ(); z++) {

				Block block = world.getBlockAt(x, y, z);
				block.getChunk().setForceLoaded(true);

				if(replace) {
					if(block.getBlockData().getMaterial().isAir()) world.getBlockAt(x, y, z).setType(Material.LAVA);
					System.out.println(x + " " + y + " " + z + " " + block.getBlockData().getMaterial());
				}
				else  world.getBlockAt(x, y, z).setType(Material.LAVA);

			}
		}
	}
}
