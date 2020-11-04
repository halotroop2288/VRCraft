package com.halotroop.vrcraft.server.mixin;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.Util;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.ServerConfig;
import com.halotroop.vrcraft.server.VrCraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	private static final ServerConfig config = VrCraftServer.config;
	
	@Inject(method = "attack", at = @At("INVOKE"))
	protected void onAttackPlayer(Entity target, CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		if (target instanceof PlayerEntity) {
			PlayerEntity victim = (PlayerEntity) target;
			if (PlayerTracker.hasPlayerData(player)) {
				VrCraft.LOGGER.devInfo("VR player " + player.getDisplayName() + " attacked player " + target.getDisplayName());
				VRPlayerData vrPlayerData = PlayerTracker.getPlayerData(player);
				if (vrPlayerData.seated) { // Seated VR vs...
					if (PlayerTracker.hasPlayerData(victim) && !config.pvpSeatedVRvsSeatedVR)
						ci.cancel(); // Seated VR
					else if (!config.pvpVRvsSeatedVR) ci.cancel(); // VR
				} else { // VR vs...
					if (!PlayerTracker.hasPlayerData(victim)) {
						if (vrPlayerData.seated && !config.pvpVRvsSeatedVR) ci.cancel(); // Seated VR
						else if (!config.pvpVRvsVR) ci.cancel(); // VR
					} else if (!config.pvpVRvsNonVR) ci.cancel(); // Non-VR
				}
			} else { // Non-VR vs...
				if (PlayerTracker.hasPlayerData(victim)) {
					VRPlayerData vrPlayerData = PlayerTracker.getPlayerData(player);
					if (vrPlayerData.seated) { // Seated
						if (!config.pvpSeatedVRvsNonVR) ci.cancel();
					} else { // ...VR
						if (!config.pvpVRvsNonVR) ci.cancel();
					}
				}
			}
		}
	}
	
	@Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("RETURN"))
	protected void onPlayerTossEvent(ItemStack stack, boolean throwRandomly, boolean retainOwnership,
	                                 CallbackInfoReturnable<@Nullable ItemEntity> cir) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		if (!PlayerTracker.hasPlayerData(player))
			return;
		
		VRPlayerData data = PlayerTracker.getAbsolutePlayerData(player);
		ItemEntity item = cir.getReturnValue();
		
		if (item != null && data != null) {
			Vec3d pos = data.getController(0).getPos();
			Vec3d aim = Util.multiplyQuat(data.getController(0).getRotation(), new Vec3d(0, 0, -1));
			Vec3d aimUp = Util.multiplyQuat(data.getController(0).getRotation(), new Vec3d(0, 1, 0));
			double pitch = Math.toDegrees(Math.asin(-aim.y));
			
			pos = pos.add(aim.multiply(0.2)).subtract(aimUp.multiply(0.4 * (1 - Math.abs(pitch) / 90)));
			double vel = 0.3;
			item.setPos(pos.x, pos.y, pos.z);
			item.setVelocity(aim.multiply(vel));
			VrCraft.LOGGER.devInfo("Adjusted item position");
		}
	}
	
	@Inject(method = "tick", at = @At("TAIL"))
	protected void onPlayerTick(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity)(Object)this;
		VRPlayerData data = PlayerTracker.getPlayerData(player);
		if (data != null && data.crawling) {
			VrCraft.LOGGER.devInfo("VR Player is crawling.");
			player.setPose(EntityPose.SWIMMING);
		}
	}
}
