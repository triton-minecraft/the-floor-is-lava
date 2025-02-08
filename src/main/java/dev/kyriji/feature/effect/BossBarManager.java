package dev.kyriji.feature.effect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {
	public static BossBarManager INSTANCE;

	private final BossBar bossBar;

	public BossBarManager() {
		INSTANCE = this;
		this.bossBar = Bukkit.createBossBar(ChatColor.AQUA + "" + ChatColor.BOLD + "WAITING FOR PLAYERS", BarColor.BLUE, BarStyle.SOLID);
		reset();
	}

	public void reset() {
		bossBar.setTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "WAITING FOR PLAYERS");
		bossBar.setProgress(1);
		bossBar.setColor(BarColor.BLUE);
	}

	public void addPlayer(Player player) {
		bossBar.addPlayer(player);
	}

	public void setTitle(String title) {
		bossBar.setTitle(title);
	}

	public void setProgress(double progress) {
		bossBar.setProgress(progress);
	}

	public void setColor(BarColor color) {
		bossBar.setColor(color);
	}




}
