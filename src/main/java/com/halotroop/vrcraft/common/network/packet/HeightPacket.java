package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.minecraft.network.PacketByteBuf;

public class HeightPacket implements BiPacket {
	public float height;
	
	public HeightPacket() {}
	
	public HeightPacket(float height) {
		this.height = height;
	}
	
	@Override
	public void read(PacketByteBuf buf) {
		this.height = buf.readFloat();
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.height);
	}
	
	@Override
	public void applyServer(VRC2SPacketListener listener) {
		VrCraft.LOGGER.devInfo("Received height packet");
		listener.applyHeight(this.height);
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
		listener.applyHeight(this.height);
	}
}
