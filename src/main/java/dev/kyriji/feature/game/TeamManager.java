package dev.kyriji.feature.game;

import dev.kyriji.feature.lifelink.LifeLink;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeamManager {
	public static TeamManager INSTANCE;
	private final Scoreboard scoreboard;
	public int colorIndex = 0;

	public TeamManager() {
		INSTANCE = this;
		this.scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
	}

	public void addTeam(LifeLink lifeLink) {
		String teamName = "team_" + (lifeLink.getPlayerOne().getUniqueId().toString().substring(0, 5)) +
				"_" + (lifeLink.getPlayerTwo().getUniqueId().toString().substring(0, 5));

		Team team = scoreboard.getTeam(teamName);
		if (team == null) {
			team = scoreboard.registerNewTeam(teamName);
		}

		ChatColor teamColor = getTeamColor();
		team.setColor(teamColor);
		team.setPrefix(teamColor.toString());
		team.setAllowFriendlyFire(false);

		team.addEntry(lifeLink.getPlayerOne().getName());
		team.addEntry(lifeLink.getPlayerTwo().getName());
	}

	public void removeTeam(LifeLink lifeLink) {
		String teamName = "team_" + (lifeLink.getPlayerOne().getUniqueId().toString().substring(0, 5)) +
				"_" + (lifeLink.getPlayerTwo().getUniqueId().toString().substring(0, 5));

		Team team = scoreboard.getTeam(teamName);
		if (team != null) {
			team.unregister();
		}
	}

	public ChatColor getTeamColor() {
		List<ChatColor> colors = getTeamColors();
		if (colorIndex >= colors.size()) colorIndex = 0;
		return colors.get(colorIndex++);
	}

	public List<ChatColor> getTeamColors() {
		List<ChatColor> list = new ArrayList<>();
		list.add(ChatColor.BLUE);
		list.add(ChatColor.GREEN);
		list.add(ChatColor.RED);
		list.add(ChatColor.YELLOW);
		list.add(ChatColor.AQUA);
		list.add(ChatColor.GOLD);
		list.add(ChatColor.LIGHT_PURPLE);
		list.add(ChatColor.DARK_PURPLE);
		list.add(ChatColor.DARK_AQUA);
		list.add(ChatColor.DARK_BLUE);
		list.add(ChatColor.DARK_GRAY);
		list.add(ChatColor.DARK_GREEN);
		list.add(ChatColor.DARK_RED);
		list.add(ChatColor.GRAY);
		list.add(ChatColor.WHITE);

		return list;
	}
}