package dev.kyriji.feature.game;

import dev.kyriji.TheFloorIsLava;
import dev.kyriji.feature.chat.LuckPermsManager;
import dev.kyriji.feature.chat.ScoreboardManager;
import dev.kyriji.feature.effect.BossBarManager;
import dev.kyriji.feature.game.enums.GameState;
import dev.kyriji.feature.game.model.Game;
import dev.kyriji.feature.game.model.GameEvent;
import dev.kyriji.feature.lifelink.LifeLink;
import dev.kyriji.feature.lifelink.LifeLinkManager;
import dev.kyriji.feature.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.List;

public class GameListener implements Listener {

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		killPlayer(event.getEntity());
	}

	@EventHandler
	public void onSnowballHit(ProjectileHitEvent event) {
		if(event.getEntity().getType() != EntityType.SNOWBALL) return;
		if(event.getHitEntity() == null) return;

		if(!(event.getHitEntity() instanceof Player hitPlayer)) return;
		hitPlayer.damage(1);

		double knockBackStrength = 1;
		Vector knockBack = event.getEntity().getVelocity().normalize().multiply(knockBackStrength);
		hitPlayer.setVelocity(event.getEntity().getVelocity().add(knockBack));
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPVP(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

		Game game = GameManager.INSTANCE.getGame();
		GameState gameState = game.getGameState();

		GameEvent currentEvent = game.getCurrentEvent();
		boolean isPvpEvent = currentEvent != null && currentEvent == EventManager.INSTANCE.getEvent("pvp");

		if(gameState != GameState.DEATH_MATCH && !isPvpEvent) event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
		GameState gameState = GameManager.INSTANCE.getGame().getGameState();
		if(gameState == GameState.WAITING) event.setSpawnLocation(WorldManager.LOBBY_SPAWN);
		else event.setSpawnLocation(WorldManager.MAP_SPAWN);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		event.getPlayer().setResourcePack("usercontent.smd.gg/ue07jxo");

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
		ScoreboardManager.INSTANCE.removePlayer(event.getPlayer());

		LifeLink lifeLink = LifeLinkManager.getLifeLink(event.getPlayer());

		GameState gameState = GameManager.INSTANCE.getGame().getGameState();

		if(lifeLink != null && gameState == GameState.WAITING) {
			LifeLinkManager.removeLifeLink(lifeLink);
		}

		if(gameState == GameState.WAITING || gameState == GameState.ENDED) return;

		killPlayer(event.getPlayer());
		GameManager.INSTANCE.getGame().removePlayer(event.getPlayer());
	}


	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.getPlayer().getWorld() == WorldManager.getLobbyWorld()) event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		int y = event.getBlock().getY();
		int currentLavaLevel = GameManager.INSTANCE.getGame().getLavaLevel();

		if(event.getPlayer().getWorld() == WorldManager.getLobbyWorld() || y < currentLavaLevel) event.setCancelled(true);

	}

	@EventHandler
	public void onHungerLoss(FoodLevelChangeEvent event) {
		if(event.getEntity().getWorld() == WorldManager.getLobbyWorld()) event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity().getWorld() == WorldManager.getLobbyWorld()) {
			event.setCancelled(true);

			if(event.getDamageSource().getDamageType() == DamageType.OUT_OF_WORLD) {
				event.getEntity().teleport(WorldManager.LOBBY_SPAWN);
			}
		}
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(WorldManager.MAP_SPAWN);
	}

	@EventHandler
	public void onChestOpen(PlayerInteractEvent event) {
		List<Player> alivePlayers = GameManager.INSTANCE.getGame().getAlivePlayers();

		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if(event.getClickedBlock().getType() != Material.CHEST) return;
		if(alivePlayers.contains(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onGamemodeChange(PlayerGameModeChangeEvent event) {
		Game game = GameManager.INSTANCE.getGame();

		if(game.getGameState() != GameState.WAITING) return;

		if(event.getNewGameMode() == GameMode.CREATIVE) game.removePlayer(event.getPlayer());
		else if(event.getNewGameMode() == GameMode.SURVIVAL) game.addPlayer(event.getPlayer());
	}
	
	public void killPlayer(Player player) {
		Game game = GameManager.INSTANCE.getGame();
		if(!game.getAlivePlayers().contains(player)) return;
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
