package dev.kyriji.feature.game.event;

import dev.kyriji.feature.game.model.AirDrop;
import dev.kyriji.feature.game.model.GameEvent;
import dev.kyriji.feature.world.WorldManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

import java.util.List;

public class TntRainEvent implements GameEvent {
	@Override
	public String getIdentifier() {
		return "tnt_rain";
	}

	@Override
	public String getDisplayName() {
		return "&4&lTNT RAIN";
	}

	@Override
	public List<String> getDescription() {
		return List.of("&7TNT will rain from the sky", "&7Be careful not to get blown up!");
	}

	@Override
	public void start() {
		Location firstCorner = WorldManager.FIRST_CORNER;
		Location secondCorner = WorldManager.SECOND_CORNER;

		for(int i = 0; i < 150; i++) {
			double x = firstCorner.getX() + Math.random() * (secondCorner.getX() - firstCorner.getX());
			double z = firstCorner.getZ() + Math.random() * (secondCorner.getZ() - firstCorner.getZ());
			Location location = new Location(firstCorner.getWorld(), x, 350, z);

			TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.TNT);
			tnt.setFuseTicks(20 * 10);
			tnt.setGlowing(true);
		}
	}

	@Override
	public void end() {

	}
}
