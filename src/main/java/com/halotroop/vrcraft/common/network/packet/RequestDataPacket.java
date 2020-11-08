package com.halotroop.vrcraft.common.network.packet;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

public class RequestDataPacket implements VivecraftPacket {
	public RequestDataPacket() {
	}
	
	@Override
	public void encode(final PacketByteBuf buffer) {
	}
	
	@Override
	public void decode(final PacketByteBuf buffer) {
	}
	
	@Override
	public void handleClient(final PacketContext context) {
	}
	
	@Override
	public void handleServer(final PacketContext context) {
	}
}
