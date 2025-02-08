package dev.kyriji.feature.effect;

import dev.kyriji.TheFloorIsLava;
import dev.kyriji.feature.world.WorldManager;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

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
}
