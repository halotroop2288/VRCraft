package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.server.ServerConfig;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

public class ClimbingPacket {
	private boolean allowClimbey = true;
	private final VRPacketHandler.BlockListMode mode;
	private final List<String> blockList;
	
	private static final ServerConfig CONFIG = VrCraft.SERVER_CONFIG;
	
	public ClimbingPacket(VRPacketHandler.BlockListMode mode, List<String> blockList) {
		this.mode = mode;
		this.blockList = blockList;
	}
	
	public ClimbingPacket(boolean allowClimbey, VRPacketHandler.BlockListMode mode, List<String> blockList) {
		this(mode, blockList);
		this.allowClimbey = allowClimbey;
	}
	
	public PacketByteBuf encode(final PacketByteBuf buffer) {
		buffer.writeByte(1); // allow climbey
		buffer.writeEnumConstant(CONFIG.blockMode);
		CONFIG.blockList.forEach(buffer::writeString);
		return buffer;
	}
	
	public static ClimbingPacket decode(final PacketByteBuf buffer) {
		boolean allow = buffer.readByte() == 1;
		VRPacketHandler.BlockListMode mode = buffer.readEnumConstant(VRPacketHandler.BlockListMode.class);
		List<String> list = new ArrayList<>();
		while (buffer.isReadable()) list.add(buffer.readString());
		return new ClimbingPacket(allow, mode, list);
	}
}
