package com.halotroop.vrcraft.common.util;

import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
	public static Vec3d multiplyQuat(Vec3d vec, Quaternion quat) {
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
}
