package com.halotroop.vrcraft.client.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
	/**
	 * VIVECRAFT: DstFactor of ONE_MINUS_SRC_ALPHA is really what we want here, because math
	 *
	 * @author halotroop2288
	 */
	@Overwrite
	public static void defaultBlendFunc() {
		RenderSystem.blendFuncSeparate(
				GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
		);
	}
}
