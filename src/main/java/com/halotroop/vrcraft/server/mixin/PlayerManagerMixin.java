package com.halotroop.vrcraft.server.mixin;

import com.halotroop.vrcraft.server.util.AimFixHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
@Environment(EnvType.SERVER)
public abstract class PlayerManagerMixin {
	@Inject(method = "onPlayerConnect", at = @At("TAIL"))
	protected void onPlayerLoggedInEvent(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		connection.channel.pipeline().addBefore("packet_handler", "vr_aim_fix", new AimFixHandler(connection));
	}
}
