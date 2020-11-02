package com.halotroop.vrcraft.common.network.packet;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public class UberPacket implements Packet<ServerVRDataPacketListener> {
	public UUID uuid;
	public HeadData headData;
	public ControllerData0 controller0Data;
	public ControllerData1 controller1Data;
	public float worldScale;
	public float height;
	
	public UberPacket(UUID uuid, HeadData headData, ControllerData0 controller0Data,
	                  ControllerData1 controller1Data, float worldScale, float height) {
		this.uuid = uuid;
		this.headData = headData;
		this.controller0Data = controller0Data;
		this.controller1Data = controller1Data;
		this.worldScale = worldScale;
		this.height = height;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
		headData.write(buffer);
		controller0Data.write(buffer);
		controller1Data.write(buffer);
		buffer.writeFloat(worldScale);
		buffer.writeFloat(height);
	}
	
	@Override
	public void apply(ServerVRDataPacketListener listener) {
		listener.onUberPacketSync(this);
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		uuid = new UUID(buffer.readLong(), buffer.readLong());
		headData = new HeadData();
		headData.read(buffer);
		controller0Data = new ControllerData0();
		controller0Data.read(buffer);
		controller1Data = new ControllerData1();
		controller1Data.read(buffer);
		worldScale = buffer.readFloat();
		height = buffer.readFloat();
	}
	
	private abstract static class DeviceData implements Packet<ServerVRDataPacketListener> {
		public float posX;
		public float posY;
		public float posZ;
		public float rotW;
		public float rotX;
		public float rotY;
		public float rotZ;
		
		public DeviceData() {}
		
		public DeviceData(float x, float y, float z, float rotW, float pitch, float yaw, float roll) {
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			this.rotW = rotW;
			this.rotX = pitch;
			this.rotY = yaw;
			this.rotZ = roll;
		}
		
		@Override
		public void read(PacketByteBuf buffer) {
			buffer.writeFloat(posX);
			buffer.writeFloat(posY);
			buffer.writeFloat(posZ);
			buffer.writeFloat(rotW);
			buffer.writeFloat(rotX);
			buffer.writeFloat(rotY);
			buffer.writeFloat(rotZ);
		}
		
		@Override
		public void write(PacketByteBuf buffer) {
			posX = buffer.readFloat();
			posY = buffer.readFloat();
			posZ = buffer.readFloat();
			rotW = buffer.readFloat();
			rotX = buffer.readFloat();
			rotY = buffer.readFloat();
			rotZ = buffer.readFloat();
		}
	}
	
	public static class HeadData extends DeviceData {
		public boolean seated;
		
		public HeadData() {}
		
		public HeadData(boolean seated, float x, float y, float z,
		                       float rotW, float pitch, float yaw, float roll) {
			super(x,y,z, rotW, pitch, yaw, roll);
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
		
		@Override
		public void apply(ServerVRDataPacketListener listener) {
			listener.onHeadDataSync(this);
		}
	}
	
	public static class ControllerData0 extends DeviceData {
		public boolean right;
		public boolean handsReversed;
		
		public ControllerData0() {
			super();
		}
		
		public ControllerData0(boolean handsReversed, float x, float y, float z,
		                       float rotW, float pitch, float yaw, float roll) {
			super(x, y, z, rotW, pitch, yaw, roll);
			this.handsReversed = handsReversed;
		}
		
		/**
		 * To avoid having to write this whole class twice
		 * @return the data object itself
		 */
		public ControllerData0 right() {
			this.right = true;
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
		public void apply(ServerVRDataPacketListener listener) {
			listener.onControllerDataSync0(this);
		}
	}
	
	// FIXME this is a redundant class. It is only used for compatibility with Vivecraft and parity with VFE
	public static class ControllerData1 extends ControllerData0 {
		public ControllerData1() {}
		
		public ControllerData1(boolean handsReversed, float x, float y, float z,
		                       float rotW, float pitch, float yaw, float roll) {
			super(handsReversed, x, y, z, rotW, pitch, yaw, roll);
		}
		
		@Override
		public void apply(ServerVRDataPacketListener listener) {
			listener.onControllerDataSync1(this);
		}
	}
}
