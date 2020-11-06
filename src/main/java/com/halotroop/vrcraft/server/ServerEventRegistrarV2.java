package com.halotroop.vrcraft.server;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.entity.ai.goal.VRCreeperIgniteGoal;
import com.halotroop.vrcraft.common.entity.ai.goal.VREndermanChasePlayerGoal;
import com.halotroop.vrcraft.common.entity.ai.goal.VREndermanTeleportTowardsPlayerGoal;
import com.halotroop.vrcraft.common.network.packet.DeviceData;
import com.halotroop.vrcraft.common.network.packet.UberPacket;
import com.halotroop.vrcraft.common.network.packet.VRPacketHandlerV2;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.Util;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketHandlerV2;
import io.github.cottonmc.cotton.logging.ModLogger;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class ServerEventRegistrarV2 {
	public static final ServerConfig CONFIG = VrCraft.SERVER_CONFIG;
	public static final ModLogger LOGGER = VrCraft.LOGGER;
	
	static void init() {
		// onServerTick
		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			PlayerManager playerManager = world.getServer().getPlayerManager();
			PlayerTracker.tick(playerManager);
			int viewDist = playerManager.getViewDistance();
			float range = MathHelper.clamp(viewDist / 8.0f, 1.0f, 2.5f) * 64.0f;
			for (Map.Entry<UUID, VRPlayerData> entry : PlayerTracker.players.entrySet()) {
				ServerPlayerEntity player = playerManager.getPlayer(entry.getKey());
				if (player != null) {
					PacketByteBuf packet = PlayerTracker.getUberPacketBytes(entry.getKey(), entry.getValue());
					ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, VRPacketHandlerV2.VIVECRAFT_CHANNEL_ID, packet);
				}
			}
		});
		
		// onPlayerJoinServer
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) entity;
				if (CONFIG.vrOnly && !player.hasPermissionLevel(2)) { // VR-only not OP
					Util.scheduler.schedule(() -> {
						world.getServer().submit(() -> {
							if (player.networkHandler.getConnection().isOpen()
									&& !PlayerTracker.hasPlayerData(player)) {
								player.sendMessage(new LiteralText(CONFIG.vrOnlyKickMessage), false);
								player.sendMessage(new LiteralText("If this is not a VR client, " +
										"you will be kicked in " + CONFIG.vrOnlyKickDelay
										+ " second(s)"), false);
								Util.scheduler.schedule(() -> {
									world.getServer().submit(() -> {
										if (player.networkHandler.getConnection().isOpen()
												&& !PlayerTracker.hasPlayerData(player)) {
											player.networkHandler.getConnection().disconnect(
													new LiteralText(CONFIG.vrOnlyKickMessage));
										}
									});
								}, Math.round(CONFIG.vrOnlyKickDelay * 1000), TimeUnit.MILLISECONDS);
							}
						});
					}, 1000, TimeUnit.MILLISECONDS);
				}
			} else if (entity instanceof ProjectileEntity) {
				ProjectileEntity projectile = (ProjectileEntity) entity;
				if (!(projectile.getOwner() instanceof PlayerEntity)) return;
				PlayerEntity shooter = (PlayerEntity) projectile.getOwner();
				if (!PlayerTracker.hasPlayerData(shooter)) return;
				boolean arrow = projectile instanceof PersistentProjectileEntity
						&& !(projectile instanceof TridentEntity);
				VRPlayerData data = PlayerTracker.getAbsolutePlayerData(shooter);
				assert data != null;
				Vec3d pos = data.getController(data.activeHand).getPos();
				Vec3d aim = Util.multiplyQuat(data.getController(data.activeHand).getRotation(), new Vec3d(0, 0, -1));
				
				if (arrow && !data.seated && data.bowDraw > 0) {
					pos = data.getController(0).getPos();
					aim = data.getController(1).getPos().subtract(pos).normalize();
				}
				
				pos = pos.add(aim.multiply(0.6));
				double vel = projectile.getVelocity().length();
				projectile.setPos(pos.x, pos.y, pos.z);
				projectile.setVelocity(aim.x, aim.y, aim.z, (float) vel, 0.0f); // unsure about this "shoot"
				
				Vec3d shooterMotion = shooter.getVelocity();
				projectile.setVelocity(projectile.getVelocity()
						.add(shooterMotion.x, shooter.isOnGround() ? 0.0 : shooterMotion.y, shooterMotion.z));
				
				VrCraft.LOGGER.devInfo("Projectile direction: " + aim);
				VrCraft.LOGGER.devInfo("Projectile velocity: " + vel);
			} else if (entity instanceof CreeperEntity) {
				CreeperEntity creeper = (CreeperEntity) entity;
				Util.replaceAIGoal(creeper, creeper.goalSelector, CreeperIgniteGoal.class,
						() -> new VRCreeperIgniteGoal(creeper));
			} else if (entity instanceof EndermanEntity) {
				EndermanEntity enderman = (EndermanEntity) entity;
				Util.replaceAIGoal(enderman, enderman.goalSelector, EndermanEntity.ChasePlayerGoal.class,
						() -> new VREndermanChasePlayerGoal(enderman));
				Util.replaceAIGoal(enderman, enderman.targetSelector, EndermanEntity.TeleportTowardsPlayerGoal.class,
						() -> new VREndermanTeleportTowardsPlayerGoal(enderman, enderman::shouldAngerAt));
			}
		});
		
		ServerSidePacketRegistry.INSTANCE.register(VRPacketHandlerV2.VIVECRAFT_CHANNEL_ID, (context, data) -> {
			if (!data.isReadable() || context.getPlayer() == null) return;
			else LOGGER.devInfo("Received a valid Vivecraft packet!");
			
			Packet disc = Packet.values()[data.readByte()];
			ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
			VRC2SPacketHandlerV2 handler = new VRC2SPacketHandlerV2(player, data);
			
			if (disc.action != null) context.getTaskQueue().execute(() -> disc.action.accept(handler));
			else throw new IllegalStateException("Unhandled C2S packet!");
		});
	}
	
	public enum Packet {
		VERSION(a -> a.version(VrCraft.MOD_VERSION)),
		DATA_REQUEST((a) -> backwardsError()), // S2C Packet
		HEAD_DATA(a -> a.hmd(DeviceData.decode(a.buffer))), // HMD Headset
		CONTROLLER_L_DATA(a -> a.controller(VRPacketHandlerV2.Controller.LEFT)), // Controller 0
		CONTROLLER_R_DATA(a -> a.controller(VRPacketHandlerV2.Controller.RIGHT)), // Controller 1
		WORLD_SCALE(a -> a.worldScale(a.buffer.readFloat())), // World Scale
		BOW_DRAW(VRC2SPacketHandlerV2::bowDraw), // Bow draw
		MOVE_MODE(a -> backwardsError()), // S2C Packet
		UBER_PACKET(a -> UberPacket.decode(a.buffer)), // L+R Controllers, HMD, world scale, and height
		TELEPORT(VRC2SPacketHandlerV2::teleport), // TP destination
		CLIMBING(a -> a.climbing(CONFIG.blockMode, CONFIG.blockList)), // Don't kick player for floating while climbing
		SETTING_OVERRIDE(a -> backwardsError()), // S2C Packet
		HEIGHT(VRC2SPacketHandlerV2::height),
		ACTIVE_HAND(VRC2SPacketHandlerV2::activeHand),
		CRAWLING(VRC2SPacketHandlerV2::crawling);
		
		private final Consumer<VRC2SPacketHandlerV2> action;
		
		Packet(@Nullable Consumer<VRC2SPacketHandlerV2> action) {
			this.action = action;
		}
		
		public static void backwardsError() {
			LOGGER.warn("A packet traveled in the wrong direction!");
		}
	}
}
