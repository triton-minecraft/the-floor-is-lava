package dev.kyriji.command;

import dev.kyriji.feature.lifelink.InviteManager;
import dev.kyriji.feature.lifelink.LifeLinkManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LifeLinkCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player player)) {
			sender.sendMessage("You must be a player to use this command!");
			return false;
		}

		if(args.length != 1) {
			player.sendMessage(ChatColor.RED + "Usage: /lifelink <player>");
			return false;
		}

		Player target = player.getServer().getPlayer(args[0]);
		if(target == null) {
			player.sendMessage(ChatColor.RED + "Player not found!");
			return false;
		}

		if(target.equals(player)) {
			player.sendMessage(ChatColor.RED + "You can't life link with yourself!");
			return false;
		}

		if(LifeLinkManager.getLifeLink(player) != null) {
			player.sendMessage(ChatColor.RED + "You are already life linked with someone!");
			return false;
		}

		InviteManager.sendInvite(player, target);
		return false;
	}
}
