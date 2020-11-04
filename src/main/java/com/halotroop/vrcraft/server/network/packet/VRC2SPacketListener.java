package com.halotroop.vrcraft.server.network.packet;

import com.halotroop.vrcraft.client.network.packet.DataRequestS2CPacket;
import com.halotroop.vrcraft.client.network.packet.SettingOverridePacket;
import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.network.packet.*;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.UberPacket;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.ServerConfig;
import com.halotroop.vrcraft.server.VrCraftServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Objects;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

@Environment(EnvType.SERVER)
public class VRC2SPacketListener implements PacketListener {
	private static final ServerSidePacketRegistry CHANNEL = ServerSidePacketRegistry.INSTANCE;
	private static final ServerConfig CONFIG = VrCraftServer.config;
	public final VRPlayerData playerData;
	ServerPlayerEntity player;
	
	public VRC2SPacketListener(VRPlayerData playerData) {
		this.playerData = playerData;
		this.player = (ServerPlayerEntity) playerData.player;
	}
	
	public void applyControllerSync(ControllerData controllerData, ControllerData.Controller controller) {
		LOGGER.devInfo("Received" + controller +" Controller Packet");
		switch (controller) {
			default:
			case LEFT:
				this.playerData.controllerR = controllerData;
				break;
			case RIGHT:
				this.playerData.controllerL = controllerData;
				break;
		}
	}
	
	public void applyHMDSync(HeadData headData) {
		LOGGER.devInfo("Received HMD Packet");
		this.playerData.head = headData;
	}
	
	public void applyUberSync(UberPacket packet) {
		this.playerData.head = packet.headData;
		this.playerData.controllerR = packet.controllerRData;
		this.playerData.controllerL = packet.controllerLData;
		this.playerData.height = packet.height;
		this.playerData.worldScale = packet.worldScale;} // No-op
	
	public void applyVersionSync(String message) {
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(this.player, new VersionPacket(VrCraft.MOD_VERSION));
		
		if (message.contains("NONVR")) {
			this.onNonVRPlayerConnected(message);
		} else {
			this.onVRPlayerConnected(message);
		}
	}
	
	private void onVRPlayerConnected(String message) {
		LOGGER.info("VR player joined " + message);
		PlayerTracker.nonVRPlayers.add(this.player.getUuid());
		if (CONFIG.welcomeMsgEnabled)
			Objects.requireNonNull(this.player.getServer()).getPlayerManager().broadcastChatMessage(
					new LiteralText(String.format(CONFIG.welcomeNonVR, playerData.player.getDisplayName())),
					MessageType.SYSTEM, Util.NIL_UUID);
	}
	
	private void onNonVRPlayerConnected(String message) {
		LOGGER.info("Non-VR player joined " +  message);
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(this.player, new DataRequestS2CPacket());
		
		if (CONFIG.enableTeleport) CHANNEL.sendToPlayer(this.player, new TeleportPacket());
		if (CONFIG.enableClimbing) CHANNEL.sendToPlayer(this.player,
					new ClimbingPacket(CONFIG.blockMode, CONFIG.blockList));
		if (CONFIG.enableCrawling) CHANNEL.sendToPlayer(this.player, new CrawlingPacket());
		
		if (CONFIG.limitedSurvival) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("teleportLimitUp", CONFIG.upLimit);
			map.put("teleportLimitDown", CONFIG.downLimit);
			map.put("teleportLimitHoriz", CONFIG.horizontalLimit);
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new SettingOverridePacket(map));
		}
		
		if (CONFIG.limitRange) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("worldScale.min", CONFIG.minRange);
			map.put("worldScale.max", CONFIG.maxRange);
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new SettingOverridePacket(map));
		}
		
		PlayerTracker.players.put(Objects.requireNonNull(player).getGameProfile().getId(), new VRPlayerData(player));
		if (CONFIG.welcomeMsgEnabled && !CONFIG.welcomeVR.isEmpty()) {
			Objects.requireNonNull(player.getServer()).getPlayerManager().broadcastChatMessage(
					new LiteralText(String.format(CONFIG.welcomeVR, player.getDisplayName())),
					MessageType.SYSTEM, Util.NIL_UUID);
		}
	}
	
	public void applyCrawling(boolean crawling) {
		if (VrCraftServer.config.enableCrawling) {
			if (PlayerTracker.hasPlayerData(this.player)) {
				VRPlayerData data = PlayerTracker.getPlayerData(this.player, true);
				data.crawling = crawling;
				if (data.crawling) this.player.setPose(EntityPose.SWIMMING);
			}
		}
	}
	
	public void applyBowDraw(float drawDist) {
		playerData.bowDraw = drawDist;
	}
	
	public void applyClimbing() {
		player.fallDistance = 0;
		player.networkHandler.floatingTicks = 0;
	}
	
	public void applyHeight(float height) {
		playerData.height = height;
	}
	
	public void applyTeleport(float x, float y, float z) {
		player.setPos(x, y, z);
	}
	
	public void applyActiveHand(int activeHand) {
		playerData.activeHand = activeHand;
	}
	
	public void applyWorldScale(float worldScale) {
		this.playerData.worldScale = worldScale;
	}
	
	@Override
	public void onDisconnected(Text reason) {
//		new LiteralText(CONFIG.vrOnlyKickMessage);
	}
	
	@Override
	public ClientConnection getConnection() {
		return ((ServerPlayerEntity) playerData.player).networkHandler.getConnection();
	}
}
