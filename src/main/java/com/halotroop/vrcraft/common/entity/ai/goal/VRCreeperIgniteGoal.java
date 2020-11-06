package com.halotroop.vrcraft.common.entity.ai.goal;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author Techjar, halotroop2288
 */
public class VRCreeperIgniteGoal extends CreeperIgniteGoal {
	public VRCreeperIgniteGoal(CreeperEntity creeper) {
		super(creeper);
	}
	
	@Override
	public boolean canStart() {
		LivingEntity target = this.creeper.getTarget();
		if (target instanceof PlayerEntity) {
			VRPlayerData data = PlayerTracker.getPlayerData((PlayerEntity)target);
			if (data != null && !data.seated)
				return this.creeper.getFuseSpeed() > 0 || this.creeper.squaredDistanceTo(target)
						< VrCraft.SERVER_CONFIG.creeperRadius * VrCraft.SERVER_CONFIG.creeperRadius;
		}
		return super.canStart();
	}
}
