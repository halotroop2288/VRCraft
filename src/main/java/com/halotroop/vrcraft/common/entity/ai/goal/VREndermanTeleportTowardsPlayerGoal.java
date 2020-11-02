package com.halotroop.vrcraft.common.entity.ai.goal;

import com.halotroop.vrcraft.common.util.PlayerTracker;
import com.halotroop.vrcraft.common.util.Util;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * @author Techjar, halotroop2288
 */
public class VREndermanTeleportTowardsPlayerGoal extends EndermanEntity.TeleportTowardsPlayerGoal {
	public VREndermanTeleportTowardsPlayerGoal(EndermanEntity enderman, @Nullable Predicate<LivingEntity> predicate) {
		super(enderman, predicate);
		this.targetPredicate = (new TargetPredicate()).setBaseMaxDistance(getFollowRange()).setPredicate((p) -> {
			if (PlayerTracker.hasPlayerData((PlayerEntity)p))
				return Util.shouldEndermanAttackVRPlayer(enderman, (PlayerEntity)p);
			else return enderman.shouldAngerAt(p);
		});
	}
	
	@Override
	public boolean canStart() {
		this.targetPlayer = this.enderman.world.getClosestPlayer(this.targetPredicate, this.enderman);
		return this.targetPlayer != null;
	}
	
	@Override
	public boolean shouldContinue() {
		if (this.targetPlayer == null || !PlayerTracker.hasPlayerData(targetPlayer)) {
			return super.shouldContinue();
		} else {
			if (!Util.shouldEndermanAttackVRPlayer(this.enderman, this.targetPlayer)) {
				return false;
			} else {
				this.enderman.lookAtEntity(this.targetPlayer, 10.0F, 10.0F);
				return true;
			}
		}
	}
	
	@Override
	public void tick() {
		if (this.target == null || !PlayerTracker.hasPlayerData((PlayerEntity) this.target)) {
			super.tick();
			return;
		}
		
		if (this.enderman.getTarget() == null) {
			super.setTargetEntity(null);
		}
		
		if (this.targetPlayer != null) {
			if (--this.lookAtPlayerWarmup <= 0) {
				this.target = this.targetPlayer;
				this.targetPlayer = null;
				super.start();
			}
		} else {
			if (this.target != null && !this.enderman.hasVehicle()) {
				if (Util.shouldEndermanAttackVRPlayer(this.enderman, (PlayerEntity) this.target)) {
					if (this.target.distanceTo(this.enderman) < 16.0D) {
						this.enderman.teleportRandomly();
					}
					
					this.ticksSinceUnseenTeleport = 0;
				} else if (this.target.distanceTo(this.enderman) > 256.0D && this.ticksSinceUnseenTeleport++ >= 30
						&& this.enderman.teleportTo(this.target)) {
					this.ticksSinceUnseenTeleport = 0;
				}
			}
			
			// don't work but doesn't matter
			//super.tick();
		}
	}
}
