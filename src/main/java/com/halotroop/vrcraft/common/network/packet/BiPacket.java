package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.client.network.packet.S2CPacket;
import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * For packets which go in both directions <br>
 *
 * @see com.halotroop.vrcraft.server.network.C2SPacket
 * @see com.halotroop.vrcraft.client.network.packet.S2CPacket
 */
public interface BiPacket extends S2CPacket {
	@Environment(EnvType.SERVER)
	void applyServer(VRC2SPacketListener listener);
	
	@Environment(EnvType.CLIENT)
	void applyClient(VRS2CPacketListener listener);
}
