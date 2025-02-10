package dev.kyriji.feature.effect;

import dev.kyriji.TheFloorIsLava;
import dev.kyriji.feature.world.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.concurrent.ThreadLocalRandom;

public class EffectUtils {
	public static final int FIREWORK_SPAWN_RADIUS = 35;
	public static final int FIREWORK_COUNT = 250;

	public static void launchFireworks() {
		Location corner1 = WorldManager.FIRST_CORNER;
		Location corner2 = WorldManager.SECOND_CORNER;

		double minX = Math.min(corner1.getX(), corner2.getX());
		double maxX = Math.max(corner1.getX(), corner2.getX());
		double minZ = Math.min(corner1.getZ(), corner2.getZ());
		double maxZ = Math.max(corner1.getZ(), corner2.getZ());
		World world = corner1.getWorld();

		new BukkitRunnable() {
			int launches = FIREWORK_COUNT;

			@Override
			public void run() {
				if (launches <= 0) {
					cancel();
					return;
				}
				double x = ThreadLocalRandom.current().nextDouble(minX, maxX);
				double z = ThreadLocalRandom.current().nextDouble(minZ, maxZ);
				double y = world.getHighestBlockYAt((int) x, (int) z) + 1;

				Location launchLocation = new Location(world, x, y, z);

				FireworkEffect.Type type = FireworkEffect.Type.values()[ThreadLocalRandom.current().nextInt(FireworkEffect.Type.values().length)];
				Color color = Color.fromRGB(ThreadLocalRandom.current().nextInt(256), ThreadLocalRandom.current().nextInt(256), ThreadLocalRandom.current().nextInt(256));

				Firework fw = world.spawn(launchLocation, Firework.class);
				FireworkMeta fm = fw.getFireworkMeta();
				fm.setPower(ThreadLocalRandom.current().nextInt(1, 3));
				fm.addEffect(FireworkEffect.builder().with(type).withColor(color).build());
				fw.setFireworkMeta(fm);

				launches--;
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 0, 1);
	}

	public static void spawnHolograms() {
		TextDisplay textDisplay = (TextDisplay) WorldManager.getLobbyWorld().spawnEntity(WorldManager.LOBBY_HOLOGRAM_SPAWN, EntityType.TEXT_DISPLAY);
		textDisplay.setText(ChatColor.GOLD + "" + ChatColor.BOLD + "The Floor is Lava!");
		textDisplay.setGravity(false);
		textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);

		Vector3f translation = new Vector3f(0, 0, 0);
		Vector3f scale = new Vector3f(10, 10, 10);
		AxisAngle4f rightRotation = new AxisAngle4f(0, 0, 0, 0);
		AxisAngle4f leftRotation = new AxisAngle4f(0, 0, 0, 0);
		textDisplay.setTransformation(new Transformation(translation, leftRotation, scale, rightRotation));

		String instructions = ChatColor.translateAlternateColorCodes('&', "&7This game is played with a &dPartner&7.\n" +
				"&7Use &f/lifelink <player>&7 to link with someone.");

		TextDisplay instructionsDisplay = (TextDisplay) WorldManager.getLobbyWorld().spawnEntity(WorldManager.LOBBY_HOLOGRAM_SPAWN.clone().add(0, -8, 0), EntityType.TEXT_DISPLAY);
		instructionsDisplay.setText(instructions);
		instructionsDisplay.setGravity(false);
		instructionsDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
		scale = new Vector3f(7, 7, 7);
		instructionsDisplay.setTransformation(new Transformation(translation, leftRotation, scale, rightRotation));
	}
}
