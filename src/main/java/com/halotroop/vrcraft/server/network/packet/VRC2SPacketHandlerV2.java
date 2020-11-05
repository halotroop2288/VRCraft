package com.halotroop.vrcraft.server.network.packet;

import com.halotroop.vrcraft.client.network.packet.SettingOverridePacket;
import com.halotroop.vrcraft.common.network.packet.ClimbingPacket;
import com.halotroop.vrcraft.common.network.packet.DeviceData;
import com.halotroop.vrcraft.common.network.packet.VRPacketHandlerV2;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.ServerConfig;
import com.halotroop.vrcraft.server.ServerEventRegistrarV2;
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
public class VRC2SPacketHandlerV2 extends VRPacketHandlerV2 {
	public final ServerPlayerEntity player;
	
	public VRC2SPacketHandlerV2(ServerPlayerEntity player, PacketByteBuf buffer) {
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
			case LEFT:
				this.data.controllerL = cData;
				break;
			case RIGHT:
				this.data.controllerR = cData;
				break;
		}
	}
	
	@Override
	public void crawling() {
		data.crawling = buffer.readBoolean();
	}
	
	@Override
	public void hmd(DeviceData hData) {
		data.head = hData;
	}
	
	@Override
	public void height() {
		data.height = buffer.readFloat();
	}
	
	@Override
	public void teleport() {
		player.setPos(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
	}
	
	@Override
	public void version(String message) {
		final ServerConfig CONFIG = VrCraftServer.config;
		
		if (message.toLowerCase().contains("vivecraft") && !CONFIG.allowVivecraft)
			player.networkHandler.getConnection().disconnect(new LiteralText(CONFIG.vivecraftDisconnectMessage));
		if (message.toLowerCase().contains("vrcraft") && !CONFIG.allowVRCraft)
			player.networkHandler.getConnection().disconnect(new LiteralText(CONFIG.vrCraftDisconnectMessage));
		
		sendToClient(new PacketByteBuf(Unpooled.buffer().writeByte(ServerEventRegistrarV2.Packet.VERSION.ordinal())));
		
		if (message.toLowerCase().contains("nonvr")) {
			LOGGER.info("VR player joined");
			PlayerTracker.nonVRPlayers.add(this.player.getUuid());
			if (CONFIG.welcomeMsgEnabled && this.player.getServer() != null)
				this.player.getServer().getPlayerManager().broadcastChatMessage(
						new LiteralText(String.format(CONFIG.welcomeNonVR, player.getDisplayName().asString())),
						MessageType.SYSTEM, Util.NIL_UUID);
		} else {
			LOGGER.info("Non-VR player joined");
			sendToClient(new PacketByteBuf(Unpooled.buffer().writeByte(ServerEventRegistrarV2.Packet.DATA_REQUEST.ordinal())));
			
			if (CONFIG.enableTeleport)
				sendToClient(new PacketByteBuf(Unpooled.buffer().writeByte(ServerEventRegistrarV2.Packet.TELEPORT.ordinal())));
			if (CONFIG.enableClimbing)
				sendToClient(new ClimbingPacket(CONFIG.blockMode, CONFIG.blockList).encode(new PacketByteBuf(Unpooled.buffer())));
			if (CONFIG.enableCrawling)
				sendToClient(new PacketByteBuf(Unpooled.buffer().writeByte(ServerEventRegistrarV2.Packet.TELEPORT.ordinal())));
			
			if (CONFIG.limitedSurvival) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("teleportLimitUp", CONFIG.upLimit);
				map.put("teleportLimitDown", CONFIG.downLimit);
				map.put("teleportLimitHoriz", CONFIG.horizontalLimit);
				sendToClient(new SettingOverridePacket(map).encode(new PacketByteBuf(Unpooled.buffer())));
			}
			
			if (CONFIG.limitRange) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("worldScale.min", CONFIG.minRange);
				map.put("worldScale.max", CONFIG.maxRange);
				sendToClient(new SettingOverridePacket(map).encode(new PacketByteBuf(Unpooled.buffer())));
			}
			
			PlayerTracker.players.put(Objects.requireNonNull(player).getGameProfile().getId(), new VRPlayerData(player));
			if (CONFIG.welcomeMsgEnabled && !CONFIG.welcomeVR.isEmpty() && player.getServer() != null) {
				player.getServer().getPlayerManager().broadcastChatMessage(
						new LiteralText(String.format(CONFIG.welcomeVR, player.getDisplayName().asString())),
						MessageType.SYSTEM, Util.NIL_UUID);
			}
		}
	}
	
	@Override
	public void worldScale(float worldScale) {
		data.worldScale = worldScale;
	}
	
	private void sendToClient(PacketByteBuf buffer) {
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(this.player, VIVECRAFT_CHANNEL_ID, buffer);
	}
}
