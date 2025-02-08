package dev.kyriji.feature.sound;

import dev.kyriji.feature.sound.enums.GameSound;
import org.bukkit.Bukkit;

public class SoundUtils {
	public static void broadcastSound(GameSound sound) {
		Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch()));
	}
}
