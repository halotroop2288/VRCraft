package com.halotroop.vrcraft.common.network.packet;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

public class MoveModePacket implements VivecraftPacket {
	public boolean freeMove;
	
	public MoveModePacket() {
	}
	
	public MoveModePacket(boolean freeMove) {
		this.freeMove = freeMove;
	}
	
	@Override
	public void encode(final PacketByteBuf buffer) {
		//buffer.writeBoolean(freeMove);
	}
	
	@Override
	public void decode(final PacketByteBuf buffer) {
		//freeMove = buffer.readBoolean();
	}
	
	@Override
	public void handleClient(final PacketContext context) {
	}
	
	@Override
	public void handleServer(final PacketContext context) {
		/*ServerPlayerEntity player = context.getSender();
		context.enqueueWork(() -> {
			VRPlayerData data = PlayerTracker.getPlayerData(player, true);
			data.freeMove = freeMove;
		});*/
	}
}
