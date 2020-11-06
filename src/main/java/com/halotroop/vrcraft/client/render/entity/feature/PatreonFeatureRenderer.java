package com.halotroop.vrcraft.client.render.entity.feature;

import com.halotroop.vrcraft.client.util.external.PatreonImpl;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class PatreonFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity,
		PlayerEntityModel<AbstractClientPlayerEntity>> {
	
	public PatreonFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity,
			PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
	}
	
	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
	                   AbstractClientPlayerEntity playerEntity, float limbAngle, float limbDistance, float tickDelta,
	                   float animationProgress, float headYaw, float headPitch) {
		if (PatreonImpl.shouldRenderHMD(playerEntity)) {
		
		}
		
		if (PatreonImpl.shouldRenderHalo(playerEntity)) {
		
		}
	}
}
