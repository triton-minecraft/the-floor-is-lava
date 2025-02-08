package dev.kyriji.feature.chat;

import dev.kyriji.TheFloorIsLava;
import dev.kyriji.feature.game.GameManager;
import dev.kyriji.feature.lifelink.LifeLink;
import dev.kyriji.feature.lifelink.LifeLinkManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ActionBarManager {

	public ActionBarManager() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					List<Player> alivePlayers = GameManager.INSTANCE.getGame().getAlivePlayers();
					if(!alivePlayers.contains(player)) player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GREEN + "You are currently spectating"));
					else player.spigot().sendMessage(ChatMessageType.ACTION_BAR, generateActionBarMessage(player));
				}
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 0L, 5L);

	}


	public BaseComponent generateActionBarMessage(Player player) {
		LifeLink lifeLink = LifeLinkManager.getLifeLink(player);
		BaseComponent baseComponent = TextComponent.fromLegacy(ChatColor.translateAlternateColorCodes('&', "&eUse &6/lifelink &eto link your life to another player"));
		if(lifeLink == null) return baseComponent;

		Player linkedPlayer = lifeLink.getPlayerOne().equals(player) ? lifeLink.getPlayerTwo() : lifeLink.getPlayerOne();
		return TextComponent.fromLegacy(ChatColor.translateAlternateColorCodes('&', "&eYou are linked to &6" + LuckPermsManager.getPlayerDisplayName(linkedPlayer)));
	}
}
