package dev.kyriji.feature.game.event;

import dev.kyriji.feature.game.GameManager;
import dev.kyriji.feature.game.model.GameEvent;
import dev.kyriji.feature.lifelink.LifeLink;
import dev.kyriji.feature.lifelink.LifeLinkManager;
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
		return List.of("&7Your inventory will be swapped with your partner!");
	}

	@Override
	public void start() {
		LifeLinkManager.lifeLinks.forEach(lifeLink -> {
			ItemStack[] firstPlayerInventory = lifeLink.getPlayerOne().getInventory().getContents();
			ItemStack[] secondPlayerInventory = lifeLink.getPlayerTwo().getInventory().getContents();

			lifeLink.getPlayerOne().getInventory().setContents(secondPlayerInventory);
			lifeLink.getPlayerTwo().getInventory().setContents(firstPlayerInventory);
		});
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
