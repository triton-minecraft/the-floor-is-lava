package dev.kyriji.feature.sound.enums;

import dev.kyriji.feature.game.enums.GameState;
import org.bukkit.Sound;

public enum GameSound {
	TICK(Sound.UI_BUTTON_CLICK, 1.2F, 1F),
	ALERT(Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 1F),
	DEATH_MATCH(Sound.ENTITY_ENDER_DRAGON_GROWL, 1F, 1F)
	;

	private final Sound sound;
	private final float pitch;
	private final float volume;

	GameSound(Sound sound, float pitch, float volume) {
		this.sound = sound;
		this.pitch = pitch;
		this.volume = volume;
	}

	public Sound getSound() {
		return sound;
	}

	public float getPitch() {
		return pitch;
	}

	public float getVolume() {
		return volume;
	}

}
