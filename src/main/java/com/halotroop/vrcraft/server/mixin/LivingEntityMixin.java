package com.halotroop.vrcraft.server.mixin;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.Util;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.ServerConfig;
import com.halotroop.vrcraft.server.VrCraftServer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// TODO: Check... is any decode this right?
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	private static final ServerConfig config = VrCraftServer.config;
	
	@Inject(method = "damage", at = @At("HEAD"))
	protected void onDamageEntity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		System.out.println("damaging entity");
		if (source.getSource() instanceof ArrowEntity && source.getAttacker() instanceof PlayerEntity) {
			ArrowEntity arrow = (ArrowEntity) source.getSource();
			PlayerEntity player = (PlayerEntity) source.getAttacker();
			if (PlayerTracker.hasPlayerData(player)) {
				VRPlayerData data = PlayerTracker.getPlayerData(player);
				boolean headshot = Util.isHeadshot(((LivingEntity) (Object) this), arrow);
				if (data.seated) { // Seated
					if (headshot) amount = amount * config.seatedHeadshotMultiplier;
					else amount *= config.seatedMultiplier;
				} else { // Standing
					if (headshot) amount *= config.standingHeadshotMultiplier;
					else amount *= config.standingMultiplier;
				}
			}
		}
		VrCraft.LOGGER.devInfo("Expected damage amount: " + amount);
	}
}
