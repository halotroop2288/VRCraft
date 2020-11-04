package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.minecraft.network.PacketByteBuf;

import java.nio.charset.StandardCharsets;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

/*
 * Why the fuck does the client want a length-prefixed string, but sends
 * a string that's just char bytes with no length prefix? This whole
 * protocol is an awful mess. I didn't write it, so don't blame me.
 *
 * - Techjar
 */
public class VersionPacket implements BiPacket {
	public String message;
	
	public VersionPacket() {
		this.message = VrCraft.MOD_VERSION;
	}
	
	public VersionPacket(String message) {
		this.message = message;
	}
	
	@Override
	public void read(PacketByteBuf buf) {
//		message = buf.readString(); // ????
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		message = new String(bytes, StandardCharsets.UTF_8);
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(message);
	}
	
	@Override
	public void applyServer(VRC2SPacketListener listener) {
		LOGGER.devInfo("Received version info packet");
		listener.applyVersionSync(message);
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
		LOGGER.devInfo("Received version info packet");
	}
}
