package com.halotroop.vrcraft.client.network.packet;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.network.packet.DeviceData;
import com.halotroop.vrcraft.common.network.packet.VRPacketHandler;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.List;
import java.util.UUID;

public class VRS2CPacketHandler extends VRPacketHandler {
	public static boolean serverWantsData = false;
	
	public final ClientPlayerEntity player;
	
	public VRS2CPacketHandler(ClientPlayerEntity player, PacketByteBuf buffer) {
		super(PlayerTracker.getPlayerData(player), buffer);
		this.player = player;
	}
	
	@Override
	public void activeHand() {
		if (serverWantsData) {
			sendToServer(new PacketByteBuf(Unpooled.buffer().writeInt(data.activeHand)));
		}
	}
	
	@Override
	public void bowDraw() {
	
	}
	
	@Override
	public void climbing(BlockListMode blockMode, List<String> list) {
	
	}
	
	@Override
	public void controller(Controller controller) {
	
	}
	
	@Override
	public void crawling() {
	
	}
	
	public void moveMode() {
	
	}
	
	public void uberPacket() {
		UUID uuid = new UUID(this.buffer.readLong(), this.buffer.readLong());
		DeviceData head = DeviceData.decode(this.buffer);
		DeviceData lCon = DeviceData.decode(this.buffer);
		DeviceData rCon = DeviceData.decode(this.buffer);
		float worldScale = this.buffer.readFloat();
		float height = this.buffer.readFloat();
		
	}
	
	@Override
	public void teleport() {
	
	}
	
	@Override
	public void version(String message) {
	
	}
	
	private void sendToServer(PacketByteBuf buffer) {
		ClientSidePacketRegistry.INSTANCE.sendToServer(VrCraft.VIVECRAFT_CHANNEL_ID, buffer);
	}
}
