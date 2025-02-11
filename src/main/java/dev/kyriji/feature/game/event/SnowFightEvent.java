package dev.kyriji.feature.game.event;

import dev.kyriji.feature.game.GameManager;
import dev.kyriji.feature.game.model.GameEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SnowFightEvent implements GameEvent {
	@Override
	public String getIdentifier() {
		return "snow_fight";
	}

	@Override
	public String getDisplayName() {
		return "&b&lSNOW FIGHT";
	}

	@Override
	public List<String> getDescription() {
		return List.of("&7You will be given some snowballs", "&7to throw at other players!");
	}

	@Override
	public void start() {
		List<Player> alivePlayers = GameManager.INSTANCE.getGame().getAlivePlayers();

		alivePlayers.forEach(player -> {
			player.getInventory().addItem(new ItemStack(Material.SNOWBALL, 16));
		});
	}

	@Override
	public void end() {

	}
}
