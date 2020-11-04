package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.minecraft.network.PacketByteBuf;

public class BowDrawPacket implements BiPacket {
	public float drawDist;
	
	public BowDrawPacket() {}
	
	public BowDrawPacket(float drawDist) {
		this.drawDist = drawDist;
	}
	
	@Override
	public void read(PacketByteBuf buf) {
		this.drawDist = buf.readFloat();
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(drawDist);
	}
	
	@Override
	public void applyServer(VRC2SPacketListener listener) {
		VrCraft.LOGGER.devInfo("Received a bow draw packet");
		listener.applyBowDraw(drawDist);
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
		listener.applyBowDraw(drawDist);
	}
}
