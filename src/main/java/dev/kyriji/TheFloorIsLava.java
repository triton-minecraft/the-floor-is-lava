package dev.kyriji;

import dev.kyriji.command.LifeLinkCommand;
import dev.kyriji.feature.lifelink.LifeLinkListener;

import org.bukkit.plugin.java.JavaPlugin;

public final class TheFloorIsLava extends JavaPlugin {
	public static JavaPlugin INSTANCE;

	@Override
	public void onEnable() {
		INSTANCE = this;

		System.out.println("TheFloorIsLava enabled!");

		getServer().getPluginManager().registerEvents(new LifeLinkListener(), this);

		getCommand("lifelink").setExecutor(new LifeLinkCommand());
	}

	@Override
	public void onDisable() {
		System.out.println("TheFloorIsLava disabled!");
	}
}
