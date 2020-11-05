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
	public static Map<UUID, VRPlayerData> players = new ConcurrentHashMap<>();
	public static Set<UUID> nonVRPlayers = ConcurrentHashMap.newKeySet();
	
	private PlayerTracker() {}
	
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
		absData.controllerL.setPos(Util.addAll(data.controllerL.getPos(), (entity.getRotationVector()), data.offset));
		absData.controllerL.setRot(data.controllerL.getRotation());
		absData.controllerR.setPos(data.controllerR.getPos().add(entity.getRotationVector()).add(data.offset));
		absData.controllerR.setRot(data.controllerR.getRotation());
		
		return absData;
	}
	
	public static boolean hasPlayerData(PlayerEntity entity) {
		return players.containsKey(entity.getGameProfile().getId());
	}
	
	public static PacketByteBuf getUberPacketBytes(UUID uuid, VRPlayerData data) {
		return new UberPacket(uuid, data.head, data.controllerL, data.controllerR, data.worldScale, data.height)
				.encode(new PacketByteBuf(Unpooled.buffer()));
	}
}
