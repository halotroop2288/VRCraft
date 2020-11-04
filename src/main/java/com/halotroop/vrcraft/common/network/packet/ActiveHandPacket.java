package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.minecraft.network.PacketByteBuf;

public class ActiveHandPacket implements BiPacket {
	public int activeHand;
	
	public ActiveHandPacket() {}
	
	public ActiveHandPacket(int activeHand) {
		this.activeHand = activeHand;
	}
	
	@Override
	public void read(PacketByteBuf buf) {
	
	}
	
	@Override
	public void write(PacketByteBuf buf) {
	
	}
	
	@Override
	public void applyServer(VRC2SPacketListener listener) {
		VrCraft.LOGGER.devInfo("Received active hand packet");
		listener.applyActiveHand(activeHand);
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
		listener.applyActiveHand(activeHand);
	}
}
