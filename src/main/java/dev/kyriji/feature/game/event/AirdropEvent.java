package dev.kyriji.feature.game.event;

import dev.kyriji.TheFloorIsLava;
import dev.kyriji.feature.chat.ScoreboardManager;
import dev.kyriji.feature.game.model.AirDrop;
import dev.kyriji.feature.game.model.GameEvent;
import dev.kyriji.feature.world.WorldManager;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AirdropEvent implements GameEvent {
	public List<AirDrop> airDrops = new ArrayList<>();

	public AirdropEvent() {
		startVelocityTimer();
	}

	@Override
	public String getIdentifier() {
		return "";
	}

	@Override
	public String getDisplayName() {
		return "&e&lAIR DROPS";
	}

	@Override
	public List<String> getDescription() {
		return List.of("&7A series of &eAir Drops &7will be dropped", "&7around the map. Be the first to get them!");
	}

	@Override
	public void start() {
		Location firstCorner = WorldManager.FIRST_CORNER;
		Location secondCorner = WorldManager.SECOND_CORNER;

		for(int i = 0; i < 5; i++) {
			double x = (int) (firstCorner.getX() + Math.random() * (secondCorner.getX() - firstCorner.getX()));
			double z = (int) (firstCorner.getZ() + Math.random() * (secondCorner.getZ() - firstCorner.getZ()));
			x += 0.5;
			z += 0.5;

			Location location = new Location(firstCorner.getWorld(), x, 350, z);

			AirDrop airDrop = new AirDrop(location);
			airDrop.spawn();
			airDrops.add(airDrop);
		}
	}

	@Override
	public void end() {
		airDrops.forEach(AirDrop::remove);
	}


	public void startVelocityTimer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				WorldManager.getWorld().getEntities().forEach(entity -> {
					if(entity.getType() != EntityType.FALLING_BLOCK) return;

					entity.setVelocity(entity.getVelocity().setY(-0.75));
				});
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 0L, 1L);
	}
}
