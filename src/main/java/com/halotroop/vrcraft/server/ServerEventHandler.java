package com.halotroop.vrcraft.server;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.entity.ai.goal.VRCreeperIgniteGoal;
import com.halotroop.vrcraft.common.entity.ai.goal.VREndermanChasePlayerGoal;
import com.halotroop.vrcraft.common.entity.ai.goal.VREndermanTeleportTowardsPlayerGoal;
import com.halotroop.vrcraft.common.network.packet.UberPacket;
import com.halotroop.vrcraft.common.util.MathUtil;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.Util;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

// I experimented with adding events that function exactly like the Forge equivalent,
// but decided it's not worth the trouble. They're confusing and no one will use them anyway.
public class ServerEventHandler {
	public static final ServerConfig config = VrCraftServer.config;
	
	public static void init() {
		// onServerTick
		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			PlayerManager playerManager = world.getServer().getPlayerManager();
			PlayerTracker.tick(playerManager);
			int viewDist = playerManager.getViewDistance();
			float range = MathHelper.clamp(viewDist / 8.0f, 1.0f, 2.5f) * 64.0f;
			for (Map.Entry<UUID, VRPlayerData> entry : PlayerTracker.players.entrySet()) {
				ServerPlayerEntity player = playerManager.getPlayer(entry.getKey());
				if (player != null) {
					UberPacket packet = PlayerTracker.getPlayerDataPacket(entry.getKey(), entry.getValue());
					ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, packet);
				}
			}
		});
		
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			LOGGER.devInfo("Entity load event running for " + entity.getEntityName());
			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) entity;
				if (config.vrOnly && !player.hasPermissionLevel(2)) { // VR-only not OP
					Util.scheduler.schedule(() -> {
						world.getServer().submit(() -> {
							if (player.networkHandler.getConnection().isOpen()
									&& !PlayerTracker.hasPlayerData(player)) {
								player.sendMessage(new LiteralText(config.vrOnlyKickMessage), false);
								player.sendMessage(new LiteralText("If this is not a VR client, " +
										"you will be kicked in " + config.vrOnlyKickDelay
										+ " seconds"), false);
								Util.scheduler.schedule(() -> {
									world.getServer().submit(() -> {
										if (player.networkHandler.getConnection().isOpen()
												&& !PlayerTracker.hasPlayerData(player)) {
											player.networkHandler.getConnection().disconnect(
													new LiteralText(config.vrOnlyKickMessage));
										}
									});
								}, Math.round(config.vrOnlyKickDelay * 1000), TimeUnit.MILLISECONDS);
							}});
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
				Vec3d aim = MathUtil.multiplyQuat(new Vec3d(0, 0, -1), data.getController(data.activeHand).getRot());
				
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
				LOGGER.devInfo("Replacing creeper AI goal");
				Util.replaceAIGoal(creeper, creeper.goalSelector, VRCreeperIgniteGoal.class,
						() -> new VRCreeperIgniteGoal(creeper));
			} else if (entity instanceof EndermanEntity) {
				EndermanEntity enderman = (EndermanEntity) entity;
				LOGGER.devInfo("Replacing enderman chase AI goal");
				Util.replaceAIGoal(enderman, enderman.goalSelector, EndermanEntity.ChasePlayerGoal.class,
						() -> new VREndermanChasePlayerGoal(enderman));
				LOGGER.devInfo("Replacing enderman AI TP goal");
				Util.replaceAIGoal(enderman, enderman.targetSelector, EndermanEntity.TeleportTowardsPlayerGoal.class,
						() -> new VREndermanTeleportTowardsPlayerGoal(enderman, enderman::shouldAngerAt));
			}
		});
	}
}
