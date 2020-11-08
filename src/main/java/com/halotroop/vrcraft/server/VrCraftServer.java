package com.halotroop.vrcraft.server;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.network.packet.UberPacket;
import com.halotroop.vrcraft.common.network.packet.VRPacketHandler;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketHandler;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Consumer;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

// This part should mirror the functionality of Vivecraft Spigot/Forge Extensions
// For maximum Fabric compatibility with Vivecraft
// According to the CurseForge page, VFE is GPLv3, but Techjar doesn't really give a crap.
@Environment(EnvType.SERVER)
public class VrCraftServer implements DedicatedServerModInitializer {
	private static final ServerConfig CONFIG = VrCraft.SERVER_CONFIG;
	
	@Override
	public void onInitializeServer() {
		LOGGER.info("Initializing VRCraft Server...");
		
		ServerEventRegistrar.init();
		
		ServerSidePacketRegistry.INSTANCE.register(VrCraft.VIVECRAFT_CHANNEL_ID, (context, data) -> {
			if (!data.isReadable() || context.getPlayer() == null) return;
			else LOGGER.devInfo("Received a valid Vivecraft packet!");
			
			Packet packet = Packet.values()[data.readByte()];
			ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
			VRC2SPacketHandler handler = new VRC2SPacketHandler(player, data);
			
			LOGGER.devInfo("It's a " + packet.name() + " packet.");
			
			if (!data.isReadable()) throw new DecoderException("Invalid packet data.");
			if (packet.action != null) context.getTaskQueue().execute(() -> packet.action.accept(handler));
			else throw new IllegalStateException("Unhandled C2S packet!");
		});
	}
	
	public enum Packet {
		VERSION(a -> a.version(VrCraft.MOD_VERSION)),
		DATA_REQUEST((a) -> backwardsError()), // S2C Packet
		HEAD_DATA(VRC2SPacketHandler::hmd), // HMD Headset
		CONTROLLER_R_DATA(a -> a.controller(VRPacketHandler.Controller.RIGHT)), // Controller 0
		CONTROLLER_L_DATA(a -> a.controller(VRPacketHandler.Controller.LEFT)), // Controller 1
		WORLD_SCALE(VRPacketHandler::worldScale), // World Scale
		BOW_DRAW(VRC2SPacketHandler::bowDraw), // Bow draw
		MOVE_MODE(a -> backwardsError()), // S2C Packet
		UBER_PACKET(a -> UberPacket.decode(a.buffer)), // L+R Controllers, HMD, world scale, and height
		TELEPORT(VRC2SPacketHandler::teleport), // TP destination
		CLIMBING(a -> a.climbing(CONFIG.blockMode, CONFIG.blockList)), // Don't kick player for floating while climbing
		SETTING_OVERRIDE(a -> backwardsError()), // S2C Packet
		HEIGHT(VRC2SPacketHandler::height),
		ACTIVE_HAND(VRC2SPacketHandler::activeHand),
		CRAWLING(VRC2SPacketHandler::crawling);
		
		private final Consumer<VRC2SPacketHandler> action;
		
		Packet(@Nullable Consumer<VRC2SPacketHandler> action) {
			this.action = action;
		}
		
		public static void backwardsError() {
			LOGGER.warn("A server to client packet traveled in the wrong direction!");
		}
	}
}
