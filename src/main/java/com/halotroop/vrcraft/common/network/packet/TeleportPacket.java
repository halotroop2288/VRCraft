package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.network.packet.BiPacket;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.minecraft.network.PacketByteBuf;

public class TeleportPacket implements BiPacket {
	public float x, y, z;
	
	public TeleportPacket() {}
	
	public TeleportPacket(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void read(PacketByteBuf buf) {
		x = buf.readFloat();
		y = buf.readFloat();
		z = buf.readFloat();
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(x);
		buf.writeFloat(y);
		buf.writeFloat(z);
	}
	
	@Override
	public void applyServer(VRC2SPacketListener listener) {
		VrCraft.LOGGER.devInfo("Received teleport packet");
		listener.applyTeleport(x, y, z);
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
		listener.applyTeleport(x, y, z);
	}
}
