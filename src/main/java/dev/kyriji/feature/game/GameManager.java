package dev.kyriji.feature.game;

import dev.kyriji.TheFloorIsLava;
import dev.kyriji.feature.chat.LuckPermsManager;
import dev.kyriji.feature.chat.MessageUtils;
import dev.kyriji.feature.chat.ScoreboardManager;
import dev.kyriji.feature.effect.BossBarManager;
import dev.kyriji.feature.effect.EffectUtils;
import dev.kyriji.feature.game.enums.GameState;
import dev.kyriji.feature.game.model.Game;
import dev.kyriji.feature.game.model.GameEvent;
import dev.kyriji.feature.lifelink.LifeLink;
import dev.kyriji.feature.lifelink.LifeLinkManager;
import dev.kyriji.feature.sound.SoundUtils;
import dev.kyriji.feature.sound.enums.GameSound;
import dev.kyriji.feature.world.SchematicUtils;
import dev.kyriji.feature.world.WorldManager;
import dev.wiji.bigminecraftapi.BigMinecraftAPI;
import dev.wiji.bigminecraftapi.controllers.NetworkManager;
import dev.wiji.bigminecraftapi.enums.InstanceState;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
	public static GameManager INSTANCE;

	public static final int START_TIMER_SECONDS = 30;
	public static final int GRACE_PERIOD_MINUTES = 3;
	public static final int LAVA_RISE_INTERVAL_SECONDS = 2;
	public static final int DEATH_MATCH_MINUTES = 2;
	public static final int MAX_LAVA_LEVEL = 317;

	private final Game game;
	private BukkitTask gameTask;

	private int randomEventSeconds = getEventRandomSeconds();

	public GameManager(Game game) {
		INSTANCE = this;
		this.game = game;

		File schematicFile = new File(TheFloorIsLava.INSTANCE.getDataFolder(), "map.schem");
		SchematicUtils.pasteSchematic(schematicFile, WorldManager.MAP_SCHEMATIC_LOCATION);
	}

	public void startGame() {
		startGameTimer();
	}

	private void startGameTimer() {
		BossBarManager bossBar = BossBarManager.INSTANCE;
		bossBar.setTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + "GAME STARTING");
		bossBar.setColor(BarColor.YELLOW);

		game.setGameState(GameState.WAITING);

		gameTask = new BukkitRunnable() {
			int seconds = START_TIMER_SECONDS;

			@Override
			public void run() {
				bossBar.setProgress((double) seconds / START_TIMER_SECONDS);

				switch (seconds) {
					case 30:
						broadcastStartTimer("&eThe game will start in &c30 &eseconds.");
						break;
					case 20:
						broadcastStartTimer("&eThe game will start in &c20 &eseconds.");
						break;
					case 10:
						broadcastStartTimer("&eThe game will start in &c10 &eseconds.");
						break;
					case 5:
						broadcastStartTimer("&eThe game will start in &c5 &eseconds.");
						MessageUtils.broadcastTitle("&c5");
						break;
					case 4:
						broadcastStartTimer("&eThe game will start in &c4 &eseconds.");
						MessageUtils.broadcastTitle("&c4");
						break;
					case 3:
						broadcastStartTimer("&eThe game will start in &c3 &eseconds.");
						MessageUtils.broadcastTitle("&c3");
						break;
					case 2:
						broadcastStartTimer("&eThe game will start in &c2 &eseconds.");
						MessageUtils.broadcastTitle("&c2");
						break;
					case 1:
						broadcastStartTimer("&eThe game will start in &c1 &esecond.");
						MessageUtils.broadcastTitle("&c1");
						break;
					case 0:
						break;
				}

				if (seconds <= 0) {
					cancel();
					startGracePeriod();
					spawnPlayers();
					ScoreboardManager.INSTANCE.switchSidebar(ScoreboardManager.SidebarType.GAME);
				}

				seconds--;
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 0, 20);
	}

	private void broadcastStartTimer(String message) {
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&6&l[&c&l!&6&l] &e" + message));
		SoundUtils.broadcastSound(GameSound.TICK);
	}

	private void startGracePeriod() {
		BossBarManager bossBar = BossBarManager.INSTANCE;
		bossBar.setTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "GRACE PERIOD");
		bossBar.setColor(org.bukkit.boss.BarColor.GREEN);

		MessageUtils.sendStartMessage();
		game.setGameState(GameState.GRACE_PERIOD);

		gameTask = new BukkitRunnable() {
			int seconds = GRACE_PERIOD_MINUTES * 60;

			@Override
			public void run() {
				if(seconds == GRACE_PERIOD_MINUTES * 60) SoundUtils.broadcastSound(GameSound.ALERT);

				seconds--;
				bossBar.setProgress((double) seconds / (GRACE_PERIOD_MINUTES * 60));

				if(seconds <= 0) {
					cancel();
					startGameLoop();
				}
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 2, 20);
	}

	private void startGameLoop() {
		BossBarManager bossBar = BossBarManager.INSTANCE;
		bossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "THE LAVA IS RISING");
		bossBar.setColor(BarColor.YELLOW);
		SoundUtils.broadcastSound(GameSound.ALERT);

		MessageUtils.sendRisingMessage();

		game.setGameState(GameState.RISING_LAVA);

		gameTask = new BukkitRunnable() {
			int lavaRiseSeconds = LAVA_RISE_INTERVAL_SECONDS;
			int totalSeconds = 0;

			@Override
			public void run() {
				lavaRiseSeconds--;
				randomEventSeconds--;
				totalSeconds++;

				bossBar.setProgress(Math.min(1.0, (double) totalSeconds / ((MAX_LAVA_LEVEL + 64) * LAVA_RISE_INTERVAL_SECONDS)));

				if(lavaRiseSeconds <= 0) {
					if(game.getLavaLevel() >= MAX_LAVA_LEVEL) {
						cancel();
						startDeathMatch();
					}

					lavaRiseSeconds = LAVA_RISE_INTERVAL_SECONDS;
					game.riseLava();
				}

				if(randomEventSeconds <= 0) {
					randomEventSeconds = getEventRandomSeconds();
					GameEvent currentEvent = game.getCurrentEvent();

					if(currentEvent != null) {
						currentEvent.end();
						game.setCurrentEvent(null);
					}

					GameEvent newEvent = EventManager.INSTANCE.getRandomEvent();
					game.setCurrentEvent(newEvent);
					newEvent.start();

					MessageUtils.broadcastTitle(newEvent.getDisplayName());
					MessageUtils.sendEventMessage(newEvent);
					SoundUtils.broadcastSound(GameSound.ALERT);
				}
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 0, 20);
	}

	public void startDeathMatch() {
		BossBarManager bossBar = BossBarManager.INSTANCE;
		bossBar.setTitle(ChatColor.RED + "" + ChatColor.BOLD + "DEATH MATCH");
		bossBar.setColor(org.bukkit.boss.BarColor.RED);
		SoundUtils.broadcastSound(GameSound.DEATH_MATCH);

		spawnEnderDragon();
		MessageUtils.sendDeathmatchMessage();

		game.setGameState(GameState.DEATH_MATCH);

		gameTask = new BukkitRunnable() {
			int seconds = DEATH_MATCH_MINUTES * 60;
			@Override
			public void run() {
				seconds--;
				bossBar.setProgress((double) seconds / (DEATH_MATCH_MINUTES * 60));

				if(seconds <= 0) {
					endGame();
					cancel();
				}
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 0, 20);
	}

	public void endGame() {
		if(game.getGameState() == GameState.ENDED) return;

		game.setGameState(GameState.ENDED);
		gameTask.cancel();

		EffectUtils.launchFireworks();
		ScoreboardManager.INSTANCE.switchSidebar(ScoreboardManager.SidebarType.ENDED);

		List<LifeLink> winners = new ArrayList<>();
		for(Player alivePlayer : game.getAlivePlayers()) {
			LifeLink lifeLink = LifeLinkManager.getLifeLink(alivePlayer);
			if(lifeLink != null && !winners.contains(lifeLink)) winners.add(lifeLink);

			alivePlayer.setGameMode(GameMode.SPECTATOR);
		}

		StringBuilder winnersMessage = new StringBuilder();

		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&7&m                                            &r"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&c&lGAME OVER &7- &6&lTHE FLOOR IS LAVA"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&r"));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&eWinners:"));
		for(LifeLink winner : winners) {
			winnersMessage.append(LuckPermsManager.getPlayerDisplayName(winner.getPlayerOne()));
			winnersMessage.append(" &7and ");
			winnersMessage.append(LuckPermsManager.getPlayerDisplayName(winner.getPlayerTwo()));
			winnersMessage.append(", ");
		}
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage(winnersMessage.toString()));
		Bukkit.broadcastMessage(LuckPermsManager.formatMessage("&7&m                                            &r"));

		gameTask = new BukkitRunnable() {
			int seconds = 20;

			@Override
			public void run() {
				seconds--;
				if(seconds <= 0) {
					cancel();

					Bukkit.getOnlinePlayers().forEach(player -> {
						BigMinecraftAPI.getNetworkManager().queuePlayer(player.getUniqueId(), "lobby");
					});

					Bukkit.getScheduler().runTaskLater(TheFloorIsLava.INSTANCE, () -> {
						NetworkManager.setInstanceState(InstanceState.STOPPING);
					}, 20 * 2);
				}
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 0, 20);
	}

	public Game getGame() {
		return game;
	}

	public void spawnPlayers() {
		game.getAlivePlayers().forEach(player -> {
			Location spawnLocation = WorldManager.MAP_SPAWN.clone();
			spawnLocation.add(Math.random() * 10 - 5, 0, Math.random() * 10 - 5);

			player.teleport(spawnLocation);
			player.setGameMode(GameMode.SURVIVAL);

			player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
			player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, GameManager.GRACE_PERIOD_MINUTES * 60 * 20, 10, false, false));

			new BukkitRunnable() {
				@Override
				public void run() {
					player.setGliding(true);
				}
			}.runTaskLater(TheFloorIsLava.INSTANCE, 20);
		});
	}

	public String getNextEventTime() {
		GameState gameState = game.getGameState();
		if(gameState == GameState.GRACE_PERIOD) return "N/A";

		int minutes = randomEventSeconds / 60;
		int seconds = randomEventSeconds % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}

	public void spawnEnderDragon() {
		Location spawn = WorldManager.MAP_SPAWN;

		GameManager.INSTANCE.getGame().getAlivePlayers().forEach(player -> {
			EnderDragon dragon = (EnderDragon) spawn.getWorld().spawnEntity(spawn, EntityType.ENDER_DRAGON);
			dragon.setPhase(EnderDragon.Phase.BREATH_ATTACK);
			dragon.setTarget(player);

			ClientboundBossEventPacket packet = ClientboundBossEventPacket.createRemovePacket(dragon.getUniqueId());
			Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
				ServerPlayer serverPlayer = ((CraftPlayer) onlinePlayer).getHandle();
				serverPlayer.connection.send(packet);
			});
		});
	}

	private int getEventRandomSeconds() {
		return (int) (Math.random() * 60) + 30;
	}
}