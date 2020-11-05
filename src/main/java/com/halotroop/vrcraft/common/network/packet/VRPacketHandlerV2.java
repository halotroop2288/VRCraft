package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.common.util.VRPlayerData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class VRPacketHandlerV2 {
	public static final Identifier VIVECRAFT_CHANNEL_ID = new Identifier("vivecraft", "data");
	public final VRPlayerData data;
	public final PacketByteBuf buffer;
	
	public VRPacketHandlerV2(VRPlayerData data, PacketByteBuf buffer) {
		this.data = data;
		this.buffer = buffer;
	}
	
	public abstract void activeHand();
	
	public abstract void bowDraw();
	
	public abstract void climbing(BlockListMode blockMode, List<String> list);
	
	public abstract void controller(Controller controller);
	
	public abstract void crawling();
	
	public abstract void hmd(DeviceData hData);
	
	public abstract void height();
	
	public abstract void teleport();
	
	public abstract void version(String message);
	
	public abstract void worldScale(float worldScale);
	
	public enum BlockListMode {
		NONE,
		INCLUDE,
		EXCLUDE
	}
	
	// TODO: Make this extensible to many controllers
	public enum Controller {
		LEFT, RIGHT
	}
}
