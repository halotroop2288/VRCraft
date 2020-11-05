package com.halotroop.vrcraft.common.network.packet;

import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public class UberPacket {
	public UUID uuid;
	public DeviceData headData;
	public DeviceData controllerLData;
	public DeviceData controllerRData;
	public float worldScale;
	public float height;
	
	public UberPacket(UUID uuid, DeviceData headData, DeviceData controllerLData, DeviceData controllerRData,
	                  float worldScale, float height) {
		this.uuid = uuid;
		this.headData = headData;
		this.controllerLData = controllerLData;
		this.controllerRData = controllerRData;
		this.worldScale = worldScale;
		this.height = height;
	}
	
	public PacketByteBuf encode(final PacketByteBuf buffer) {
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
		this.headData.encode(buffer);
		this.controllerLData.encode(buffer);
		this.controllerRData.encode(buffer);
		buffer.writeFloat(worldScale);
		buffer.writeFloat(height);
		return buffer;
	}
	
	public static UberPacket decode(final PacketByteBuf buffer) {
		return new UberPacket(new UUID(buffer.readLong(), buffer.readLong()), DeviceData.decode(buffer),
				DeviceData.decode(buffer), DeviceData.decode(buffer), buffer.readFloat(), buffer.readFloat());
	}
}
