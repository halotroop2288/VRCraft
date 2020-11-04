package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.minecraft.network.PacketByteBuf;

public class WorldScalePacket implements BiPacket {
	public float worldScale;
	
	public WorldScalePacket() {}
	
	public WorldScalePacket(float worldScale) {
		this.worldScale = worldScale;
	}
	
	@Override
	public void read(PacketByteBuf buf) {
		worldScale = buf.readFloat();
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(worldScale);
	}
	
	@Override
	public void applyServer(VRC2SPacketListener listener) {
		VrCraft.LOGGER.devInfo("Received world scale packet");
		listener.applyWorldScale(worldScale);
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
		listener.applyWorldScale(worldScale);
	}
}
