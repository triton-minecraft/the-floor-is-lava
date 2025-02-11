package dev.kyriji.feature.game.model;

import dev.kyriji.feature.game.enums.GameState;
import dev.kyriji.feature.world.SchematicUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {
	private GameState gameState = GameState.WAITING;
	private final List<Player> alivePlayers;
	private GameEvent currentEvent;

	private int lavaLevel = -64;

	public Game() {
		this.alivePlayers = new ArrayList<>();
		this.currentEvent = null;

		Bukkit.getOnlinePlayers().forEach(this::addPlayer);
	}

	public void addPlayer(Player player) {
		alivePlayers.add(player);
	}

	public void removePlayer(Player player) {
		alivePlayers.remove(player);
	}

	public List<Player> getAlivePlayers() {
		return alivePlayers;
	}

	public void riseLava() {
		lavaLevel++;
		SchematicUtils.setLayerToLava(lavaLevel, false);
		SchematicUtils.setLayerToLava(lavaLevel - 1, true);
	}

	public int getLavaLevel() {
		return lavaLevel;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public GameEvent getCurrentEvent() {
		return currentEvent;
	}

	public String getCurrentEventString() {
		return currentEvent == null ? "&cNone" : currentEvent.getDisplayName();
	}

	public void setCurrentEvent(GameEvent event) {
		this.currentEvent = event;
	}
}
