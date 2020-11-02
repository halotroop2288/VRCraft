package com.halotroop.vrcraft.common.network.packet;

import net.minecraft.network.PacketByteBuf;

public interface IPacket {
	void write(final PacketByteBuf buffer);
	void read(final PacketByteBuf buffer);
	
	void handleClient();
	void handleServer();
}
