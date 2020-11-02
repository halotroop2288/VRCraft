package com.halotroop.vrcraft.common.util;

import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

/**
 * An Object that represents a player's VR headset and controller data
 * @authors Techjar, halotroop2288
 */
public class VRPlayerData {
	public Vec3d offset = new Vec3d(0, 0, 0);
	public ObjectInfo head = new ObjectInfo();
	public ObjectInfo controller0 = new ObjectInfo();
	public ObjectInfo controller1 = new ObjectInfo();
	public boolean handsReversed;
	public float worldScale;
	public boolean seated;
	public boolean freeMove;
	public float bowDraw;
	public float height;
	public int activeHand;
	public boolean crawling;
	
	public ObjectInfo getController(int c) {
		return c == 0 ? controller0 : controller1;
	}
	
	/**
	 * An Object that represents the position of a real-world object
	 */
	public static class ObjectInfo {
		public double posX;
		public double posY;
		public double posZ;
		public float rotW;
		public float rotX;
		public float rotY;
		public float rotZ;
		
		public Vec3d getPos() {
			return new Vec3d(posX, posY, posZ);
		}
		
		public void setPos(Vec3d pos) {
			posX = pos.x;
			posY = pos.y;
			posZ = pos.z;
		}
		
		public Quaternion getRot() {
			return new Quaternion(rotW, rotX, rotY, rotZ);
		}
		
		public void setRot(Quaternion quat) {
			rotW = quat.getW();
			rotX = quat.getX();
			rotY = quat.getY();
			rotZ = quat.getZ();
		}
	}
}
