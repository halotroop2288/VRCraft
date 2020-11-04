package com.halotroop.vrcraft.client.network.packet;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.IOException;

public interface S2CPacket extends Packet<VRS2CPacketListener> {
	Identifier VIVECRAFT_CHANNEL_ID = new Identifier("vivecraft", "data");
	
	@Environment(EnvType.SERVER)
	default void sendToClient(PlayerEntity player) {
		try {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			this.read(buf);
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, VIVECRAFT_CHANNEL_ID, buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Environment(EnvType.CLIENT)
	void applyClient(VRS2CPacketListener listener);
	
	/**
	 * @deprecated use applyClient instead!
	 */
	@Deprecated
	@Override
	@Environment(EnvType.SERVER)
	default void apply(VRS2CPacketListener listener) {
	}
}
