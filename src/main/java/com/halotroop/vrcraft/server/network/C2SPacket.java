package com.halotroop.vrcraft.server.network;

import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;

/**
 * For packets that should be handled on the server
 */
public interface C2SPacket extends Packet<VRC2SPacketListener> {
	@Environment(EnvType.SERVER)
	void applyServer(VRC2SPacketListener listener);
	
	/**
	 * @deprecated use applyServer instead!
	 */
	@Deprecated	@Override @Environment(EnvType.SERVER)
	default void apply(VRC2SPacketListener listener) {
		applyServer(listener);
	}
}
