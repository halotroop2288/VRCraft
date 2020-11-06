package com.halotroop.vrcraft.client.util.external;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import io.github.cottonmc.cotton.logging.ModLogger;

public class JInfinadeck implements Library {
	private static final ModLogger LOGGER = new ModLogger("JInfinadeck");
	
	public static native int InitInternal(IntByReference inError, boolean use_local_server);
	
	public static native int DeInitInternal();
	
	public static native boolean CheckConnection();
	
	public static native boolean GetTreadmillRunState();
	
	public static native double GetFloorSpeedAngle();
	
	public static native double GetFloorSpeedMagnitude();
	
	public static final String INFINADECK_LIBRARY_NAME = "InfinadeckAPI.dll";
	public static final NativeLibrary INFINADECK_NATIVE_LIB = NativeLibrary.getInstance(INFINADECK_LIBRARY_NAME);
	
	static {
		Native.register(JInfinadeck.class, INFINADECK_NATIVE_LIB);
	}

	public static boolean InitConnection() {
		IntByReference error = new IntByReference();
		InitInternal(error, false);
		if(error.getValue() != 0) {
			InitInternal(error, true);
		}
		return error.getValue() == 0;
	}
	
	public static void Destroy() {
		DeInitInternal();
	}

	static float yaw, yawOffset;
	static double power;
	static int direction;
	static boolean isMoving;

	static IntByReference y = new IntByReference();
	static IntByReference m = new IntByReference();
	static IntByReference is = new IntByReference();
	static DoubleByReference pow = new DoubleByReference();
	static FloatByReference fl = new FloatByReference();

	static float mag = 0.15f;
	static float bMag = 0.10f;
	static float maxPower = 2;
	
	public static void query(){
		try {
			CheckConnection();
			yaw = (float) GetFloorSpeedAngle();
			power = GetFloorSpeedMagnitude();
			direction = 1;
			isMoving = GetTreadmillRunState();
			yaw = yaw * 57.296f;
		} catch (Exception e) {
			LOGGER.error("Infinadeck Error: " + e.getMessage());
		}
	}
	
	public static float getYaw(){
		return yaw - yawOffset;
	}
	
	public static boolean isMoving(){
		return true;
	}
	
	public static void resetYaw(float offsetDegrees){
		yawOffset = offsetDegrees + yaw;
	}

	
	public static float walkDirection(){
		return direction;
	}
	
	public static float getSpeed(){
		return (float) (power/ maxPower * (walkDirection() == 1 ? mag : bMag));
	}
}