package com.halotroop.vrcraft.client.network.packet;

import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;

public class SettingOverridePacket {
	public HashMap<String, Object> settings;
	
	public SettingOverridePacket(HashMap<String, Object> settings) {
		this.settings = settings;
	}
	
	public PacketByteBuf encode(final PacketByteBuf buffer) {
		for (Map.Entry<String, Object> entry : settings.entrySet()) {
			buffer.writeString(entry.getKey());
			buffer.writeString(entry.getValue().toString());
		}
		return buffer;
	}
	
	public static SettingOverridePacket decode(final PacketByteBuf buffer) {
		HashMap<String, Object> settings = new HashMap<>();
		while (buffer.isReadable()) {
			settings.put(buffer.readString(32767), buffer.readString(32767));
		}
		return new SettingOverridePacket(settings);
	}
}
