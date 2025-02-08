package dev.kyriji.feature.world;

import dev.kyriji.TheFloorIsLava;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;

public class WorldManager {
	public static final Location FIRST_CORNER = new Location(getWorld(), 74, 0, 74);
	public static final Location SECOND_CORNER = new Location(getWorld(), -75, 0, -75);

	public static final Location MAP_SPAWN = new Location(getWorld(), 0, 300, 0);
	public static final Location MAP_SCHEMATIC_LOCATION = new Location(getWorld(), 0, -64, 0);

	public static final Location LOBBY_SCHEMATIC_LOCATION = new Location(getLobbyWorld(), 0, 76, 0);
	public static final Location LOBBY_SPAWN = new Location(getLobbyWorld(), 0.5, 78, 0.5);

	public static void init() {
		getWorld().getWorldBorder().setCenter(0, 0);
		getWorld().getWorldBorder().setSize(150);

		getWorld().setStorm(false);
		getWorld().setThundering(false);
		getWorld().setTime(1000);
		getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		getWorld().setSpawnLocation(MAP_SPAWN);

		getLobbyWorld().setStorm(false);
		getLobbyWorld().setThundering(false);
		getLobbyWorld().setClearWeatherDuration(Integer.MAX_VALUE);
		getLobbyWorld().setTime(1000);
		getLobbyWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		getLobbyWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		getLobbyWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		getLobbyWorld().setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
		getLobbyWorld().setGameRule(GameRule.DO_MOB_SPAWNING, false);
		getLobbyWorld().setSpawnLocation(LOBBY_SPAWN);
	}

	public static World getWorld() {
		return Bukkit.getWorld("world");
	}

	public static World getLobbyWorld() {
		return Bukkit.getWorld("lobby");
	}

	public static void pasteLobbySchematic() {
		File lobbySchematic = new File(TheFloorIsLava.INSTANCE.getDataFolder(), "lobby.schem");
		SchematicUtils.pasteSchematic(lobbySchematic, LOBBY_SCHEMATIC_LOCATION);
	}

}
