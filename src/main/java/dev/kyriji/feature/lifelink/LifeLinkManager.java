package dev.kyriji.feature.lifelink;

import dev.kyriji.TheFloorIsLava;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LifeLinkManager {

	public static List<LifeLink> lifeLinks = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(LifeLink lifeLink : lifeLinks) setGlowing(lifeLink, true);
			}
		}.runTaskTimer(TheFloorIsLava.INSTANCE, 0, 20);
	}

	public static void registerLifeLink(LifeLink lifeLink) {
		setGlowing(lifeLink, true);
		lifeLinks.add(lifeLink);
	}

	public static void removeLifeLink(LifeLink lifeLink) {
		setGlowing(lifeLink, false);
		lifeLinks.remove(lifeLink);
	}

	public static LifeLink getLifeLink(Player player) {
		return lifeLinks.stream()
				.filter(link -> link.getPlayerOne().equals(player) || link.getPlayerTwo().equals(player))
				.findFirst()
				.orElse(null);
	}

	public static void setGlowing(LifeLink lifeLink, boolean glowing) {
		ServerPlayer entityPlayerOne = ((CraftPlayer) lifeLink.getPlayerOne()).getHandle();
		ServerPlayer entityPlayerTwo = ((CraftPlayer) lifeLink.getPlayerTwo()).getHandle();

		ServerGamePacketListenerImpl playerConnectionOne = entityPlayerOne.connection;
		ServerGamePacketListenerImpl playerConnectionTwo = entityPlayerTwo.connection;

		List<SynchedEntityData.DataValue<?>> dataWatchers = new ArrayList<>();
		dataWatchers.add(new SynchedEntityData.DataValue<>(0, EntityDataSerializers.BYTE, (byte) (glowing ? 0x40 : ~0x40)));


		ClientboundSetEntityDataPacket packetPlayOutEntityMetadata = new ClientboundSetEntityDataPacket(entityPlayerOne.getId(), dataWatchers);
		ClientboundSetEntityDataPacket packetPlayOutEntityMetadata2 = new ClientboundSetEntityDataPacket(entityPlayerTwo.getId(), dataWatchers);

		playerConnectionTwo.sendPacket(packetPlayOutEntityMetadata);
		playerConnectionOne.sendPacket(packetPlayOutEntityMetadata2);
	}


}
