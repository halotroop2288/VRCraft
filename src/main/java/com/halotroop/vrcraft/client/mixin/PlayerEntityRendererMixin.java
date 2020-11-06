package com.halotroop.vrcraft.client.mixin;

import com.halotroop.vrcraft.client.render.entity.feature.PatreonFeatureRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
	@Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Z)V", at = @At("TAIL"))
	private void onPlayerEntityRendererInit(EntityRenderDispatcher dispatcher, boolean bl, CallbackInfo ci) {
		((PlayerEntityRenderer) (Object) this).features.add(new PatreonFeatureRenderer((PlayerEntityRenderer) (Object) this));
	}
}
