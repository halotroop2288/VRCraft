package com.halotroop.vrcraft.client.network.packet;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingOverridePacket implements S2CPacket {
	public Map<String, Object> settings = new HashMap<>();
	
	public SettingOverridePacket() {}
	
	public SettingOverridePacket(HashMap<String, Object> settings) {
		this.settings = settings;
	}
	
	@Override
	public void read(PacketByteBuf buf) {
		while (buf.readableBytes() > 0) {
			settings.put(buf.readString(32767), buf.readString(32767));
		}
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		for (Map.Entry<String, Object> entry : settings.entrySet()) {
			buf.writeString(entry.getKey());
			buf.writeString(entry.getValue().toString());
		}
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
		// TODO
	}
}
