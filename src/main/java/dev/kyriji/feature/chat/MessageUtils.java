package dev.kyriji.feature.chat;

import dev.kyriji.feature.game.model.Game;
import dev.kyriji.feature.game.model.GameEvent;
import org.bukkit.Bukkit;

public class MessageUtils {
	public static void sendStartMessage() {
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&7&m                                            &r"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&6&lTHE FLOOR IS LAVA"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&r"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&6Lava &awill start rising after a short grace period."));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&aUse this time to prepare with your partner."));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&7&m                                            &r"));
	}

	public static void sendRisingMessage() {
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&7&m                                            &r"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&6&lTHE FLOOR IS LAVA"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&r"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&6Lava &cis now rising!"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&cGet to high ground to stay safe."));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&7&m                                            &r"));
	}

	public static void sendDeathmatchMessage() {
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&7&m                                            &r"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&6&lTHE FLOOR IS LAVA"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&r"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&c&lDEATHMATCH &7has started."));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&cPvP &7is enabled and the game will end soon."));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&7&m                                            &r"));
	}

	public static void broadcastTitle(String title) {
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.sendTitle(LuckPermsManager.formatMessage(title), null, 10, 70, 20);
		});
	}

	public static void sendEventMessage(GameEvent event) {
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&7&m                                            &r"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&a&lRANDOM EVENT"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&r"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage(event.getDisplayName()));
		event.getDescription().forEach(description -> Bukkit.broadcastMessage(LuckPermsManager.formatMessage(description)));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&7&m                                            &r"));
	}
}
