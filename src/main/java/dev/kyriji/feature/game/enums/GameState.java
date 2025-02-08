package dev.kyriji.feature.game.enums;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum GameState {
	WAITING(Component.text("WAITING").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)),
	GRACE_PERIOD(Component.text("GRACE PERIOD").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)),
	RISING_LAVA(Component.text("RISING LAVA").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD)),
	DEATH_MATCH(Component.text("DEATH MATCH").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)),
	ENDED(Component.text("ENDED").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD)),
	;

	private final Component displayName;

	GameState(Component displayName) {
		this.displayName = displayName;
	}

	public Component getDisplayName() {
		return displayName;
	}
}
