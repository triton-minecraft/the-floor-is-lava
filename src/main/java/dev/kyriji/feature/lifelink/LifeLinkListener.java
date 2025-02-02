package dev.kyriji.feature.lifelink;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LifeLinkListener implements Listener {
	private final Set<UUID> processingDamage = new HashSet<>();
	private final Set<UUID> processingHeal = new HashSet<>();

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player player)) return;

		if(processingDamage.contains(player.getUniqueId())) return;

		LifeLink lifeLink = LifeLinkManager.getLifeLink(player);
		if(lifeLink == null) return;

		Player otherPlayer = lifeLink.getPlayerOne().equals(player) ? lifeLink.getPlayerTwo() : lifeLink.getPlayerOne();

		processingDamage.add(otherPlayer.getUniqueId());
		try {
			otherPlayer.damage(event.getDamage());
		} finally {
			processingDamage.remove(otherPlayer.getUniqueId());
		}
	}

	@EventHandler
	public void onHeal(EntityRegainHealthEvent event) {
		if(!(event.getEntity() instanceof Player player)) return;

		if(processingHeal.contains(player.getUniqueId())) return;

		LifeLink lifeLink = LifeLinkManager.getLifeLink(player);
		if(lifeLink == null) return;

		Player otherPlayer = lifeLink.getPlayerOne().equals(player) ? lifeLink.getPlayerTwo() : lifeLink.getPlayerOne();
		double newHealth = Math.min(otherPlayer.getMaxHealth(), otherPlayer.getHealth() + event.getAmount());

		processingHeal.add(otherPlayer.getUniqueId());
		try {
			otherPlayer.setHealth(newHealth);
		} finally {
			processingHeal.remove(otherPlayer.getUniqueId());
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		LifeLink lifeLink = LifeLinkManager.getLifeLink(event.getEntity());
		if(lifeLink == null) return;

		Player otherPlayer = lifeLink.getPlayerOne().equals(event.getEntity()) ? lifeLink.getPlayerTwo() : lifeLink.getPlayerOne();
		LifeLinkManager.removeLifeLink(lifeLink);
		otherPlayer.setHealth(0);
	}
}