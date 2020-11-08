package com.halotroop.vrcraft.common.network.packet;

import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.VrCraftServer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class CrawlPacket implements VivecraftPacket {
	public boolean crawling;
	
	public CrawlPacket() {
	}
	
	public CrawlPacket(boolean crawling) {
		this.crawling = crawling;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		crawling = buffer.readBoolean();
	}
	
	@Override
	public void handleClient(PacketContext context) {
	}
	
	@Override
	public void handleServer(PacketContext context) {
		if (VrCraftServer.CONFIG.enableCrawling) {
			ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
			context.getTaskQueue().execute(() -> {
				if (!PlayerTracker.hasPlayerData(player)) return;
				VRPlayerData data = PlayerTracker.getPlayerData(player, true);
				data.crawling = crawling;
				if (data.crawling) player.setPose(EntityPose.SWIMMING);
			});
		}
	}
}
