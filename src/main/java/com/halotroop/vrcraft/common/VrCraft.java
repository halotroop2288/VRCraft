package com.halotroop.vrcraft.common;

import com.halotroop.vrcraft.common.util.Util;
import com.halotroop.vrcraft.server.ServerConfig;
import io.github.cottonmc.cotton.config.ConfigManager;
import io.github.cottonmc.cotton.logging.ModLogger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Identifier;

public class VrCraft implements ModInitializer {
	public static final ModMetadata MOD_INFO;
	public static final String MOD_VERSION;
	public static final ModLogger LOGGER;
	public static final ServerConfig SERVER_CONFIG = ConfigManager.loadConfig(ServerConfig.class);
	public static final Identifier VIVECRAFT_CHANNEL_ID = Util.vcID("data");
	
	static {
		assert FabricLoader.getInstance().getModContainer("vrcraft").isPresent();
		MOD_INFO = FabricLoader.getInstance().getModContainer("vrcraft").get().getMetadata();
		MOD_VERSION = VrCraft.MOD_INFO.getName() + " " + VrCraft.MOD_INFO.getVersion().toString();
		LOGGER = new ModLogger("VRCraft");
	}
	
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing VRCraft...");
	}
}
