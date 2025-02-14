package dev.kyriji.feature.game;

import dev.kyriji.feature.game.event.*;
import dev.kyriji.feature.game.model.GameEvent;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
	public static EventManager INSTANCE;

	private final List<GameEvent> events;
	private GameEvent lastRandomEvent;

	private int eventIndex = 0;

	public EventManager() {
		INSTANCE = this;
		events = new ArrayList<>();
		lastRandomEvent = null;

		registerEvent(new PvpEvent());
		registerEvent(new AirdropEvent());
		registerEvent(new InventorySwapEvent());
		registerEvent(new SnowFightEvent());
		registerEvent(new TntRainEvent());
		registerEvent(new RationsEvent());
	}

	public void registerEvent(GameEvent event) {
		events.add(event);
	}

	public GameEvent getEvent(String identifier) {
		return events.stream()
				.filter(event -> event.getIdentifier().equals(identifier))
				.findFirst()
				.orElse(null);
	}

	public GameEvent getRandomEvent() {
		eventIndex++;
		if(events.isEmpty()) return null;

		if(eventIndex == 0) return getEvent("rations");

		if(events.size() == 1) return events.getFirst();

		GameEvent event = events.get((int) (Math.random() * events.size()));
		while(event.equals(lastRandomEvent)) event = events.get((int) (Math.random() * events.size()));
		lastRandomEvent = event;

		return event;
	}
}
