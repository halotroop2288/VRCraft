package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.common.util.VRPlayerData;
import net.minecraft.network.PacketByteBuf;

import java.util.List;

public abstract class VRPacketHandler {
	public final VRPlayerData data;
	public final PacketByteBuf buffer;
	
	public VRPacketHandler(VRPlayerData data, PacketByteBuf buffer) {
		this.data = data;
		this.buffer = buffer;
	}
	
	public abstract void activeHand();
	
	public abstract void bowDraw();
	
	public abstract void climbing(BlockListMode blockMode, List<String> list);
	
	public abstract void controller(Controller controller);
	
	public abstract void crawling();
	
	public void hmd() {
		this.data.head = DeviceData.decode(this.buffer);
	}
	
	public void height() {
		this.data.height = this.buffer.readFloat();
	}
	
	public abstract void teleport();
	
	public abstract void version(String message);
	
	public void worldScale() {
		data.worldScale = buffer.readFloat();
	}
	
	public enum BlockListMode {
		NONE,
		INCLUDE,
		EXCLUDE
	}
	
	// TODO: Make this extensible to many controllers
	public enum Controller {
		RIGHT, LEFT
	}
}
