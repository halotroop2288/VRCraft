package com.halotroop.vrcraft.common.util;

import com.halotroop.vrcraft.common.network.packet.DeviceDataPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * An Object that represents a player's VR headset and controller data
 * <p>
 * This is a Frankenstein's monster of the classes that control this data in Vivecraft and in VFE
 *
 * @author Techjar, jrbudda, halotroop2288
 */
public class VRPlayerData {
	private static final Vec3d FORWARD = new Vec3d(0, 0, -1);
	public Vec3d offset = new Vec3d(0, 0, 0);
	public DeviceDataPacket head = new DeviceDataPacket(DeviceDataPacket.Device.HMD);
	public DeviceDataPacket controller0 = new DeviceDataPacket(DeviceDataPacket.Device.CONTROLLER_0);
	public DeviceDataPacket controller1 = new DeviceDataPacket(DeviceDataPacket.Device.CONTROLLER_1);
	public boolean handsReversed;
	public float worldScale;
	public boolean seated;
	public boolean freeMove;
	public float bowDraw; // bow draw
	public float height;
	public int activeHand;
	public boolean crawling;
	public boolean vr = true;
	public PlayerEntity player;
	
	public VRPlayerData() {
	}
	
	public VRPlayerData(PlayerEntity player) {
		this.player = player;
	}
	
	public float getBowDraw() {
		return bowDraw;
	}
	
	public Vec3d getControllerVectorCustom(int controller, Vec3d direction) {
		return Util.multiplyQuat(controller == 0 ? this.controller0.getRotation() : controller1.getRotation(), direction);
	}
	
	public Vec3d getHMDDirection() {
		return Util.multiplyQuat(head.getRotation(), FORWARD);
	}
	
	public Vec3d getHMDPosition(PlayerEntity player) {
		return head.getPos().add(player.getPos()).add(offset);
	}
	
	public Vec3d getControllerPosition(int c, PlayerEntity player) {
		if (this.seated) {
			Vec3d dir = this.getHMDDirection();
			dir = dir.rotateY((float) Math.toRadians(c == 0 ? -35 : 35));
			dir = new Vec3d(dir.x, 0, dir.z).normalize();
			Vec3d out = this.getHMDPosition(player).add(dir.x * 0.3 * worldScale,
					-0.4 * worldScale, dir.z * 0.3 * worldScale);
			return new Vec3d(out.x, out.y, out.z);
		}

		return player.getPos().add(0, 1.62, 0);
	}
	
	public boolean isVR() {
		return this.vr;
	}
	
	public void setVR(boolean vr) {
		this.vr = vr;
	}
	
	public boolean isSeated() {
		return seated;
	}
	
	public DeviceDataPacket getController(int c) {
		return c == 0 ? controller0 : controller1;
	}
	
}
