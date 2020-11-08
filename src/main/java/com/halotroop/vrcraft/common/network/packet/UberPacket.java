package com.halotroop.vrcraft.common.network.packet;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public class UberPacket implements VivecraftPacket {
	public UUID uuid;
	public DeviceDataPacket headData;
	public DeviceDataPacket controller0Data;
	public DeviceDataPacket controller1Data;
	public float worldScale;
	public float height;
	
	public UberPacket() {
	}
	
	public UberPacket(UUID uuid, DeviceDataPacket headData, DeviceDataPacket controller0Data,
	                  DeviceDataPacket controller1Data, float worldScale, float height) {
		this.uuid = uuid;
		this.headData = headData;
		this.controller0Data = controller0Data;
		this.controller1Data = controller1Data;
		this.worldScale = worldScale;
		this.height = height;
	}
	
	@Override
	public void encode(final PacketByteBuf buffer) {
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
		headData.encode(buffer);
		controller0Data.encode(buffer);
		controller1Data.encode(buffer);
		buffer.writeFloat(worldScale);
		buffer.writeFloat(height);
	}
	
	@Override
	public void decode(final PacketByteBuf buffer) {
		uuid = new UUID(buffer.readLong(), buffer.readLong());
		headData.decode(buffer);
		controller0Data.decode(buffer);
		controller1Data.decode(buffer);
		worldScale = buffer.readFloat();
		height = buffer.readFloat();
	}
	
	@Override
	public void handleClient(final PacketContext context) {
	}
	
	@Override
	public void handleServer(final PacketContext context) {
	}
}
