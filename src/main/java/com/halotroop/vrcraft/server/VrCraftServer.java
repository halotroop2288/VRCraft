package com.halotroop.vrcraft.server;

import io.github.cottonmc.cotton.config.ConfigManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

// This part should mirror the functionality of Vivecraft Spigot/Forge Extensions
// For maximum Fabric compatibility with Vivecraft
// According to the CurseForge page, VFE is GPLv3, but Techjar doesn't really give a crap.
@Environment(EnvType.SERVER)
public class VrCraftServer implements DedicatedServerModInitializer {
	public static ServerConfig config = ConfigManager.loadConfig(ServerConfig.class);
	
	@Override
	public void onInitializeServer() {
		LOGGER.info("Initializing VRCraft Server...");
		
		ServerEventRegistrarV2.init();
	}
}
