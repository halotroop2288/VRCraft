package com.halotroop.vrcraft.common.util;

import com.halotroop.vrcraft.common.network.packet.UberPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility class that tracks the VR Data of each player
 */
public final class PlayerTracker {
	public static final Map<UUID, VRPlayerData> players = new ConcurrentHashMap<>();
	public static final Set<UUID> nonVRPlayers = ConcurrentHashMap.newKeySet();
	
	private PlayerTracker() {
	}
	
	public static void tick(PlayerManager playerManager) {
		for (Iterator<Map.Entry<UUID, VRPlayerData>> it = players.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<UUID, VRPlayerData> entry = it.next();
			Entity entity = playerManager.getPlayer(entry.getKey());
			if (entity == null) {
				it.remove();
			}
		}
		for (Iterator<UUID> it = nonVRPlayers.iterator(); it.hasNext(); ) {
			UUID uuid = it.next();
			Entity entity = playerManager.getPlayer(uuid);
			if (entity == null) {
				it.remove();
			}
		}
	}
	
	public static VRPlayerData getPlayerData(PlayerEntity entity, boolean createIfMissing) {
		VRPlayerData data = players.get(entity.getGameProfile().getId());
		if (data == null && createIfMissing) {
			data = new VRPlayerData(entity);
			players.put(entity.getGameProfile().getId(), data);
		}
		return data;
	}
	
	@Nullable
	public static VRPlayerData getPlayerData(PlayerEntity entity) {
		return getPlayerData(entity, false);
	}
	
	@Nullable
	public static VRPlayerData getAbsolutePlayerData(PlayerEntity entity) {
		VRPlayerData data = getPlayerData(entity);
		if (data == null)
			return null;
		
		VRPlayerData absData = new VRPlayerData(entity);
		
		absData.handsReversed = data.handsReversed;
		absData.worldScale = data.worldScale;
		absData.seated = data.seated;
		absData.freeMove = data.freeMove;
		absData.bowDraw = data.bowDraw;
		absData.height = data.height;
		absData.activeHand = data.activeHand;
		absData.crawling = data.crawling;
		
		absData.head.setPos(data.head.getPos().add(entity.getRotationVector()).add(data.offset));
		absData.head.setRot(data.head.getRotation());
		absData.controller0.setPos(Util.addAll(data.controller0.getPos(), (entity.getRotationVector()), data.offset));
		absData.controller0.setRot(data.controller0.getRotation());
		absData.controller1.setPos(data.controller1.getPos().add(entity.getRotationVector()).add(data.offset));
		absData.controller1.setRot(data.controller1.getRotation());
		
		return absData;
	}
	
	public static boolean hasPlayerData(PlayerEntity entity) {
		return players.containsKey(entity.getGameProfile().getId());
	}
	
	public static PacketByteBuf getUberPacketBytes(UUID uuid, VRPlayerData data) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		new UberPacket(uuid, data.head, data.controller0, data.controller1, data.worldScale, data.height).encode(buffer);
		return buffer;
	}
}
