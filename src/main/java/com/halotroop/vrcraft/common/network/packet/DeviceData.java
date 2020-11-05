package com.halotroop.vrcraft.common.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class DeviceData {
	public boolean b1;
	public float x;
	public float y;
	public float z;
	public float rotX;
	public float rotY;
	public float rotZ;
	public float rotW;
	
	public DeviceData() {
	}
	
	public DeviceData(boolean b1, float x, float y, float z, float rotW, float rotX, float rotY, float rotZ) {
		this.b1 = b1;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotW = rotW;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
	}
	
	public Vec3d getPos() {
		return new Vec3d(x, y, z);
	}
	
	public void setPos(Vec3d pos) {
		x = (float) pos.x;
		y = (float) pos.y;
		z = (float) pos.z;
	}
	
	public Quaternion getRotation() {
		return new Quaternion(rotX, rotY, rotZ, rotW); // FSR our Quaternion class puts w last in the constructor -_-
	}
	
	public void setRot(Quaternion quat) {
		rotX = quat.getX();
		rotY = quat.getY();
		rotZ = quat.getZ();
		rotW = quat.getW();
	}
	
	public PacketByteBuf encode(PacketByteBuf buffer) {
		buffer.writeBoolean(this.b1);
		buffer.writeFloat(this.x);
		buffer.writeFloat(this.y);
		buffer.writeFloat(this.z);
		buffer.writeFloat(this.rotW);
		buffer.writeFloat(this.rotX);
		buffer.writeFloat(this.rotY);
		buffer.writeFloat(this.rotZ);
		return buffer;
	}
	
	public static DeviceData decode(PacketByteBuf buffer) {
		return new DeviceData(buffer.readBoolean(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(),
				buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
	}
}
