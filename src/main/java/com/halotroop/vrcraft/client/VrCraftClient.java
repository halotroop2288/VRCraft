package com.halotroop.vrcraft.client;

import static com.halotroop.vrcraft.common.VrCraft.*;
import net.fabricmc.api.ClientModInitializer;

// This part should function identically to the Non-VR version of Vivecraft upon first release.
public class VrCraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing VRCraft Client...");
	}
}
