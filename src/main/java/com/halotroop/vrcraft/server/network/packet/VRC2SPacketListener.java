package com.halotroop.vrcraft.server.network.packet;

import com.halotroop.vrcraft.client.network.packet.DataRequestS2CPacket;
import com.halotroop.vrcraft.client.network.packet.SettingOverridePacket;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Objects;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

@Environment(EnvType.SERVER)
public class VRC2SPacketListener implements PacketListener {
	private static final ServerSidePacketRegistry CHANNEL = ServerSidePacketRegistry.INSTANCE;
	private static final ServerConfig CONFIG = VrCraftServer.config;
	private static final String NO_VR_PD = "Found null player data in a place where it should not be!";
	
	private final @Nullable VRPlayerData playerData;
	private final @NonNull ServerPlayerEntity player;
	private final @NonNull MinecraftServer server;
	
	public VRC2SPacketListener(@NonNull ServerPlayerEntity player) {
		this.player = player;
		this.playerData = PlayerTracker.getPlayerData(player);
		this.server = Objects.requireNonNull(player.getServer());
	}
	
	public void applyControllerSync(ControllerData controllerData, ControllerData.Controller controller) {
		LOGGER.devInfo("Received" + controller + " Controller Packet");
		if (this.playerData == null) throw new IllegalStateException(NO_VR_PD);
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
		if (this.playerData == null) throw new IllegalStateException(NO_VR_PD);
		this.playerData.head = headData;
	}
	
	public void applyUberSync(UberPacket packet) {
		if (this.playerData == null) throw new IllegalStateException(NO_VR_PD);
		this.playerData.head = packet.headData;
		this.playerData.controllerR = packet.controllerRData;
		this.playerData.controllerL = packet.controllerLData;
		this.playerData.height = packet.height;
		this.playerData.worldScale = packet.worldScale;
	}
	
	public void applyVersionSync(String message) {
		if (message.toLowerCase().contains("vivecraft") && !CONFIG.allowVivecraft)
			player.networkHandler.getConnection().disconnect(new LiteralText(CONFIG.vivecraftDisconnectMessage));
		if (message.toLowerCase().contains("vrcraft") && !CONFIG.allowVRCraft)
			player.networkHandler.getConnection().disconnect(new LiteralText(CONFIG.vrCraftDisconnectMessage));
		
		new VersionPacket().sendToClient(player);
		
		if (message.toLowerCase().contains("nonvr")) {
			this.onNonVRPlayerConnected(message);
		} else {
			this.onVRPlayerConnected(message);
		}
	}
	
	private void onVRPlayerConnected(String message) {
		LOGGER.info("VR player joined " + message);
		PlayerTracker.nonVRPlayers.add(this.player.getUuid());
		if (CONFIG.welcomeMsgEnabled && this.player.getServer() != null)
			this.player.getServer().getPlayerManager().broadcastChatMessage(
					new LiteralText(String.format(CONFIG.welcomeNonVR, player.getDisplayName().asString())),
					MessageType.SYSTEM, Util.NIL_UUID);
	}
	
	private void onNonVRPlayerConnected(String message) {
		LOGGER.info("Non-VR player joined " + message);
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(this.player, new DataRequestS2CPacket());
		
		if (CONFIG.enableTeleport) new TeleportPacket().sendToClient(this.player);
		if (CONFIG.enableClimbing) new ClimbingPacket(CONFIG.blockMode, CONFIG.blockList).sendToClient(player);
		if (CONFIG.enableCrawling) new CrawlingPacket().sendToClient(this.player);
		
		if (CONFIG.limitedSurvival) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("teleportLimitUp", CONFIG.upLimit);
			map.put("teleportLimitDown", CONFIG.downLimit);
			map.put("teleportLimitHoriz", CONFIG.horizontalLimit);
			new SettingOverridePacket(map);
		}
		
		if (CONFIG.limitRange) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("worldScale.min", CONFIG.minRange);
			map.put("worldScale.max", CONFIG.maxRange);
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new SettingOverridePacket(map));
		}
		
		PlayerTracker.players.put(Objects.requireNonNull(player).getGameProfile().getId(), new VRPlayerData(player));
		if (CONFIG.welcomeMsgEnabled && !CONFIG.welcomeVR.isEmpty()) {
			server.getPlayerManager().broadcastChatMessage(
					new LiteralText(String.format(CONFIG.welcomeVR, player.getDisplayName().asString())),
					MessageType.SYSTEM, Util.NIL_UUID);
		}
	}
	
	public void applyCrawling(boolean crawling) {
		if (this.playerData == null) throw new IllegalStateException(NO_VR_PD);
		if (VrCraftServer.config.enableCrawling) {
			if (PlayerTracker.hasPlayerData(this.player)) {
				playerData.crawling = crawling;
				if (playerData.crawling) this.player.setPose(EntityPose.SWIMMING);
			}
		}
	}
	
	public void applyBowDraw(float drawDist) {
		if (this.playerData == null) throw new IllegalStateException(NO_VR_PD);
		playerData.bowDraw = drawDist;
	}
	
	public void applyClimbing() {
		player.fallDistance = 0;
		player.networkHandler.floatingTicks = 0;
	}
	
	public void applyHeight(float height) {
		if (this.playerData == null) throw new IllegalStateException(NO_VR_PD);
		playerData.height = height;
	}
	
	public void applyTeleport(float x, float y, float z) {
		player.setPos(x, y, z);
	}
	
	public void applyActiveHand(int activeHand) {
		if (this.playerData == null) throw new IllegalStateException(NO_VR_PD);
		playerData.activeHand = activeHand;
	}
	
	public void applyWorldScale(float worldScale) {
		if (this.playerData == null) throw new IllegalStateException(NO_VR_PD);
		this.playerData.worldScale = worldScale;
	}
	
	@Override
	public void onDisconnected(Text reason) {
//		new LiteralText(CONFIG.vrOnlyKickMessage);
	}
	
	@Override
	public ClientConnection getConnection() {
		return player.networkHandler.getConnection();
	}
}
