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
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import dev.kyriji.feature.game.WorldManager;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class SchematicUtils {
	public static void pasteSchematic(File schematicFile, Location location) {
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

		assert WorldManager.getWorld() != null;
		World world = new BukkitWorld(WorldManager.getWorld());

		BlockVector3 pos1 = BlockVector3.at(WorldManager.FIRST_CORNER.getX(), y, WorldManager.FIRST_CORNER.getZ());
		BlockVector3 pos2 = BlockVector3.at(WorldManager.SECOND_CORNER.getX(), y, WorldManager.SECOND_CORNER.getZ());

		try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
			Region region = new CuboidRegion(pos1, pos2);
			BlockPattern blockPattern = new BlockPattern(editSession.getBlock(pos1));
			blockPattern.setBlock(BaseBlock.getState(11, 0));

			Set<BaseBlock> blocks = Set.of(new BaseBlock(BaseBlock.getState(0, 0)));
			if(replace) editSession.replaceBlocks(region, blocks, blockPattern);
			else editSession.setBlocks(region, blockPattern);

			editSession.flushQueue();
		}
	}
}
