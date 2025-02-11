package dev.kyriji.feature.chat;

import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import dev.kyriji.TheFloorIsLava;
import dev.kyriji.feature.chat.model.KeyValueSidebarComponent;
import dev.kyriji.feature.chat.model.ValueSidebarComponent;
import dev.kyriji.feature.game.GameManager;
import dev.kyriji.feature.lifelink.LifeLinkManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

public class ScoreboardManager {
	public static ScoreboardManager INSTANCE;
	private final Map<SidebarType, Sidebar> sidebars;
	private final Map<SidebarType, ComponentSidebarLayout> layouts;
	private SidebarType currentType;

	public ScoreboardManager() {
		INSTANCE = this;
		sidebars = new EnumMap<>(SidebarType.class);
		layouts = new EnumMap<>(SidebarType.class);
		sidebars.put(SidebarType.WAITING, createWaitingScoreboard());
		sidebars.put(SidebarType.GAME, createGameScoreboard());
		sidebars.put(SidebarType.ENDED, createEndingSidebar());
		currentType = SidebarType.WAITING;
		startUpdating();
	}

	private void startUpdating() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (SidebarType type : sidebars.keySet()) {
					ComponentSidebarLayout layout = layouts.get(type);
					Sidebar sidebar = sidebars.get(type);
					if (layout != null && sidebar != null) {
						layout.apply(sidebar);
					}
				}
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 0L, 1L);
	}

	private Sidebar createWaitingScoreboard() {
		Sidebar sidebar = TheFloorIsLava.scoreboardLibrary.createSidebar();
		SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
		SidebarComponent lines = SidebarComponent.builder()
				.addDynamicLine(() -> Component.text(dtf.format(new Date()), NamedTextColor.GRAY))
				.addBlankLine()
				.addStaticLine(Component.text("Waiting for players...", NamedTextColor.GRAY))
				.addBlankLine()
				.addStaticLine(Component.text("tritonmc.com", NamedTextColor.AQUA))
				.build();
		ComponentSidebarLayout layout = new ComponentSidebarLayout(
				SidebarComponent.staticLine(Component.text("THE FLOOR IS LAVA", NamedTextColor.GOLD, TextDecoration.BOLD)),
				lines
		);
		layout.apply(sidebar);
		layouts.put(SidebarType.WAITING, layout);
		return sidebar;
	}

	private Sidebar createGameScoreboard() {
		Sidebar sidebar = TheFloorIsLava.scoreboardLibrary.createSidebar();
		SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
		SidebarComponent lines = SidebarComponent.builder()
				.addDynamicLine(() -> Component.text(dtf.format(new Date()), NamedTextColor.GRAY))
				.addBlankLine()
				.addComponent(new ValueSidebarComponent(() -> GameManager.INSTANCE.getGame().getGameState().getDisplayName()))
				.addBlankLine()
				.addDynamicLine(() -> Component.text("Event: ")
						.append(Component.text(LuckPermsManager.formatMessage(GameManager.INSTANCE.getGame().getCurrentEventString()))))
				.addDynamicLine(() -> Component.text(LuckPermsManager.formatMessage("Next Event: "))
						.append(Component.text(LuckPermsManager.formatMessage("&a" + GameManager.INSTANCE.getNextEventTime()))))
				.addBlankLine()
				.addComponent(new KeyValueSidebarComponent(
						Component.text("Alive players"),
						() -> Component.text(GameManager.INSTANCE.getGame().getAlivePlayers().size(), NamedTextColor.GREEN)
				))
				.addComponent(new KeyValueSidebarComponent(
						Component.text("Alive teams"),
						() -> Component.text(LifeLinkManager.lifeLinks.size(), NamedTextColor.GREEN)
				))
				.addBlankLine()
				.addStaticLine(Component.text("tritonmc.com", NamedTextColor.AQUA))
				.build();
		ComponentSidebarLayout layout = new ComponentSidebarLayout(
				SidebarComponent.staticLine(Component.text("THE FLOOR IS LAVA", NamedTextColor.GOLD, TextDecoration.BOLD)),
				lines
		);
		layout.apply(sidebar);
		layouts.put(SidebarType.GAME, layout);
		return sidebar;
	}

	private Sidebar createEndingSidebar() {
		Sidebar sidebar = TheFloorIsLava.scoreboardLibrary.createSidebar();
		SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
		SidebarComponent lines = SidebarComponent.builder()
				.addDynamicLine(() -> Component.text(dtf.format(new Date()), NamedTextColor.GRAY))
				.addBlankLine()
				.addStaticLine(Component.text("GAME OVER", NamedTextColor.RED, TextDecoration.BOLD))
				.addBlankLine()
				.addStaticLine(Component.text("tritonmc.com", NamedTextColor.AQUA))
				.build();
		ComponentSidebarLayout layout = new ComponentSidebarLayout(
				SidebarComponent.staticLine(Component.text("THE FLOOR IS LAVA", NamedTextColor.GOLD, TextDecoration.BOLD)),
				lines
		);
		layout.apply(sidebar);
		layouts.put(SidebarType.ENDED, layout);
		return sidebar;
	}

	public void addPlayer(Player player) {
		Sidebar sidebar = sidebars.get(currentType);
		if (sidebar != null) {
			sidebar.addPlayer(player);
		}
	}

	public void removePlayer(Player player) {
		Sidebar sidebar = sidebars.get(currentType);
		if (sidebar != null) {
			sidebar.removePlayer(player);
		}
	}

	public void switchSidebar(SidebarType type) {
		Sidebar oldSidebar = sidebars.get(currentType);
		if (oldSidebar != null) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				oldSidebar.removePlayer(player);
			}
		}
		currentType = type;
		Sidebar newSidebar = sidebars.get(currentType);
		if (newSidebar != null) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				newSidebar.addPlayer(player);
			}
		}
	}

	public enum SidebarType {
		WAITING,
		GAME,
		ENDED
	}
}