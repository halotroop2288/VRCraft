package com.halotroop.vrcraft.common.mixin;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.Util;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import com.halotroop.vrcraft.server.ServerConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// TODO: Check... is any of this right?
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	public abstract boolean damage(DamageSource source, float amount);
	
	private static final ServerConfig config = VrCraft.SERVER_CONFIG;
	
	@Inject(method = "damage", at = @At("HEAD"))
	protected void onDamageEntity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		float newAmount = amount;
		if (source.getSource() instanceof ArrowEntity && source.getAttacker() instanceof PlayerEntity) {
			ArrowEntity arrow = (ArrowEntity) source.getSource();
			PlayerEntity player = (PlayerEntity) source.getAttacker();
			if (PlayerTracker.hasPlayerData(player)) {
				VRPlayerData data = PlayerTracker.getPlayerData(player);
				assert data != null; // unsafe but if the hasPlayerData check works, it should be OK.
				boolean headshot = Util.isHeadshot(((LivingEntity) (Object) this), arrow);
				if (data.seated) { // Seated
					if (headshot) newAmount = amount * config.seatedHeadshotMultiplier;
					else newAmount *= config.seatedMultiplier;
				} else { // Standing
					if (headshot) newAmount *= config.standingHeadshotMultiplier;
					else newAmount *= config.standingMultiplier;
				}
			}
		}
		if (newAmount != amount) {
			VrCraft.LOGGER.devInfo("Expected damage amount: " + amount);
			cir.setReturnValue(this.damage(source, newAmount));
			cir.cancel();
		}
	}
}
