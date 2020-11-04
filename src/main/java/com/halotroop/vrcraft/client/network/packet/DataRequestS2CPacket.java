package com.halotroop.vrcraft.client.network.packet;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;

public class DataRequestS2CPacket implements Packet<VRS2CPacketListener> {
	@Override
	public void read(PacketByteBuf buf) {}
	
	@Override
	public void write(PacketByteBuf buf) {}
	
	@Override
	public void apply(VRS2CPacketListener listener) {}
}
