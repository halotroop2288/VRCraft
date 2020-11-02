package com.halotroop.vrcraft.common;

import io.github.cottonmc.cotton.logging.ModLogger;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class VrCraft implements ModInitializer {
	public static final ModLogger LOGGER = new ModLogger("VRCraft");
	
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing VRCraft...");
	}
}
