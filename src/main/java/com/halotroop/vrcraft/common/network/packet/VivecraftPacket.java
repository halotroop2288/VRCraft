package com.halotroop.vrcraft.common.network.packet;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Supplier;

public interface VivecraftPacket {
	void encode(final PacketByteBuf buffer);
	
	void decode(final PacketByteBuf buffer);
	
	void handleClient(final PacketContext context);
	
	void handleServer(final PacketContext context);
	
	enum Discriminator {
		VERSION(VersionPacket::new),
		REQUEST_DATA(RequestDataPacket::new),
		HEAD_DATA(() -> new DeviceDataPacket(DeviceDataPacket.Device.HMD)),
		CONTROLLER_0_DATA(() -> new DeviceDataPacket(DeviceDataPacket.Device.CONTROLLER_0)),
		CONTROLLER_1_DATA(() -> new DeviceDataPacket(DeviceDataPacket.Device.CONTROLLER_1)),
		WORLD_SCALE(WorldScalePacket::new),
		DRAW(WorldScalePacket::new),
		MOVE_MODE(MoveModePacket::new),
		UBERPACKET(UberPacket::new),
		TELEPORT(TeleportPacket::new),
		CLIMBING(ClimbingPacket::new),
		SETTING_OVERRIDE(SettingOverridePacket::new),
		HEIGHT(HeadDataPacket::new),
		ACTIVE_HAND(ActiveHandPacket::new),
		CRAWL(CrawlPacket::new);
		
		public Supplier<? extends VivecraftPacket> supplier;
		
		Discriminator(Supplier<? extends VivecraftPacket> supplier) {
			this.supplier = supplier;
		}
	}
}
