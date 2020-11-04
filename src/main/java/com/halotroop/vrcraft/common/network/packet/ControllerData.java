package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.minecraft.network.PacketByteBuf;

public class ControllerData extends DeviceData {
	private Controller controller = Controller.LEFT;
	public boolean handsReversed;
	
	public ControllerData() {}
	
	public ControllerData(PacketByteBuf buf) {
		this.read(buf);
	}
	
	public ControllerData(boolean handsReversed, double x, double y, double z,
	                      float rotW, float pitch, float yaw, float roll) {
		super(x, y, z, rotW, pitch, yaw, roll);
		this.handsReversed = handsReversed;
	}
	
	/**
	 * To avoid having to write this whole class twice
	 *
	 * @return the data object itself
	 */
	public ControllerData right() {
		this.controller = Controller.RIGHT;
		return this;
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		handsReversed = buffer.readBoolean();
		super.read(buffer);
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeBoolean(handsReversed);
		super.write(buffer);
	}
	
	@Override
	public void applyServer(VRC2SPacketListener listener) {
		listener.applyControllerSync(this, controller);
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
		listener.applyControllerSync();
	}
	
	// TODO: Make this extensible to many controllers
	public enum Controller {
		LEFT, RIGHT
	}
}
