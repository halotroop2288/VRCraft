package com.halotroop.vrcraft.server.mixin;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.ServerConfig;
import com.halotroop.vrcraft.server.VrCraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	private static final ServerConfig config = VrCraftServer.config;
	
	@Inject(method = "attack", at = @At("INVOKE"))
	protected void onReleaseBow(Entity target, CallbackInfo ci) {
		VrCraft.LOGGER.devInfo("A player released a bow.");
		PlayerEntity player = (PlayerEntity)(Object) this;
		if (target instanceof PlayerEntity) {
			VrCraft.LOGGER.devInfo(player + " attacked " + target);
			PlayerEntity tgt1 = (PlayerEntity) target;
			if (PlayerTracker.hasPlayerData(player)) {
				VRPlayerData vrPlayerData = PlayerTracker.getPlayerData(player);
				if (vrPlayerData.seated) { // Seated VR vs...
					if (PlayerTracker.hasPlayerData(tgt1) && !config.pvpSeatedVRvsSeatedVR)
						ci.cancel(); //Seated VR
					else if (!config.pvpVRvsSeatedVR) ci.cancel();// VR
				} else { // VR vs...
					if (!PlayerTracker.hasPlayerData(tgt1)) {
						if (vrPlayerData.seated && !config.pvpVRvsSeatedVR) ci.cancel();// Seated VR
						else if (!config.pvpVRvsVR) ci.cancel(); // VR
					} else if (!config.pvpVRvsNonVR) ci.cancel();// Non-VR
				}
			} else { // Non-VR vs...
				if (PlayerTracker.hasPlayerData(tgt1)) {
					VRPlayerData vrPlayerData = PlayerTracker.getPlayerData(player);
					if (vrPlayerData.seated) {//Seated
						if (!config.pvpSeatedVRvsNonVR) ci.cancel();
					} else { // ...VR
						if (!config.pvpVRvsNonVR) ci.cancel();
					}
				}
			}
		}
	}
}
