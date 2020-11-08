package com.halotroop.vrcraft.server.network.packet;

import com.halotroop.vrcraft.client.network.packet.SettingOverrideS2CPacket;
import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.network.packet.ClimbingPacket;
import com.halotroop.vrcraft.common.network.packet.DeviceData;
import com.halotroop.vrcraft.common.network.packet.VRPacketHandler;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.ServerConfig;
import com.halotroop.vrcraft.server.VrCraftServer;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

@Environment(EnvType.SERVER)
public class VRC2SPacketHandler extends VRPacketHandler {
	public final ServerPlayerEntity player;
	
	public VRC2SPacketHandler(ServerPlayerEntity player, PacketByteBuf buffer) {
		super(PlayerTracker.getAbsolutePlayerData(player), buffer);
		this.player = player;
	}
	
	public void activeHand() {
		data.activeHand = buffer.readInt();
	}
	
	@Override
	public void bowDraw() {
		data.bowDraw = buffer.readFloat();
	}
	
	@Override
	public void climbing(BlockListMode blockMode, List<String> list) {
		player.fallDistance = 0;
		player.networkHandler.floatingTicks = 0;
	}
	
	@Override
	public void controller(Controller controller) {
		DeviceData cData = DeviceData.decode(buffer);
		switch (controller) {
			case RIGHT:
				this.data.controllerR = cData;
				break;
			case LEFT:
				this.data.controllerL = cData;
				break;
		}
	}
	
	@Override
	public void crawling() {
		this.data.crawling = buffer.readBoolean();
	}
	
	@Override
	public void hmd() {
		this.data.head = DeviceData.decode(this.buffer);
	}
	
	@Override
	public void height() {
		this.data.height = buffer.readFloat();
	}
	
	@Override
	public void teleport() {
		this.player.setPos(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
	}
	
	@Override
	public void version(String message) {
		final ServerConfig CONFIG = VrCraft.SERVER_CONFIG;
		
		if (message.toLowerCase().contains("vivecraft") && !CONFIG.allowVivecraft)
			this.player.networkHandler.getConnection().disconnect(new LiteralText(CONFIG.vivecraftDisconnectMessage));
		if (message.toLowerCase().contains("vrcraft") && !CONFIG.allowVRCraft)
			this.player.networkHandler.getConnection().disconnect(new LiteralText(CONFIG.vrCraftDisconnectMessage));
		
		sendToClient(new PacketByteBuf(Unpooled.buffer().writeByte(VrCraftServer.Packet.VERSION.ordinal())));
		
		if (message.toLowerCase().contains("nonvr")) {
			LOGGER.info("Non-VR player joined: " + message);
			PlayerTracker.nonVRPlayers.add(this.player.getUuid());
			if (CONFIG.welcomeMsgEnabled && this.player.getServer() != null)
				this.player.getServer().getPlayerManager().broadcastChatMessage(
						new LiteralText(String.format(CONFIG.welcomeNonVR, player.getName().asString())),
						MessageType.SYSTEM, Util.NIL_UUID);
		} else {
			LOGGER.info("VR player joined: " + message);
			sendToClient(new PacketByteBuf(Unpooled.buffer().writeByte(VrCraftServer.Packet.DATA_REQUEST.ordinal())));
			
			if (CONFIG.enableTeleport)
				sendToClient(new PacketByteBuf(Unpooled.buffer().writeByte(VrCraftServer.Packet.TELEPORT.ordinal())));
			if (CONFIG.enableClimbing)
				sendToClient(new ClimbingPacket(CONFIG.blockMode, CONFIG.blockList).encode(new PacketByteBuf(Unpooled.buffer())));
			if (CONFIG.enableCrawling)
				sendToClient(new PacketByteBuf(Unpooled.buffer().writeByte(VrCraftServer.Packet.TELEPORT.ordinal())));
			
			if (CONFIG.limitedSurvival) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("teleportLimitUp", CONFIG.upLimit);
				map.put("teleportLimitDown", CONFIG.downLimit);
				map.put("teleportLimitHoriz", CONFIG.horizontalLimit);
				sendToClient(new SettingOverrideS2CPacket(map).encode(new PacketByteBuf(Unpooled.buffer())));
			}
			
			if (CONFIG.limitRange) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("worldScale.min", CONFIG.minRange);
				map.put("worldScale.max", CONFIG.maxRange);
				sendToClient(new SettingOverrideS2CPacket(map).encode(new PacketByteBuf(Unpooled.buffer())));
			}
			
			PlayerTracker.players.put(Objects.requireNonNull(player).getGameProfile().getId(), new VRPlayerData(player));
			if (CONFIG.welcomeMsgEnabled && !CONFIG.welcomeVR.isEmpty() && player.getServer() != null) {
				player.getServer().getPlayerManager().broadcastChatMessage(
						new LiteralText(String.format(CONFIG.welcomeVR, player.getName().asString())),
						MessageType.SYSTEM, Util.NIL_UUID);
			}
		}
	}
	
	private void sendToClient(PacketByteBuf buffer) {
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(this.player, VrCraft.VIVECRAFT_CHANNEL_ID, buffer);
	}
}
