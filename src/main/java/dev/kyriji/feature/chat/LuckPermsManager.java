package dev.kyriji.feature.chat;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LuckPermsManager {
	public static LuckPerms api = LuckPermsProvider.get();

	public static String getPlayerDisplayName(Player player) {
		if(api == null) throw new NullPointerException("LuckPerms API not found");

		User user = api.getUserManager().getUser(player.getUniqueId());

		if(user == null) {
			try {
				user = api.getUserManager().loadUser(player.getUniqueId()).join();
			} catch (Exception e) {
				e.printStackTrace();
				return formatMessage("&7" + player.getName() + "&r");
			}
		}

		if(user == null) {
			return formatMessage("&7" + player.getName() + "&r");
		}

		String groupName = user.getPrimaryGroup();
		Group group = api.getGroupManager().getGroup(groupName);

		if(group == null) {
			return formatMessage("&7" + player.getName() + "&r");
		}

		String prefix = formatMessage("&7[" + group.getDisplayName() + "&7] ");
		String name = formatMessage(group.getCachedData().getMetaData().getPrefix() + player.getName() + "&r");

		return prefix + name;
	}

	public static String formatMessage(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
}
