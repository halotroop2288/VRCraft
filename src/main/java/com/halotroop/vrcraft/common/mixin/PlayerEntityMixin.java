package com.halotroop.vrcraft.common.mixin;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.Util;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.ServerConfig;
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
	private static final ServerConfig SERVER_CONFIG = VrCraft.SERVER_CONFIG;
	
	@Inject(method = "attack", at = @At("INVOKE"))
	protected void onAttackPlayer(Entity target, CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		if (target instanceof PlayerEntity) {
			PlayerEntity victim = (PlayerEntity) target;
			if (PlayerTracker.hasPlayerData(player)) {
				VrCraft.LOGGER.devInfo("VR player " + player.getName() + " attacked player " + target.getName());
				VRPlayerData vrPlayerData = PlayerTracker.getAbsolutePlayerData(player);
				if (vrPlayerData != null && vrPlayerData.seated) { // Seated VR vs...
					if (PlayerTracker.hasPlayerData(victim) && !SERVER_CONFIG.pvpSeatedVRvsSeatedVR)
						ci.cancel(); // Seated VR
					else if (!SERVER_CONFIG.pvpVRvsSeatedVR) ci.cancel(); // VR
				} else { // VR vs...
					if (!PlayerTracker.hasPlayerData(victim)) {
						if (vrPlayerData != null && vrPlayerData.seated && !SERVER_CONFIG.pvpVRvsSeatedVR)
							ci.cancel(); // Seated VR
						else if (!SERVER_CONFIG.pvpVRvsVR) ci.cancel(); // VR
					} else if (!SERVER_CONFIG.pvpVRvsNonVR) ci.cancel(); // Non-VR
				}
			} else { // Non-VR vs...
				if (PlayerTracker.hasPlayerData(victim)) {
					VRPlayerData victimPlayerData = PlayerTracker.getPlayerData(victim);
					if (victimPlayerData != null && victimPlayerData.seated) { // Seated
						if (!SERVER_CONFIG.pvpSeatedVRvsNonVR) ci.cancel();
					} else { // ...VR
						if (!SERVER_CONFIG.pvpVRvsNonVR) ci.cancel();
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
