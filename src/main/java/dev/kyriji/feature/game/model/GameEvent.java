package dev.kyriji.feature.game.model;

import net.kyori.adventure.text.Component;

import java.util.List;

public interface GameEvent {

	String getIdentifier();

	String getDisplayName();

	List<String> getDescription();

	void start();

	void end();
}
