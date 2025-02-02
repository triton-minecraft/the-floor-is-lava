package dev.kyriji.feature.lifelink;

import org.bukkit.entity.Player;

public class LifeLink {

	private final Player playerOne;
	private final Player playerTwo;

	public LifeLink(Player playerOne, Player playerTwo) {
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;

		playerOne.setHealth(playerTwo.getHealth());
	}

	public Player getPlayerOne() {
		return playerOne;
	}

	public Player getPlayerTwo() {
		return playerTwo;
	}
}
