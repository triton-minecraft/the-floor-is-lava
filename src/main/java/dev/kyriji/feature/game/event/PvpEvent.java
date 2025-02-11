package dev.kyriji.feature.game.event;

import dev.kyriji.feature.game.model.GameEvent;

import java.util.List;

public class PvpEvent implements GameEvent {
	@Override
	public String getIdentifier() {
		return "pvp";
	}

	@Override
	public String getDisplayName() {
		return "&c&lPVP ENABLED";
	}

	@Override
	public List<String> getDescription() {
		return List.of("&7PvP has been enabled until the next event");
	}

	@Override
	public void start() {

	}

	@Override
	public void end() {

	}
}
