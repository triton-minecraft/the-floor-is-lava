package dev.kyriji.feature.game.event;

import dev.kyriji.feature.game.GameManager;
import dev.kyriji.feature.game.model.GameEvent;
import dev.kyriji.feature.world.WorldManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class RationsEvent implements GameEvent {
	@Override
	public String getIdentifier() {
		return "rations";
	}

	@Override
	public String getDisplayName() {
		return "&a&lRATIONS";
	}

	@Override
	public List<String> getDescription() {
		return List.of("&7A variety of different \"animals\" will", "&7rain from the sky, but be careful!");
	}

	@Override
	public void start() {
		List<EntityType> food = List.of(EntityType.COW, EntityType.PIG, EntityType.SHEEP, EntityType.RABBIT, EntityType.CREEPER);

		Location firstCorner = WorldManager.FIRST_CORNER;
		Location secondCorner = WorldManager.SECOND_CORNER;

		int alivePlayers = GameManager.INSTANCE.getGame().getAlivePlayers().size();
		for(int i = 0; i < alivePlayers * 5; i++) {
			double x = firstCorner.getX() + Math.random() * (secondCorner.getX() - firstCorner.getX());
			double z = firstCorner.getZ() + Math.random() * (secondCorner.getZ() - firstCorner.getZ());
			Location location = new Location(firstCorner.getWorld(), x, 350, z);

			EntityType type = food.get((int) (Math.random() * food.size()));
			LivingEntity entity = (LivingEntity) firstCorner.getWorld().spawnEntity(location, type);
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 100, 0));
			entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 20, 0));


		}
	}

	@Override
	public void end() {

	}
}
