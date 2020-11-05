package com.halotroop.vrcraft.common.util;

import com.halotroop.vrcraft.common.VrCraft;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

/**
 * A general utility class
 *
 * @author Techjar, halotroop2288
 */
public final class Util {
	public static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdownNow));
	}
	
	private Util() {}
	
	/*
	 * This is mostly copied from VFE which was mostly copied from VSE
	 */
	public static boolean isHeadshot(LivingEntity target, ArrowEntity arrow) {
		if (target.hasVehicle()) return false;
		if (target instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)target;
			if (player.isSneaking()) {
				//totalHeight = 1.65;
				//bodyHeight = 1.20;
				//headHeight = 0.45;
				return arrow.getY() >= player.getY() + 1.20;
			} else {
				//totalHeight = 1.80;
				//bodyHeight = 1.35;
				//headHeight = 0.45;
				return arrow.getY() >= player.getY() + 1.35;
			}
		}  // TODO (Techjar): else { mobs }
		return false;
	}
	
	public static boolean shouldEndermanAttackVRPlayer(EndermanEntity enderman, PlayerEntity player) {
		ItemStack itemstack = player.inventory.armor.get(3);
		if (itemstack.getItem() == Blocks.CARVED_PUMPKIN.asItem() && PlayerTracker.hasPlayerData(player)) {
			VRPlayerData data = PlayerTracker.getAbsolutePlayerData(player);
			Quaternion quat = Objects.requireNonNull(data).head.getRotation();
			Vec3d vector3d = multiplyQuat(quat, new Vec3d(0, 0, -1));
			Vec3d vector3d1 = new Vec3d(enderman.getX() - data.head.x, enderman.getEyeY() - data.head.y, enderman.getZ() - data.head.z);
			double d0 = vector3d1.length();
			vector3d1 = vector3d1.normalize();
			double d1 = vector3d.dotProduct(vector3d1);
			return d1 > 1.0D - 0.025D / d0 && canEntityBeSeen(enderman, data.head.getPos());
		}
		return false;
	}
	
	public static boolean canEntityBeSeen(Entity entity, Vec3d playerEyePos) {
		Vec3d entityEyePos = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
		return entity.world.raycast(new RaycastContext(playerEyePos, entityEyePos, RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE, entity)).getType() == BlockHitResult.Type.MISS;
	}
	
	public static void replaceAIGoal(MobEntity entity, GoalSelector selector, Class<? extends Goal> targetGoal,
	                                 Supplier<Goal> newGoalSupplier) {
		PrioritizedGoal goal = selector.goals.stream().filter((g) -> targetGoal.isInstance(g.getGoal())).findFirst().orElse(null);
		if (goal != null) {
			selector.remove(goal.getGoal());
			selector.add(goal.getPriority(), newGoalSupplier.get());
		} else {
			VrCraft.LOGGER.error("Couldn't find " + targetGoal.getSimpleName() + " in " + entity);
		}
	}
	
	public static Identifier vcID(String path) {
		return new Identifier("vivecraft", path);
	}
	
	public static Identifier vrID(String path) {
		return new Identifier("vrcraft", path);
	}
	
	// This should be in Quaternion, according to the Forge source.
	// Apparently that's one of their patches. /shrug
	public static Vec3d multiplyQuat(Quaternion quat, Vec3d vec) {
		float num = quat.getX() * 2f;
		float num2 = quat.getY() * 2f;
		float num3 = quat.getZ() * 2f;
		float num4 = quat.getX() * num;
		float num5 = quat.getY() * num2;
		float num6 = quat.getZ() * num3;
		float num7 = quat.getX() * num2;
		float num8 = quat.getX() * num3;
		float num9 = quat.getY() * num3;
		float num10 = quat.getW() * num;
		float num11 = quat.getW() * num2;
		float num12 = quat.getW() * num3;
		double x = (1f - (num5 + num6)) * vec.x + (num7 - num12) * vec.y + (num8 + num11) * vec.z;
		double y = (num7 + num12) * vec.x + (1f - (num4 + num6)) * vec.y + (num9 - num10) * vec.z;
		double z = (num8 - num11) * vec.x + (num9 + num10) * vec.y + (1f - (num4 + num5)) * vec.z;
		return new Vec3d(x, y, z);
	}
	
	public static Vec3d addAll(Vec3d... a) {
		Vec3d vec = a[0];
		Iterator<Vec3d> iterator = Arrays.stream(a).iterator();
		while (iterator.hasNext()) {
			vec = vec.add(iterator.next());
		}
		return vec;
	}
}
