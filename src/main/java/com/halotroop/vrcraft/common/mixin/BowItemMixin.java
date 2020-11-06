package com.halotroop.vrcraft.common.mixin;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public abstract class BowItemMixin {
	@Shadow
	public abstract void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks);
	
	@Inject(method = "onStoppedUsing", at = @At("INVOKE"))
	protected void onArrowLoose(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
		VrCraft.LOGGER.devInfo(user.getName().asString() + " released bow.");
		int i = stack.getMaxUseTime() - remainingUseTicks;
		if (user instanceof PlayerEntity) {
			VRPlayerData data = PlayerTracker.getPlayerData((PlayerEntity) user);
			if (data != null && !data.seated) {
				ci.cancel(); // resend the new info
				VrCraft.LOGGER.devInfo("Bow draw: " + data.bowDraw);
				this.onStoppedUsing(stack, world, user, Math.round(i * 20)); // setCharge
			}
		}
	}
}
