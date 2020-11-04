package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.client.network.packet.VRS2CPacketListener;
import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.minecraft.network.PacketByteBuf;

public class CrawlingPacket implements BiPacket {
	public boolean crawling;
	
	public CrawlingPacket() {}
	
	public CrawlingPacket(boolean crawling) {
		this.crawling = crawling;
	}
	
	@Override
	public void read(PacketByteBuf buf) {
	
	}
	
	@Override
	public void write(PacketByteBuf buf) {
	
	}
	
	@Override
	public void applyServer(VRC2SPacketListener listener) {
		VrCraft.LOGGER.devInfo("Received crawling packet");
		listener.applyCrawling(crawling);
	}
	
	@Override
	public void applyClient(VRS2CPacketListener listener) {
		listener.applyCrawling();
	}
}
