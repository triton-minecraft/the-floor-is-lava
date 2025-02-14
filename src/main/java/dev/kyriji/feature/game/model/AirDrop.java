package dev.kyriji.feature.game.model;

import dev.kyriji.TheFloorIsLava;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		AirDropLootTable lootTable = new AirDropLootTable();

		List<Integer> chosenNumbers = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			ItemStack loot = lootTable.getLoot();
			int slot = (int) (Math.random() * 27);
			while(chosenNumbers.contains(slot)) slot = (int) (Math.random() * 27);

			chosenNumbers.add(slot);

			barrel.getInventory().setItem(slot, loot);
		}
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

	public static class AirDropLootTable {
		public Map<Integer, ItemStack> lootTable = new HashMap<>();

		public AirDropLootTable() {
			ItemStack fireResistance = new ItemStack(Material.SPLASH_POTION);
			PotionMeta potionMeta = (PotionMeta) fireResistance.getItemMeta();
			potionMeta.addCustomEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(20, 0), true);
			fireResistance.setItemMeta(potionMeta);

			ItemStack istantHealth = new ItemStack(Material.SPLASH_POTION);
			PotionMeta potionMeta2 = (PotionMeta) istantHealth.getItemMeta();
			potionMeta2.setBasePotionType(PotionType.HEALING);
			istantHealth.setItemMeta(potionMeta2);

			addLoot(10, new ItemStack(Material.DIAMOND, 3));
			addLoot(20, new ItemStack(Material.GOLD_INGOT, 10));
			addLoot(30, new ItemStack(Material.IRON_INGOT, 15));
			addLoot(5, new ItemStack(Material.FIREWORK_ROCKET, 1));
			addLoot(5, new ItemStack(Material.TRIDENT, 1));
			addLoot(5, fireResistance);
			addLoot(15, istantHealth);
			addLoot(10, new ItemStack(Material.GOLDEN_APPLE, 1));
			addLoot(20, new ItemStack(Material.COOKED_BEEF, 5));
			addLoot(20, new ItemStack(Material.COOKED_CHICKEN, 5));

		}

		private void addLoot(int weight, ItemStack item) {
			lootTable.put(weight, item);
		}

		public ItemStack getLoot() {
			int totalWeight = lootTable.keySet().stream().mapToInt(Integer::intValue).sum();
			int randomWeight = (int) (Math.random() * totalWeight);

			int currentWeight = 0;
			for(Map.Entry<Integer, ItemStack> entry : lootTable.entrySet()) {
				currentWeight += entry.getKey();
				if(randomWeight <= currentWeight) return entry.getValue();
			}

			return null;
		}
	}
}
