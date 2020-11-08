package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class DeviceDataPacket implements VivecraftPacket {
	private Device device;
	public boolean b1; // handsReversed for controller, seated for head
	public float x;
	public float y;
	public float z;
	public float rotX;
	public float rotY;
	public float rotZ;
	public float rotW;
	
	public DeviceDataPacket(Device device) {
	}
	
	public DeviceDataPacket(boolean b1, float x, float y, float z, float rotW, float rotX, float rotY, float rotZ) {
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
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeBoolean(this.b1);
		buffer.writeFloat(this.x);
		buffer.writeFloat(this.y);
		buffer.writeFloat(this.z);
		buffer.writeFloat(this.rotW);
		buffer.writeFloat(this.rotX);
		buffer.writeFloat(this.rotY);
		buffer.writeFloat(this.rotZ);
	}
	
	public void decode(PacketByteBuf buffer) {
		this.b1 = buffer.readBoolean();
		this.x = buffer.readFloat();
		this.y = buffer.readFloat();
		this.z = buffer.readFloat();
		this.rotW = buffer.readFloat();
		this.rotX = buffer.readFloat();
		this.rotY = buffer.readFloat();
		this.rotZ = buffer.readFloat();
	}
	
	@Override
	public void handleClient(PacketContext context) {
	
	}
	
	@Override
	public void handleServer(PacketContext context) {
		ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
		switch (device) {
			case HMD:
				context.getTaskQueue().execute(() -> {
					if (!PlayerTracker.hasPlayerData(player))
						return;
					VRPlayerData data = PlayerTracker.getPlayerData(player, true);
					data.seated = b1;
					DeviceDataPacket info = data.head;
					info.x = x;
					info.y = y;
					info.z = z;
					info.rotW = rotW;
					info.rotX = rotX;
					info.rotY = rotY;
					info.rotZ = rotZ;
				});
			case CONTROLLER_0:
			case CONTROLLER_1:
				context.getTaskQueue().execute(() -> {
					if (!PlayerTracker.hasPlayerData(player))
						return;
					VRPlayerData data = PlayerTracker.getPlayerData(player, true);
					data.handsReversed = b1;
					DeviceDataPacket info = device == Device.CONTROLLER_0 ? data.controller0 : data.controller1;
					info.x = x;
					info.y = y;
					info.z = z;
					info.rotW = rotW;
					info.rotX = rotX;
					info.rotY = rotY;
					info.rotZ = rotZ;
				});
		}
	}
	
	public enum Device {
		CONTROLLER_0, CONTROLLER_1, HMD
	}
}
