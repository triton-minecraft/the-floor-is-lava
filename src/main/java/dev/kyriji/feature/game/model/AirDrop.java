package dev.kyriji.feature.game.model;

import dev.kyriji.TheFloorIsLava;
import dev.kyriji.feature.world.WorldManager;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class AirDrop implements Listener {
	public Location spawnLocation;
	public Location blockLocation;

	public BukkitTask runnable;

	public Entity entity = null;
	public Location lastKnownLocation = null;

	public AirDrop(Location location) {
		this.spawnLocation = location;
		this.blockLocation = null;

		TheFloorIsLava.INSTANCE.getServer().getPluginManager().registerEvents(this, TheFloorIsLava.INSTANCE);

		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(entity == null) return;

				if(entity.isDead()) {
					onBlockLand();
					cancel();
				}
				else lastKnownLocation = entity.getLocation();
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 0, 1);
	}

	public void spawn() {
		MaterialData materialData = new MaterialData(Material.BARREL);
		FallingBlock block = spawnLocation.getWorld().spawnFallingBlock(spawnLocation, materialData);

		block.setHurtEntities(false);
		block.setGravity(false);
		block.setGlowing(true);

		this.entity = block;
	}

	public void onBlockLand() {
		System.out.println("Block landed");
		if(lastKnownLocation == null) return;

		System.out.println(lastKnownLocation);

		Block block = lastKnownLocation.getBlock();

		if(block.getType() != Material.BARREL) return;
		System.out.println("Barrel!");

		Barrel barrel = (Barrel) block.getState();

		System.out.println(block.getLocation());
		System.out.println(spawnLocation);

		Location blockLocation = block.getLocation();
		if(blockLocation.getBlockX() == spawnLocation.getBlockX() && blockLocation.getBlockZ() == spawnLocation.getBlockZ()) {
			this.blockLocation = block.getLocation();
		}

		barrel.setCustomName("Air Drop");
		barrel.getInventory().addItem(new ItemStack(Material.DIAMOND, 5));
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Location inventoryLocation = event.getInventory().getLocation();
		if(inventoryLocation == null) return;

		Block block = inventoryLocation.getBlock();
		if(block.getType() != Material.TRAPPED_CHEST) return;

		remove();
	}

	public void remove() {
		if(blockLocation != null) {
			blockLocation.getBlock().setType(Material.AIR);
			blockLocation.getWorld().playEffect(blockLocation, Effect.ZOMBIE_DESTROY_DOOR, 0);
		}

		runnable.cancel();

		InventoryCloseEvent.getHandlerList().unregister(this);
		EntityChangeBlockEvent.getHandlerList().unregister(this);
	}
}
