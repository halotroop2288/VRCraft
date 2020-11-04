package com.halotroop.vrcraft.client.network.packet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;

public interface S2CPacket extends Packet<VRS2CPacketListener> {
	@Environment(EnvType.CLIENT)
	void applyClient(VRS2CPacketListener listener);
	
	@Deprecated @Override @Environment(EnvType.SERVER)
	default void apply(VRS2CPacketListener listener) {
		applyClient(listener);
	}
}
