package dev.kyriji.command;

import dev.kyriji.feature.chat.LuckPermsManager;
import dev.kyriji.feature.game.GameManager;
import dev.kyriji.feature.game.enums.GameState;
import dev.kyriji.feature.lifelink.LifeLink;
import dev.kyriji.feature.lifelink.LifeLinkManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("You must be a player to use this command!");
			return false;
		}

		if (!player.hasPermission("tmc.staff")) {
			player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return false;
		}

		if (args.length != 1 || !args[0].equalsIgnoreCase("start")) {
			player.sendMessage(ChatColor.RED + "Usage: /game start");
			return false;
		}

		if (GameManager.INSTANCE.getGame().getGameState() != GameState.WAITING) {
			player.sendMessage(ChatColor.RED + "The game is already running!");
			return false;
		}

		List<Player> unLinkedPlayers = new ArrayList<>();
		GameManager.INSTANCE.getGame().getAlivePlayers().forEach(onlinePlayer -> {
			LifeLink lifeLink = LifeLinkManager.getLifeLink(onlinePlayer);
			if (lifeLink == null) unLinkedPlayers.add(onlinePlayer);
		});

		if(!unLinkedPlayers.isEmpty()) {
			player.sendMessage(ChatColor.RED + "The following players are not life linked: ");
			player.sendMessage(LuckPermsManager.formatMessage("&7&m--------------------------------&r"));
			unLinkedPlayers.forEach(unLinkedPlayer -> player.sendMessage(ChatColor.RED + unLinkedPlayer.getName()));
			player.sendMessage(LuckPermsManager.formatMessage("&7&m--------------------------------&r"));
			return false;
		}

		if(LifeLinkManager.lifeLinks.size() < 2) {
			player.sendMessage(ChatColor.RED + "There must be at least 2 life links to start the game!");
			return false;
		}

		GameManager.INSTANCE.startGame();
		player.sendMessage(ChatColor.GREEN + "Game started!");
		return true;
	}
}
