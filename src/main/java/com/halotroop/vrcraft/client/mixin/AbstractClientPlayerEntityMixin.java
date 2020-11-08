package com.halotroop.vrcraft.client.mixin;

import com.halotroop.vrcraft.client.util.external.PatreonImpl;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void onClientPlayerInit(ClientWorld world, GameProfile profile, CallbackInfo ci) {
		PatreonImpl.addPlayerInfo((PlayerEntity) (Object) this);
	}
}
