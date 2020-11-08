package com.halotroop.vrcraft.client.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.mojang.blaze3d.platform.GlStateManager.*;

@Mixin(GlStateManager.class)
public class GLStateManagerMixin {
	/**
	 * VIVECRAFT: Correct bad blend function that trashes alpha channel
	 *
	 * @author halotroop2288, Vivecraft team
	 * @see com.mojang.blaze3d.platform.GlStateManager
	 */
	@Overwrite
	public static void blendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (srcFactorRGB == SrcFactor.SRC_ALPHA.field_22545
				&& dstFactorRGB == DstFactor.ONE_MINUS_SRC_ALPHA.field_22528
				&& srcFactorAlpha == SrcFactor.ONE.field_22545
				&& dstFactorAlpha == DstFactor.ZERO.field_22528) {
			dstFactorAlpha = DstFactor.ONE_MINUS_SRC_ALPHA.field_22528;
		}
		
		if (srcFactorRGB != BLEND.srcFactorRGB || dstFactorRGB != BLEND.dstFactorRGB ||
				srcFactorAlpha != BLEND.srcFactorAlpha || dstFactorAlpha != BLEND.dstFactorAlpha) {
			BLEND.srcFactorRGB = srcFactorRGB;
			BLEND.dstFactorRGB = dstFactorRGB;
			BLEND.srcFactorAlpha = srcFactorAlpha;
			BLEND.dstFactorAlpha = dstFactorAlpha;
			blendFuncSeparateUntracked(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
		}
	}
}
