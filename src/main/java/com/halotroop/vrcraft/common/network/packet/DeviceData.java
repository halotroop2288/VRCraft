package com.halotroop.vrcraft.common.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public abstract class DeviceData implements BiPacket {
	public double posX;
	public double posY;
	public double posZ;
	public float rotX;
	public float rotY;
	public float rotZ;
	public float rotW;
	
	DeviceData() {
	}
	
	public DeviceData(double x, double y, double z, float rotW, float rotX, float rotY, float rotZ) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.rotW = rotW;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		buffer.writeDouble(posX);
		buffer.writeDouble(posY);
		buffer.writeDouble(posZ);
		buffer.writeFloat(rotW);
		buffer.writeFloat(rotX);
		buffer.writeFloat(rotY);
		buffer.writeFloat(rotZ);
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		posX = buffer.readDouble();
		posY = buffer.readDouble();
		posZ = buffer.readDouble();
		rotW = buffer.readFloat();
		rotX = buffer.readFloat();
		rotY = buffer.readFloat();
		rotZ = buffer.readFloat();
	}
	
	public Vec3d getPos() {
		return new Vec3d(posX, posY, posZ);
	}
	
	public void setPos(Vec3d pos) {
		posX = pos.x;
		posY = pos.y;
		posZ = pos.z;
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
}
