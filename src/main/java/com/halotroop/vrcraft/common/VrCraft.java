package com.halotroop.vrcraft.common;

import io.github.cottonmc.cotton.logging.ModLogger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Identifier;

public class VrCraft implements ModInitializer {
	public static final ModMetadata MOD_INFO;
	public static final String MOD_VERSION;
	public static final ModLogger LOGGER;
	
	static {
		MOD_INFO = FabricLoader.getInstance().getModContainer("vrcraft").get().getMetadata();
		MOD_VERSION = VrCraft.MOD_INFO.getName() + " " + VrCraft.MOD_INFO.getVersion().toString();
		LOGGER = new ModLogger("VRCraft");
	}
	
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing VRCraft...");
	}
}
