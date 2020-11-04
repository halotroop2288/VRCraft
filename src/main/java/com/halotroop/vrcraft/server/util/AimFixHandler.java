package com.halotroop.vrcraft.server.util;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.Util;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * @author Techjar, halotroop2288
 */
public class AimFixHandler extends ChannelInboundHandlerAdapter {
	private final ClientConnection connection;
	
	public AimFixHandler(ClientConnection connection) {
		this.connection = connection;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ServerPlayerEntity player = ((ServerPlayNetworkHandler) connection.getPacketListener()).player;
		boolean isCapturedPacket = msg instanceof PlayerInteractItemC2SPacket
				|| msg instanceof PlayerInteractBlockC2SPacket || msg instanceof PlayerActionC2SPacket;
		
		if (!PlayerTracker.hasPlayerData(player) || !isCapturedPacket || player.getServer() == null) {
			// we don't need to handle this packet, just defer to the next handler in the pipeline
			ctx.fireChannelRead(msg);
			return;
		}
		
		VrCraft.LOGGER.devInfo("Captured message " + msg.getClass().getSimpleName());
		player.getServer().submit(() -> {
			// Save all the current orientation data
			Vec3d oldPos = player.getPos();
			Vec3d oldPrevPos = new Vec3d(player.prevX, player.prevY, player.prevZ);
			float oldPitch = player.pitch;
			float oldYaw = player.yaw;
			float oldYawHead = player.headYaw;
			float oldPrevPitch = player.prevPitch;
			float oldPrevYaw = player.prevYaw;
			float oldPrevYawHead = player.prevHeadYaw;
			float oldEyeHeight = player.standingEyeHeight;
			
			VRPlayerData data = null;
			if (PlayerTracker.hasPlayerData(player)) { // Check again in case of race condition
				data = PlayerTracker.getAbsolutePlayerData(player);
				assert data != null;
				Vec3d pos = data.getController(0).getPos();
				Vec3d aim = Util.multiplyQuat(data.getController(0).getRotation(), new Vec3d(0, 0, -1));
				
				// Inject our custom orientation data
				player.setPos(pos.x, pos.y, pos.z);
				player.prevX = pos.x;
				player.prevY = pos.y;
				player.prevZ = pos.z;
				player.pitch = (float) Math.toDegrees(Math.asin(-aim.y));
				player.yaw = (float) Math.toDegrees(Math.atan2(-aim.x, aim.z));
				player.prevPitch = player.pitch;
				player.prevYaw = player.prevHeadYaw = player.headYaw = player.yaw;
				player.standingEyeHeight = 0;
				
				// Set up offset to fix relative positions
				data = PlayerTracker.getPlayerData(player);
				data.offset = oldPos.subtract(pos);
			}
			
			// Call the packet handler directly
			// This is several implementation details that we have to replicate
			try {
				if (connection.isOpen()) {
					try {
						((Packet<PacketListener>) msg).apply(connection.getPacketListener());
					} catch (OffThreadException e) { // Apparently might get thrown and can be ignored
					}
				}
			} finally {
				// Vanilla uses SimpleInboundChannelHandler, which automatically releases
				// by default, so we're expected to release the packet once we're done.
				ReferenceCountUtil.release(msg);
			}
			
			// Restore the original orientation data
			player.setPos(oldPos.x, oldPos.y, oldPos.z);
			player.prevX = oldPrevPos.x;
			player.prevY = oldPrevPos.y;
			player.prevZ = oldPrevPos.z;
			player.pitch = oldPitch;
			player.yaw = oldYaw;
			player.headYaw = oldYawHead;
			player.prevPitch = oldPrevPitch;
			player.prevYaw = oldPrevYaw;
			player.prevHeadYaw = oldPrevYawHead;
			player.standingEyeHeight = oldEyeHeight;
			
			// Reset offset
			if (data != null)
				data.offset = new Vec3d(0, 0, 0);
		});
	}
}
