package com.halotroop.vrcraft.client.network.packet;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.network.packet.VRPacketHandler;
import com.halotroop.vrcraft.server.ServerEventRegistrar;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.function.Consumer;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;
import static com.halotroop.vrcraft.common.VrCraft.VIVECRAFT_CHANNEL_ID;

public class ClientVRPacketRegistrar {
	private static boolean init;
	
	public static void init() {
		if (init) return;
		
		ClientSidePacketRegistry.INSTANCE.register(VIVECRAFT_CHANNEL_ID, (context, data) -> {
			// valid data check
			if (!data.isReadable()) return;
			final byte discByte = data.readByte();
			if (!(discByte <= Packet.values().length) // packet can be handled
					|| (context.getPlayer() == null && (discByte != Packet.VERSION.ordinal()))) { // packet is null
				return;
			} // end valid data check
			
			LOGGER.devInfo("Received a valid Vivecraft packet!");
			Packet packet = Packet.values()[discByte];
			ClientPlayerEntity player = (ClientPlayerEntity) context.getPlayer();
			VRS2CPacketHandler handler = new VRS2CPacketHandler(player, data);
			
			if (!data.isReadable() && packet != Packet.VERSION) {
				LOGGER.error("Packet was improperly handled! Data was null!");
			}
			if (packet.action != null) context.getTaskQueue().execute(() -> packet.action.accept(handler));
			else throw new IllegalStateException("Unhandled S2C packet!"); // This should be impossible.
			if (data.isReadable()) {
				LOGGER.error("Packet was improperly handled! Data was left-over!");
			}
		});
		
		init = true;
	}
	
	private enum Packet {
		VERSION(a -> a.version(VrCraft.MOD_VERSION)),
		REQUEST_DATA(a -> VRS2CPacketHandler.serverWantsData = true),
		HEAD_DATA(VRS2CPacketHandler::hmd),
		CONTROLLER_L_DATA(a -> a.controller(VRPacketHandler.Controller.RIGHT)),
		CONTROLLER_R_DATA(a -> a.controller(VRPacketHandler.Controller.LEFT)),
		WORLD_SCALE(VRPacketHandler::worldScale),
		DRAW(VRS2CPacketHandler::bowDraw),
		MOVE_MODE(VRS2CPacketHandler::moveMode),
		UBERPACKET(VRS2CPacketHandler::uberPacket),
		TELEPORT(a -> {
		}),
		CLIMBING(a -> {
		}),
		SETTING_OVERRIDE(a -> {
		}),
		HEIGHT(a -> {
		}),
		ACTIVE_HAND(a -> {
		}),
		CRAWL(a -> {
		});
		
		private final Consumer<VRS2CPacketHandler> action;
		
		Packet(Consumer<VRS2CPacketHandler> action) {
			this.action = action;
		}
		
		public static void backwardsError() {
			ServerEventRegistrar.LOGGER.warn("A client to server packet traveled in the wrong direction!");
		}
	}
}
