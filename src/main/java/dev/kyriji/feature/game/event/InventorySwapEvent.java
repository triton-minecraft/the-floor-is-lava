package dev.kyriji.feature.game.event;

import dev.kyriji.feature.game.GameManager;
import dev.kyriji.feature.game.model.GameEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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

		if(alivePlayers.size() < 2) {
			return;
		}

		Map<Player, ItemStack[]> playerInventories = new HashMap<>();
		alivePlayers.forEach(player -> playerInventories.put(player, player.getInventory().getContents()));

		List<Player> shuffledPlayers = new ArrayList<>(alivePlayers);

		do {
			Collections.shuffle(shuffledPlayers);
		} while (!isDerangement(alivePlayers, shuffledPlayers));

		for(int i = 0; i < alivePlayers.size(); i++) {
			Player player = alivePlayers.get(i);
			player.getInventory().setContents(playerInventories.get(shuffledPlayers.get(i)));
		}
	}

	private boolean isDerangement(List<Player> original, List<Player> shuffled) {
		for(int i = 0; i < original.size(); i++) {
			if(original.get(i) == shuffled.get(i)) {
				return false;
			}
		}
		return true;
	}



	@Override
	public void end() {

	}
}
