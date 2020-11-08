package com.halotroop.vrcraft.server;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.network.packet.VivecraftPacket;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

// This part should mirror the functionality of Vivecraft Spigot/Forge Extensions
// For maximum Fabric compatibility with Vivecraft
// According to the CurseForge page, VFE is GPLv3, but Techjar doesn't really give a crap.
@Environment(EnvType.SERVER)
public class VrCraftServer implements DedicatedServerModInitializer {
	public static final ServerConfig CONFIG = VrCraft.SERVER_CONFIG;
	
	@Override
	public void onInitializeServer() {
		LOGGER.info("Initializing VRCraft Server...");
		
		ServerEventRegistrar.init();
		
		ServerSidePacketRegistry.INSTANCE.register(VrCraft.VIVECRAFT_CHANNEL_ID, (context, data) -> {
			if (!data.isReadable() || context.getPlayer() == null) return;
			VivecraftPacket.Discriminator discriminator = VivecraftPacket.Discriminator.values()[data.readByte()];
			LOGGER.devInfo("Received a Vivecraft " + discriminator + " packet");
			
			if (!data.isReadable() && !discriminator.equals(VivecraftPacket.Discriminator.VERSION)) {
				LOGGER.error("Invalid Vivecraft data packet.");
			}
			
			VivecraftPacket packet = discriminator.supplier.get();
			
			packet.decode(data);
			packet.handleServer(context);
		});
	}
}
