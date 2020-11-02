package com.halotroop.vrcraft.server;

import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.Util;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import io.github.cottonmc.cotton.config.ConfigManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

// This part should mirror the functionality of Vivecraft Spigot/Forge Extensions
// For maximum Fabric compatibility with Vivecraft
// According to the CurseForge page, VFE is GPLv3, but Techjar doesn't really give a crap.
public class VrCraftServer implements DedicatedServerModInitializer {
	public static ServerConfig config;
	
	@Override
	public void onInitializeServer() {
		LOGGER.info("Initializing VRCraft Server...");
		config = ConfigManager.loadConfig(ServerConfig.class);
		ServerSidePacketRegistry.INSTANCE.register(Util.vrID("controller_0_data"),
				(context, packetData) -> context.getTaskQueue().execute(() -> {
					// I am *extremely* uncertain whether this is right or not.
					PlayerEntity player = context.getPlayer();
					if (!PlayerTracker.hasPlayerData(player))
						return;
					VRPlayerData data = PlayerTracker.getPlayerData(player, true);
					data.handsReversed = packetData.readBoolean();
					VRPlayerData.ObjectInfo info = data.controller0;
					info.posX = packetData.readByte();
					info.posY = packetData.readByte();
					info.posZ = packetData.readByte();
					info.rotW = packetData.readByte();
					info.rotX = packetData.readByte();
					info.rotY = packetData.readByte();
					info.rotZ = packetData.readByte();
				}));
		ServerSidePacketRegistry.INSTANCE.register(Util.vrID("controller_1_data"),
				(context, packetData) -> context.getTaskQueue().execute(() -> {
					// I am *extremely* uncertain whether this is right or not.
					PlayerEntity player = context.getPlayer();
					if (!PlayerTracker.hasPlayerData(player))
						return;
					VRPlayerData data = PlayerTracker.getPlayerData(player, true);
					data.handsReversed = packetData.readBoolean();
					VRPlayerData.ObjectInfo info = data.controller1;
					info.posX = packetData.readByte();
					info.posY = packetData.readByte();
					info.posZ = packetData.readByte();
					info.rotW = packetData.readByte();
					info.rotX = packetData.readByte();
					info.rotY = packetData.readByte();
					info.rotZ = packetData.readByte();
				}));
		ServerSidePacketRegistry.INSTANCE.register(Util.vrID("head_data"),
				(context, packetData) -> context.getTaskQueue().execute(() -> {
					// I am *extremely* uncertain whether this is right or not.
					PlayerEntity player = context.getPlayer();
					if (!PlayerTracker.hasPlayerData(player))
						return;
					VRPlayerData data = PlayerTracker.getPlayerData(player, true);
					VRPlayerData.ObjectInfo info = data.head;
					data.seated = packetData.readBoolean();
					info.posX = packetData.readByte();
					info.posY = packetData.readByte();
					info.posZ = packetData.readByte();
					info.rotW = packetData.readByte();
					info.rotX = packetData.readByte();
					info.rotY = packetData.readByte();
					info.rotZ = packetData.readByte();
				}));
		
		ServerEventHandler.init();
	}
}
