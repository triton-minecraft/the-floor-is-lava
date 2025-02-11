package dev.kyriji.feature.game.event;

import dev.kyriji.feature.game.GameManager;
import dev.kyriji.feature.game.model.GameEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventorySwapEvent implements GameEvent {
	@Override
	public String getIdentifier() {
		return "inventory_swap";
	}

	@Override
	public String getDisplayName() {
		return "&d&lINVENTORY SWAP";
	}

	@Override
	public List<String> getDescription() {
		return List.of("&7Your inventory will be swapped with another", "&7player at random!");
	}

	@Override
	public void start() {
		List<Player> alivePlayers = GameManager.INSTANCE.getGame().getAlivePlayers();

		List<ItemStack[]> inventories = new java.util.ArrayList<>(alivePlayers.stream()
				.map(player -> player.getInventory().getContents())
				.toList());

		alivePlayers.forEach(player -> {
			ItemStack[] inventory = inventories.get((int) (Math.random() * inventories.size()));
			inventories.remove(inventory);

			player.getInventory().setContents(inventory);
		});
	}

	@Override
	public void end() {

	}
}
