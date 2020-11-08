package com.halotroop.vrcraft.common.network.packet;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

public class HeadDataPacket implements VivecraftPacket {
	public boolean seated;
	public float posX;
	public float posY;
	public float posZ;
	public float rotW;
	public float rotX;
	public float rotY;
	public float rotZ;
	
	public HeadDataPacket() {
	}
	
	public HeadDataPacket(boolean seated, float posX, float posY, float posZ, float rotW, float rotX, float rotY, float rotZ) {
		this.seated = seated;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.rotW = rotW;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
	}
	
	@Override
	public void encode(final PacketByteBuf buffer) {
		buffer.writeBoolean(seated);
		buffer.writeFloat(posX);
		buffer.writeFloat(posY);
		buffer.writeFloat(posZ);
		buffer.writeFloat(rotW);
		buffer.writeFloat(rotX);
		buffer.writeFloat(rotY);
		buffer.writeFloat(rotZ);
	}
	
	@Override
	public void decode(final PacketByteBuf buffer) {
		seated = buffer.readBoolean();
		posX = buffer.readFloat();
		posY = buffer.readFloat();
		posZ = buffer.readFloat();
		rotW = buffer.readFloat();
		rotX = buffer.readFloat();
		rotY = buffer.readFloat();
		rotZ = buffer.readFloat();
	}
	
	@Override
	public void handleClient(final PacketContext context) {
	}
	
	@Override
	public void handleServer(final PacketContext context) {
	}
}
