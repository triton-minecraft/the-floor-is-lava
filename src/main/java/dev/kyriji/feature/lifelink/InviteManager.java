package dev.kyriji.feature.lifelink;

import dev.kyriji.feature.chat.LuckPermsManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InviteManager {
	public static Map<UUID, UUID> pendingInvites = new HashMap<>();

	public static void sendInvite(Player sender, Player receiver) {
		if(pendingInvites.containsKey(receiver.getUniqueId())
				&& pendingInvites.get(receiver.getUniqueId()).equals(sender.getUniqueId())) {

			acceptInvite(sender, receiver);
			return;
		}

		pendingInvites.put(sender.getUniqueId(), receiver.getUniqueId());
		sender.sendMessage(ChatColor.GREEN + "You have sent a life link request to " + LuckPermsManager.getPlayerDisplayName(receiver));
		receiver.sendMessage(ChatColor.GREEN + "You have received a life link request from " + LuckPermsManager.getPlayerDisplayName(sender));
		receiver.sendMessage(ChatColor.GREEN + "Type " + ChatColor.WHITE +"/lifelink " + sender.getName() + ChatColor.GREEN + " to accept");
	}

	public static void acceptInvite(Player player, Player accepted) {
		pendingInvites.remove(player.getUniqueId());
		pendingInvites.remove(accepted.getUniqueId());

		LifeLink lifeLink = new LifeLink(player, accepted);
		LifeLinkManager.registerLifeLink(lifeLink);

		player.sendMessage(ChatColor.GREEN + "You are now life linked with " + LuckPermsManager.getPlayerDisplayName(accepted));
		accepted.sendMessage(ChatColor.GREEN + "You are now life linked with " + LuckPermsManager.getPlayerDisplayName(player));
	}
}
