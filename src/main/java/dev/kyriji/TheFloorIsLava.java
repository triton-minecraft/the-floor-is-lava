package dev.kyriji;

import dev.kyriji.command.GameCommand;
import dev.kyriji.command.LifeLinkCommand;
import dev.kyriji.feature.chat.ActionBarManager;
import dev.kyriji.feature.chat.ScoreboardManager;
import dev.kyriji.feature.effect.BossBarManager;
import dev.kyriji.feature.game.GameListener;
import dev.kyriji.feature.game.GameManager;
import dev.kyriji.feature.game.TeamManager;
import dev.kyriji.feature.game.model.Game;
import dev.kyriji.feature.lifelink.LifeLinkListener;

import dev.kyriji.feature.world.WorldManager;
import dev.wiji.bigminecraftapi.BigMinecraftAPI;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import org.bukkit.plugin.java.JavaPlugin;

public final class TheFloorIsLava extends JavaPlugin {
	public static JavaPlugin INSTANCE;
	public static ScoreboardLibrary scoreboardLibrary;

	@Override
	public void onEnable() {
		INSTANCE = this;
		BigMinecraftAPI.init();

		System.out.println("TheFloorIsLava enabled!");

		getServer().getPluginManager().registerEvents(new LifeLinkListener(), this);
		getServer().getPluginManager().registerEvents(new GameListener(), this);

		getCommand("lifelink").setExecutor(new LifeLinkCommand());
		getCommand("game").setExecutor(new GameCommand());


		getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
			new GameManager(new Game());
			new BossBarManager();
			new ScoreboardManager();
			new ActionBarManager();
			new TeamManager();

			WorldManager.pasteLobbySchematic();
		});

		try {
			scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(this);
		} catch (NoPacketAdapterAvailableException e) {
			scoreboardLibrary = new NoopScoreboardLibrary();
			this.getLogger().warning("No scoreboard packet adapter available!");
		}
	}

	@Override
	public void onDisable() {
		System.out.println("TheFloorIsLava disabled!");
		scoreboardLibrary.close();
	}
}
