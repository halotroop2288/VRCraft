package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class WorldScalePacket implements VivecraftPacket {
	public float worldScale;
	
	public WorldScalePacket() {
	}
	
	@Override
	public void encode(final PacketByteBuf buffer) {
		buffer.writeFloat(worldScale);
	}
	
	@Override
	public void decode(final PacketByteBuf buffer) {
		worldScale = buffer.readFloat();
	}
	
	@Override
	public void handleClient(final PacketContext context) {
	}
	
	@Override
	public void handleServer(final PacketContext context) {
		ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
		context.getTaskQueue().execute(() -> {
			if (!PlayerTracker.hasPlayerData(player))
				return;
			VRPlayerData data = PlayerTracker.getPlayerData(player, true);
			data.worldScale = worldScale;
		});
	}
}
