package com.halotroop.vrcraft.client.network.packet;

import com.halotroop.vrcraft.common.network.packet.ClimbingPacket;
import com.halotroop.vrcraft.common.util.UberPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.text.Text;

// TODO the whole thing
@Environment(EnvType.CLIENT)
public class VRS2CPacketListener implements PacketListener {
	private PlayerEntity player;
	
	public VRS2CPacketListener(PlayerEntity player) {
		this.player = player;
	}
	
	public void onUberPacketSync(UberPacket uberPacket) {
	
	}
	
	public void applyControllerSync() {
	
	}
	
	public void applyClimbing(ClimbingPacket packet) {
	
	}
	
	public void applyTeleport(float x, float y, float z) {
	
	}
	
	public void applyActiveHand(int activeHand) {
	
	}
	
	public void applyWorldScale(float worldScale) {
	}
	
	public void applyBowDraw(float drawDist) {
	}
	
	public void applyCrawling() {
	}
	
	public void applyHeight(float height) {
	}
	
	@Override
	public void onDisconnected(Text reason) {}
	
	@Override
	public ClientConnection getConnection() { return null; }
}
