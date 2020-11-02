package com.halotroop.vrcraft.common.util;

import com.halotroop.vrcraft.common.network.packet.UberPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility class that tracks the VR Data of each player
 */
public class PlayerTracker {
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
			data = new VRPlayerData();
			players.put(entity.getGameProfile().getId(), data);
		}
		return data;
	}
	
	public static VRPlayerData getPlayerData(PlayerEntity entity) {
		return getPlayerData(entity, false);
	}
	
	public static VRPlayerData getAbsolutePlayerData(PlayerEntity entity) {
		VRPlayerData data = getPlayerData(entity);
		if (data == null)
			return null;
		
		VRPlayerData absData = new VRPlayerData();
		
		absData.handsReversed = data.handsReversed;
		absData.worldScale = data.worldScale;
		absData.seated = data.seated;
		absData.freeMove = data.freeMove;
		absData.bowDraw = data.bowDraw;
		absData.height = data.height;
		absData.activeHand = data.activeHand;
		absData.crawling = data.crawling;
		
		absData.head.setPos(data.head.getPos().add(entity.getRotationVector()).add(data.offset));
		absData.head.setRot(data.head.getRot());
		absData.controller0.setPos(Util.add(Util.add(data.controller0.getPos(), (entity.getRotationVector())), data.offset));
		absData.controller0.setRot(data.controller0.getRot());
		absData.controller1.setPos(data.controller1.getPos().add(entity.getRotationVector()).add(data.offset));
		absData.controller1.setRot(data.controller1.getRot());
		
		return absData;
	}
	
	public static boolean hasPlayerData(PlayerEntity entity) {
		return players.containsKey(entity.getGameProfile().getId());
	}
	
	public static UberPacket getPlayerDataPacket(UUID uuid, VRPlayerData data) {
		UberPacket.HeadData headData = new UberPacket.HeadData(data.seated, (float)data.head.posX, (float)data.head.posY, (float)data.head.posZ, data.head.rotW, data.head.rotX, data.head.rotY, data.head.rotZ);
		UberPacket.ControllerData0 controller0Data = new UberPacket.ControllerData0(data.handsReversed, (float)data.controller0.posX, (float)data.controller0.posY, (float)data.controller0.posZ, data.controller0.rotW, data.controller0.rotX, data.controller0.rotY, data.controller0.rotZ);
		UberPacket.ControllerData1 controller1Data = new UberPacket.ControllerData1(data.handsReversed, (float)data.controller1.posX, (float)data.controller1.posY, (float)data.controller1.posZ, data.controller1.rotW, data.controller1.rotX, data.controller1.rotY, data.controller1.rotZ);
		return new UberPacket(uuid, headData, controller0Data, controller1Data, data.worldScale, data.height);
	}
}
