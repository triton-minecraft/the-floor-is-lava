package dev.kyriji.feature.game;

import dev.kyriji.TheFloorIsLava;
import dev.kyriji.feature.chat.LuckPermsManager;
import dev.kyriji.feature.chat.ScoreboardManager;
import dev.kyriji.feature.effect.BossBarManager;
import dev.kyriji.feature.game.enums.GameState;
import dev.kyriji.feature.game.model.Game;
import dev.kyriji.feature.lifelink.LifeLink;
import dev.kyriji.feature.lifelink.LifeLinkManager;
import dev.kyriji.feature.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class GameListener implements Listener {

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		killPlayer(event.getEntity());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPVP(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

		Game game = GameManager.INSTANCE.getGame();
		GameState gameState = game.getGameState();

		if(gameState != GameState.DEATH_MATCH) event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
		GameState gameState = GameManager.INSTANCE.getGame().getGameState();
		if(gameState == GameState.WAITING) event.setSpawnLocation(WorldManager.LOBBY_SPAWN);
		else event.setSpawnLocation(WorldManager.MAP_SPAWN);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		System.out.println("Player joined");
		event.setJoinMessage(null);

		WorldManager.init();
		ScoreboardManager.INSTANCE.addPlayer(event.getPlayer());
		BossBarManager.INSTANCE.addPlayer(event.getPlayer());

		event.getPlayer().getInventory().clear();

		GameState gameState = GameManager.INSTANCE.getGame().getGameState();

		if(gameState == GameState.WAITING) {
			event.getPlayer().teleport(WorldManager.LOBBY_SPAWN);
			event.getPlayer().setGameMode(GameMode.SURVIVAL);
			GameManager.INSTANCE.getGame().addPlayer(event.getPlayer());
		} else {
			event.getPlayer().teleport(WorldManager.MAP_SPAWN);
			event.getPlayer().setGameMode(GameMode.SPECTATOR);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				System.out.println("Checking if player is in lobby world");
				if(gameState != GameState.WAITING) {
					System.out.println("Game is not in waiting state");
					cancel();
					return;
				}

				World currentWorld = event.getPlayer().getWorld();

				if(currentWorld == WorldManager.getLobbyWorld()) {
					System.out.println("Player is in lobby world");
					cancel();
					return;
				}

				System.out.println("Teleporting player to lobby world");
				event.getPlayer().teleport(WorldManager.LOBBY_SPAWN);
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 0, 1);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		GameManager.INSTANCE.getGame().removePlayer(event.getPlayer());
		ScoreboardManager.INSTANCE.removePlayer(event.getPlayer());

		LifeLink lifeLink = LifeLinkManager.getLifeLink(event.getPlayer());

		if(lifeLink != null) {
			Player otherPlayer = lifeLink.getPlayerOne().equals(event.getPlayer()) ? lifeLink.getPlayerTwo() : lifeLink.getPlayerOne();
			otherPlayer.damage(1000);

			LifeLinkManager.removeLifeLink(lifeLink);
		}

		GameState gameState = GameManager.INSTANCE.getGame().getGameState();
		if(gameState == GameState.WAITING || gameState == GameState.ENDED) return;

		killPlayer(event.getPlayer());
	}


	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.getPlayer().getWorld() == WorldManager.getLobbyWorld()) event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.getPlayer().getWorld() == WorldManager.getLobbyWorld()) event.setCancelled(true);
	}

	@EventHandler
	public void onHungerLoss(FoodLevelChangeEvent event) {
		if(event.getEntity().getWorld() == WorldManager.getLobbyWorld()) event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity().getWorld() == WorldManager.getLobbyWorld()) event.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(WorldManager.MAP_SPAWN);
	}
	
	public void killPlayer(Player player) {
		Game game = GameManager.INSTANCE.getGame();
		game.removePlayer(player);

		String deathMessage = LuckPermsManager.formatMessage(LuckPermsManager.getPlayerDisplayName(player) + " &chas died.");
		player.getWorld().strikeLightningEffect(player.getLocation());
		Bukkit.broadcastMessage(deathMessage);

		GameState gameState = game.getGameState();
		player.setGameMode(GameMode.SPECTATOR);

		if(gameState == GameState.WAITING || gameState == GameState.ENDED) return;

		if(game.getAlivePlayers().size() > 2) return;

		int lifeLinkCount = 0;
		for(Player alivePlayer : game.getAlivePlayers()) {
			LifeLink lifeLink = LifeLinkManager.getLifeLink(alivePlayer);
			if(lifeLink != null) lifeLinkCount++;
		}

		if(lifeLinkCount <= 1) {
			new BukkitRunnable() {
				@Override
				public void run() {
					GameManager.INSTANCE.endGame();
				}
			}.runTaskLater(TheFloorIsLava.INSTANCE, 5);
		}
	}
}
