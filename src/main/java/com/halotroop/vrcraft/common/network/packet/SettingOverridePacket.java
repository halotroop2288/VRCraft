package com.halotroop.vrcraft.common.network.packet;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;

public class SettingOverridePacket implements VivecraftPacket {
	public Map<String, Object> settings = new HashMap<>();
	
	public SettingOverridePacket() {
	}
	
	public SettingOverridePacket(Map<String, Object> settings) {
		this.settings = settings;
	}
	
	@Override
	public void encode(final PacketByteBuf buffer) {
		for (Map.Entry<String, Object> entry : settings.entrySet()) {
			buffer.writeString(entry.getKey());
			buffer.writeString(entry.getValue().toString());
		}
	}
	
	@Override
	public void decode(final PacketByteBuf buffer) {
		while (buffer.readableBytes() > 0) {
			settings.put(buffer.readString(32767), buffer.readString(32767));
		}
	}
	
	@Override
	public void handleClient(final PacketContext context) {
	}
	
	@Override
	public void handleServer(final PacketContext context) {
	}
}
