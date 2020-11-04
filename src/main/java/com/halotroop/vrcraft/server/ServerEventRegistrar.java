package com.halotroop.vrcraft.server;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.entity.ai.goal.VRCreeperIgniteGoal;
import com.halotroop.vrcraft.common.entity.ai.goal.VREndermanChasePlayerGoal;
import com.halotroop.vrcraft.common.entity.ai.goal.VREndermanTeleportTowardsPlayerGoal;
import com.halotroop.vrcraft.common.network.packet.*;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.UberPacket;
import com.halotroop.vrcraft.common.util.Util;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.network.packet.VRC2SPacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.halotroop.vrcraft.common.VrCraft.LOGGER;

// I experimented with adding events that function exactly like the Forge equivalent,
// but decided it's not worth the trouble. They're confusing and no one will use them anyway.
@Environment(EnvType.SERVER)
public class ServerEventRegistrar {
	public static final ServerConfig config = VrCraftServer.config;
	
	public static Map<UUID, VRPlayerData> vrPlayers = new HashMap<>();
	
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
		
		// TODO: Diverge from Vivecraft and send these all as *different* packets instead of using a discriminator byte
		//  This is one big confusing mess.
		ServerSidePacketRegistry.INSTANCE.register(Util.vcID("data"), (context, unfixedData) -> {
			LOGGER.devInfo("Received a Vivecraft packet!");
			LOGGER.devInfo("Packet has data: " + unfixedData.hasArray());
			if (!unfixedData.hasArray() || unfixedData.array().length == 0) return;
			LOGGER.devInfo("Packet data: " + Arrays.toString(unfixedData.array()));
			
			VRPlayerData pd = vrPlayers.get(context.getPlayer().getUuid());
			VRC2SPacketListener listener = new VRC2SPacketListener(pd);
			
			PacketDiscriminators disc = PacketDiscriminators.values()[unfixedData.array()[0]];
			
			LOGGER.devInfo("It's a " + disc.name() + " packet.");
			LOGGER.devInfo("If all goes well, another line should be printed about this packet now...");
			
			// (pd == null && disc != PacketDiscriminators.VERSION) is impossible
			
			PacketByteBuf buf = new PacketByteBuf(unfixedData.copy(1, unfixedData.array().length)); // Remove the discriminator byte
			context.getTaskQueue().execute(() -> {
				LOGGER.devInfo("what thread is this?");
				switch (disc) {
					case CONTROLLERLDATA:
						new ControllerData().applyServer(listener);
						break;
					case CONTROLLERRDATA:
						new ControllerData().right().applyServer(listener);
						break;
					case HEADDATA:
						new HeadData().applyServer(listener);
						break;
					case UBERPACKET:
						new UberPacket().applyServer(listener);
						break;
					case BOWDRAW:
						new BowDrawPacket().applyServer(listener);
						break;
					case CRAWL:
						new CrawlingPacket(buf.readBoolean()).applyServer(listener);
						break;
					case HEIGHT:
						new HeightPacket().applyServer(listener);
						break;
					case CLIMBING:
						new ClimbingPacket().applyServer(listener);
						break;
					case TELEPORT:
						new TeleportPacket().applyServer(listener);
						break;
					case ACTIVEHAND:
						new ActiveHandPacket().applyServer(listener);
						break;
					case WORLDSCALE:
						new WorldScalePacket().applyServer(listener);
						break;
					case VERSION:
						new VersionPacket().applyServer(listener);
						break;
					case SETTING_OVERRIDE: // S2C
					case MOVEMODE: // S2C
					case REQUESTDATA: // S2C
						LOGGER.warn("S2C packet was sent backwards!");
						break;
					default: // Unhandled
						LOGGER.warn("Unhandled packet was received on server");
				}
			});
		});
	}
	
	public enum PacketDiscriminators {
		VERSION, // VERSION_OH_AND_HEY_WHAT_DO_YOU_SUPPORT - Techjar
		REQUESTDATA, // S2C Packet
		HEADDATA, // HMD Headset
		CONTROLLERLDATA, // Controller 0
		CONTROLLERRDATA, // Controller 1
		WORLDSCALE, // World Scale
		BOWDRAW, // Bow draw
		MOVEMODE, // S2C Packet
		UBERPACKET, // L+R Controllers, HMD, world scale, and height
		TELEPORT, // TP destination
		CLIMBING, // Don't kick player for floating while climbing
		SETTING_OVERRIDE, // S2C Packet
		HEIGHT,
		ACTIVEHAND,
		CRAWL
	}
}
