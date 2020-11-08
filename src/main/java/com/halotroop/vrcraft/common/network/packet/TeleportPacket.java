package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.server.VrCraftServer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class TeleportPacket implements VivecraftPacket {
	public float posX;
	public float posY;
	public float posZ;
	
	public TeleportPacket() {
	}
	
	@Override
	public void encode(final PacketByteBuf buffer) {
	}
	
	@Override
	public void decode(final PacketByteBuf buffer) {
		posX = buffer.readFloat();
		posY = buffer.readFloat();
		posZ = buffer.readFloat();
	}
	
	@Override
	public void handleClient(final PacketContext context) {
	}
	
	@Override
	public void handleServer(final PacketContext context) {
		if (VrCraftServer.CONFIG.enableTeleport) {
			ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
			context.getTaskQueue().execute(() -> player.setPos(posX, posY, posZ));
		}
	}
}
