package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.server.VrCraftServer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/*
 * For whatever reason this uses a serializer instead of just
 * packing the data into the buffer.
 */
public class ClimbingPacket implements VivecraftPacket {
	
	public BlockListMode blockListMode;
	public List<? extends String> blockList;
	
	public ClimbingPacket() {
	}
	
	public ClimbingPacket(BlockListMode blockListMode, List<? extends String> blockList) {
		this.blockListMode = blockListMode;
		this.blockList = blockList;
	}
	
	@Override
	public void encode(final PacketByteBuf buffer) {
		buffer.writeByte(1); // allow climbey
		buffer.writeByte(VrCraftServer.CONFIG.blockMode.ordinal());
		for (String s : VrCraftServer.CONFIG.blockList) {
			buffer.writeString(s);
		}
	}
	
	@Override
	public void decode(final PacketByteBuf buffer) {
	}
	
	@Override
	public void handleClient(final PacketContext context) {
	}
	
	@Override
	public void handleServer(final PacketContext context) {
		ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
		player.fallDistance = 0;
		player.networkHandler.floatingTicks = 0;
	}
	
	public enum BlockListMode {
		NONE,
		INCLUDE,
		EXCLUDE
	}
}
