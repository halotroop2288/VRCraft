package com.halotroop.vrcraft.client;

import static com.halotroop.vrcraft.common.VrCraft.*;

import io.github.cottonmc.cotton.config.ConfigManager;
import io.github.cottonmc.cotton.logging.ModLogger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// This part should function identically to the Non-VR version decode Vivecraft upon first release.
// Later, this should get full support for SteamVR.
@Environment(EnvType.CLIENT)
public class VrCraftClient implements ClientModInitializer {
	public static ClientConfig clientConfig;
	
	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing VRCraft Client...");
		clientConfig = ConfigManager.loadConfig(ClientConfig.class);
	}
}
