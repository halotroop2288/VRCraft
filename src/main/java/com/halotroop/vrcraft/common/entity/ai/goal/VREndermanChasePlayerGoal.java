package com.halotroop.vrcraft.common.entity.ai.goal;

import com.halotroop.vrcraft.common.VrCraft;
import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.Util;
import com.halotroop.vrcraft.common.util.VRPlayerData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author Techjar, halotroop2288
 */
public class VREndermanChasePlayerGoal extends EndermanEntity.ChasePlayerGoal {
	public VREndermanChasePlayerGoal(EndermanEntity enderman) {
		super(enderman);
	}
	
	@Override
	public boolean canStart() {
		boolean orig = super.canStart(); // call this always so stuff gets set up
		LivingEntity target = this.enderman.getTarget();
		
		if (target instanceof PlayerEntity && PlayerTracker.hasPlayerData((PlayerEntity)target)) {
			double dist = target.distanceTo(this.enderman);
			return dist <= 256.0D && Util.shouldEndermanAttackVRPlayer(this.enderman, (PlayerEntity)target);
		}
		
		return orig;
	}
	
	@Override
	public void tick() {
		LivingEntity target = this.enderman.getTarget();
		if (target instanceof PlayerEntity && PlayerTracker.hasPlayerData((PlayerEntity) target)) {
			VRPlayerData data = PlayerTracker.getAbsolutePlayerData((PlayerEntity) target);
			if (data != null) this.enderman.getLookControl().lookAt(data.head.posX, data.head.posY, data.head.posZ);
			else {
				VrCraft.LOGGER.error("Failed data check in " + this.getClass().getSimpleName());
				super.tick();
			}
		} else super.tick();
	}
}
