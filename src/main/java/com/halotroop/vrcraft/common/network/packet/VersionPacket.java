package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.ServerConfig;
import com.halotroop.vrcraft.server.VrCraftServer;
import io.github.cottonmc.cotton.logging.ModLogger;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

/*
 * Why the fuck does the client want a length-prefixed string, but sends
 * a string that's just char bytes with no length prefix? This whole
 * protocol is an awful mess. I didn't write it, so don't blame me.
 */
public class VersionPacket implements VivecraftPacket {
	private static final ModLogger LOGGER = VrCraft.LOGGER;
	private static final ServerConfig CONFIG = VrCraftServer.CONFIG;
	
	public String message;
	
	public VersionPacket() {
	}
	
	public VersionPacket(String message) {
		this.message = message;
	}
	
	@Override
	public void encode(final PacketByteBuf buffer) {
		buffer.writeString(message);
	}
	
	@Override
	public void decode(final PacketByteBuf buffer) {
		byte[] bytes = new byte[buffer.readableBytes()];
		buffer.readBytes(bytes);
		message = new String(bytes, StandardCharsets.UTF_8);
	}
	
	@Override
	public void handleClient(final PacketContext context) {
	}
	
	@Override
	public void handleServer(final PacketContext context) {
		ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
		ChannelHandler.sendTo(new VersionPacket(VrCraft.MOD_VERSION), player);
		if (!message.contains("NONVR")) {
			LOGGER.info("VR player joined: " + message);
			ChannelHandler.sendTo(new RequestDataPacket(), player);
			
			if (CONFIG.enableTeleport)
				ChannelHandler.sendTo(new TeleportPacket(), player);
			if (CONFIG.enableClimbing)
				ChannelHandler.sendTo(new ClimbingPacket(CONFIG.blockMode, CONFIG.blockList), player);
			if (CONFIG.enableCrawling)
				ChannelHandler.sendTo(new CrawlPacket(), player);
			
			if (CONFIG.limitedSurvival) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("limitedTeleport", true);
				map.put("teleportLimitUp", CONFIG.upLimit);
				map.put("teleportLimitDown", CONFIG.downLimit);
				map.put("teleportLimitHoriz", CONFIG.horizontalLimit);
				ChannelHandler.sendTo(new SettingOverridePacket(map), player);
			}
			
			if (CONFIG.limitRange) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("worldScale.min", CONFIG.minRange);
				map.put("worldScale.max", CONFIG.maxRange);
				ChannelHandler.sendTo(new SettingOverridePacket(map), player);
			}
			
			context.getTaskQueue().execute(() -> {
				PlayerTracker.players.put(player.getGameProfile().getId(), new VRPlayerData());
				if (CONFIG.welcomeMsgEnabled && !CONFIG.welcomeVR.isEmpty())
					Objects.requireNonNull(player.getServer()).getPlayerManager().broadcastChatMessage(
							new LiteralText(String.format(CONFIG.welcomeVR, player.getDisplayName())),
							MessageType.SYSTEM, Util.NIL_UUID);
			});
		} else {
			LOGGER.info("Non-VR player joined: " + message);
			context.getTaskQueue().execute(() -> {
				PlayerTracker.nonVRPlayers.add(player.getGameProfile().getId());
				if (CONFIG.welcomeMsgEnabled && !CONFIG.welcomeNonVR.isEmpty())
					Objects.requireNonNull(player.getServer()).getPlayerManager().broadcastChatMessage(
							new LiteralText(String.format(CONFIG.welcomeNonVR, player.getDisplayName())),
							MessageType.SYSTEM, Util.NIL_UUID);
			});
		}
	}
	
	private static class ChannelHandler {
		private static void sendTo(VivecraftPacket packet, ServerPlayerEntity player) {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			packet.encode(buf);
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, VrCraft.VIVECRAFT_CHANNEL_ID, buf);
		}
	}
}
