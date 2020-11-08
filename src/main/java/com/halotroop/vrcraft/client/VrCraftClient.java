package com.halotroop.vrcraft.client;

import com.halotroop.vrcraft.client.network.packet.ClientVRPacketRegistrar;
import io.github.cottonmc.cotton.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

// This part should function identically to the Non-VR version of Vivecraft upon first release.
// Later, this should get full support for SteamVR.
@Environment(EnvType.CLIENT)
public class VrCraftClient implements ClientModInitializer {
	public static ClientConfig CLIENT_CONFIG;
	
	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing VRCraft Client...");
		CLIENT_CONFIG = ConfigManager.loadConfig(ClientConfig.class);
		ClientEventRegistrar.init();
		ClientVRPacketRegistrar.init();
	}
}
