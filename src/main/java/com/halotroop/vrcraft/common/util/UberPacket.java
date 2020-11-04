package com.halotroop.vrcraft.common.util;

import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.common.network.packet.ControllerData;
import com.halotroop.vrcraft.common.network.packet.HeadData;
import com.halotroop.vrcraft.common.network.packet.BiPacket;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

/**
 * A packet containing the data decode a player's device locations
 */
public class UberPacket implements BiPacket {
	public UUID uuid;
	public HeadData headData;
	public ControllerData controllerLData;
	public ControllerData controllerRData;
	public float worldScale;
	public float height;
	
	public UberPacket() {}
	
	public UberPacket(PacketByteBuf buf) {
		this.read(buf);
	}
	
	public UberPacket(UUID uuid, HeadData headData, ControllerData controllerLData,
	                  ControllerData controllerRData, float worldScale, float height) {
		this.uuid = uuid;
		this.headData = headData;
		this.controllerLData = controllerLData;
		this.controllerRData = controllerRData;
		this.worldScale = worldScale;
		this.height = height;
	}
	
	public UberPacket(UUID uuid, VRPlayerData data, float worldScale, float height) {
		this(uuid, data.head, data.controllerL, data.controllerR, worldScale, height);
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
		headData.write(buffer);
		controllerLData.write(buffer);
		controllerRData.write(buffer);
		buffer.writeFloat(worldScale);
		buffer.writeFloat(height);
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		uuid = new UUID(buffer.readLong(), buffer.readLong());
		headData = new HeadData();
		headData.read(buffer);
		controllerLData = new ControllerData(buffer);
		controllerRData = new ControllerData(buffer).right();
		worldScale = buffer.readFloat();
		height = buffer.readFloat();
	}
	
	@Override
	public void applyServer(VRC2SPacketListener listener) {
		LOGGER.devInfo("Received Uber Packet");
		listener.applyUberSync(this);
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
		LOGGER.devInfo("Received Uber Packet");
		listener.onUberPacketSync(this);
	}
}
