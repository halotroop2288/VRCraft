package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.minecraft.network.PacketByteBuf;

public class HeadData extends DeviceData {
	public boolean seated;
	
	public HeadData() {
	}
	
	public HeadData(PacketByteBuf buf) {
		this.read(buf);
	}
	
	public HeadData(boolean seated, double x, double y, double z,
	                float rotW, float rotX, float rotY, float rotZ) {
		super(x, y, z, rotW, rotX, rotY, rotZ);
		this.seated = seated;
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		seated = buffer.readBoolean();
		super.read(buffer);
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeBoolean(seated);
		super.write(buffer);
	}
	
	public static HeadData decode(PacketByteBuf payload) {
		HeadData head = new HeadData();
		head.read(payload);
		return head;
	}
	
	@Override
	public void applyServer(VRC2SPacketListener listener) {
		listener.applyHMDSync(this);
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
	
	}
}
