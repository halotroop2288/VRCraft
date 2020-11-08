package com.halotroop.vrcraft.client.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
	@Inject(method = "applyDamage", at = @At("HEAD"))
	private void onApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
		if (amount > 0 && !((ClientPlayerEntity) (Object) this).abilities.creativeMode) {
			int dur = 1000;
			if (source.isExplosive()) dur = 2000;
			if (source == DamageSource.CACTUS) dur = 200;
			// Trigger haptics
			// TODO
		}
	}
}
