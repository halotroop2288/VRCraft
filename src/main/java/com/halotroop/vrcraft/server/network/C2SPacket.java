package com.halotroop.vrcraft.server.network;

import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;

/**
 * For packets that should be handled on the server
 */
public interface C2SPacket extends Packet<VRC2SPacketListener> {
	Identifier VIVECRAFT_CHANNEL_ID = new Identifier("vivecraft", "data");
	
	@Environment(EnvType.SERVER)
	void applyServer(VRC2SPacketListener listener);
	
	/**
	 * @deprecated use applyServer instead!
	 */
	@Deprecated
	@Override
	@Environment(EnvType.SERVER)
	default void apply(VRC2SPacketListener listener) {
		applyServer(listener);
	}
}
