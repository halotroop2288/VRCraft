package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.server.VrCraftServer;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.minecraft.network.PacketByteBuf;

import java.util.List;

public class ClimbingPacket implements BiPacket {
	public boolean allowClimbey = false;
	public BlockListMode mode;
	public List<? extends String> list;
	
	public ClimbingPacket() {}
	
	public ClimbingPacket(BlockListMode blockMode, List<String> list) {
		this.mode = blockMode;
		this.list = list;
	}
	
	@Override
	public void read(PacketByteBuf buf) {
		allowClimbey = buf.readByte() == 1;
		buf.readByte();
		// decode strings
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(1); // allow climbey
		buf.writeEnumConstant(VrCraftServer.config.blockMode);
		VrCraftServer.config.blockList.forEach(buf::writeString);
	}
	
	@Override
	public void applyServer(VRC2SPacketListener listener) {
		listener.applyClimbing();
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
		listener.applyClimbing(this);
	}
	
	public enum BlockListMode {
		NONE,
		INCLUDE,
		EXCLUDE
	}
}
