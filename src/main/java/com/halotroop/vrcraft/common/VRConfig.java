package com.halotroop.vrcraft.common;

import net.minecraft.util.math.Quaternion;

public class VRConfig {
	public static final int VERSION = 2;
	public static final int UNKNOWN_VERSION = 0;
	public final String DEGREE  = "\u00b0";
	
	public static final int INERTIA_NONE = 0;
	public static final int INERTIA_NORMAL = 1;
	public static final int INERTIA_LARGE = 2;
	public static final int INERTIA_MASSIVE = 3;
	
	public static final int BOW_MODE_ON = 2;
	public static final int BOW_MODE_VANILLA = 1;
	public static final int BOW_MODE_OFF = 0;
	
	public static final float INERTIA_NONE_ADD_FACTOR = 1f / 0.01f;
	public static final float INERTIA_NORMAL_ADD_FACTOR = 1f;
	public static final float INERTIA_LARGE_ADD_FACTOR = 1f / 4f;
	public static final float INERTIA_MASSIVE_ADD_FACTOR = 1f / 16f;
	public static final int RENDER_FIRST_PERSON_FULL = 0;
	public static final int RENDER_FIRST_PERSON_HAND = 1;
	public static final int RENDER_FIRST_PERSON_NONE = 2;
	public static final int RENDER_CROSSHAIR_MODE_ALWAYS = 0;
	public static final int RENDER_CROSSHAIR_MODE_HUD = 1;
	public static final int RENDER_CROSSHAIR_MODE_NEVER = 2;
	public static final int RENDER_BLOCK_OUTLINE_MODE_ALWAYS = 0;
	public static final int RENDER_BLOCK_OUTLINE_MODE_HUD = 1;
	public static final int RENDER_BLOCK_OUTLINE_MODE_NEVER = 2;
	
	public static final int CHAT_NOTIFICATIONS_NONE = 0;
	public static final int CHAT_NOTIFICATIONS_HAPTIC = 1;
	public static final int CHAT_NOTIFICATIONS_SOUND = 2;
	public static final int CHAT_NOTIFICATIONS_BOTH = 3;
	
	public static final int MIRROR_OFF = 10;
	public static final int MIRROR_ON_DUAL = 11;
	public static final int MIRROR_ON_SINGLE = 12;
	public static final int MIRROR_FIRST_PERSON = 13;
	public static final int MIRROR_THIRD_PERSON = 14;
	public static final int MIRROR_MIXED_REALITY = 15;
	public static final int MIRROR_ON_CROPPED = 16;
	
	public static final int HUD_LOCK_HEAD= 1;
	public static final int HUD_LOCK_HAND= 2;
	public static final int HUD_LOCK_WRIST= 3;
	public static final int HUD_LOCK_BODY= 4;
	
	public static final int FREEMOVE_CONTROLLER= 1;
	public static final int FREEMOVE_HMD= 2;
	public static final int FREEMOVE_RUNINPLACE= 3;
	public static final int FREEMOVE_ROOM= 5;
	@Deprecated
	public static final int FREEMOVE_JOYPAD = 4;
	
	public static final int MENU_WORLD_BOTH = 0;
	public static final int MENU_WORLD_CUSTOM = 1;
	public static final int MENU_WORLD_OFFICIAL = 2;
	public static final int MENU_WORLD_NONE = 3;
	
	public static final int NO_SHADER = -1;
	
	public int version = UNKNOWN_VERSION;
	
	public int renderFullFirstPersonModelMode = RENDER_FIRST_PERSON_HAND;   // VIVE - hand only by default
	public int shaderIndex = NO_SHADER;
	public String stereoProviderPluginID = "openvr";
	public String badStereoProviderPluginID = "";
	public boolean storeDebugAim = false;
	public int smoothRunTickCount = 20;
	public boolean smoothTick = false;
	//Jrbudda's Options
	
	public String[] vrQuickCommands;
	public String[] vrRadialItems;
	public String[] vrRadialItemsAlt;
	
	//Control
	public boolean vrReverseHands = false;
	public boolean vrReverseShootingEye = false;
	public float vrWorldScale = 1.0f;
	public float vrWorldRotation = 0f;
	public float vrWorldRotationCached;
	public float vrWorldRotationIncrement = 45f;
	public float xSensitivity=1f;
	public float ySensitivity=1f;
	public float keyholeX=15;
	public double headToHmdLength=0.10f;
	public float autoCalibration=-1;
	public float manualCalibration=-1;
	public boolean alwaysSimulateKeyboard = false;
	public int bowMode = BOW_MODE_ON;
	public String keyboardKeys = "`1234567890-=qwertyuiop[]\\asdfghjkl;':\"zxcvbnm,./?<>";
	public String keyboardKeysShift = "~!@#$%^&*()_+QWERTYUIOP{}|ASDFGHJKL;':\"ZXCVBNM,./?<>";
	public int hrtfSelection = 0;
	public boolean firstRun = true;
	public int rightclickDelay = 6 ;
	//
	
	//Locomotion
	public int inertiaFactor = INERTIA_NORMAL;
	public boolean walkUpBlocks = true;     // VIVE default to enable climbing
	public boolean simulateFalling = true;  // VIVE if HMD is over empty space, fall
	public int weaponCollision = 2;  // VIVE weapon hand collides with blocks/enemies
	public float movementSpeedMultiplier = 1.0f;   // VIVE - use full speed by default
	public int vrFreeMoveMode = FREEMOVE_CONTROLLER;
	public boolean vrLimitedSurvivalTeleport = true;
	
	public int vrTeleportUpLimit = 1;
	public int vrTeleportDownLimit = 4;
	public int vrTeleportHorizLimit = 16;
	
	public boolean seated = false;
	public boolean seatedUseHMD = false;
	public float jumpThreshold=0.05f;
	public float sneakThreshold=0.4f;
	public float crawlThreshold = 0.82f;
	public boolean realisticJumpEnabled=true;
	public boolean realisticSneakEnabled=true;
	public boolean realisticClimbEnabled=true;
	public boolean realisticSwimEnabled=true;
	public boolean realisticRowEnabled=true;
	public boolean backpackSwitching = true;
	public boolean physicalGuiEnabled = false;
	public float walkMultiplier=1;
	public boolean vrAllowCrawling = true;
	public boolean vrShowBlueCircleBuddy = true;
	public boolean vehicleRotation = true;
	public boolean analogMovement = true;
	public boolean autoSprint = true;
	public float autoSprintThreshold = 0.9f;
	public boolean allowStandingOriginOffset = false;
	public boolean seatedFreeMove = false;
	public boolean forceStandingFreeMove = false;
	//
	
	//Rendering
	public boolean useFsaa = true;   // default to off
	public boolean useFOVReduction = false;   // default to off
	public float fovRedutioncOffset = 0.1f;
	public float fovReductionMin = 0.25f;
	public boolean vrUseStencil = true;
	public boolean insideBlockSolidColor = false; //unused
	public float renderScaleFactor = 1.0f;
	public int displayMirrorMode = MIRROR_ON_CROPPED;
	public boolean displayMirrorLeftEye = false;
	public boolean shouldRenderSelf=false;
	public boolean tmpRenderSelf;
	public int menuWorldSelection = MENU_WORLD_BOTH;
	//
	
	//Mixed Reality
	public float mixedRealityAspectRatio = 16F / 9F;
	public boolean mixedRealityRenderHands = false;
	public boolean mixedRealityUnityLike = true;
	public boolean mixedRealityMRPlusUndistorted = true;
	public boolean mixedRealityAlphaMask = false;
	public float mixedRealityFov = 40;
	public float vrFixedCamposX = -1.0f;
	public float vrFixedCamposY = 2.4f;
	public float vrFixedCamposZ = 2.7f;
	public Quaternion vrFixedCamrotQuat =new Quaternion(.962f, .125f, .239f, .041f);
	public float mrMovingCamOffsetX = 0;
	public float mrMovingCamOffsetY = 0;
	public float mrMovingCamOffsetZ = 0;
	public float handCameraFov = 70;
	public float handCameraResScale = 1.0f;
	//
	
	//HUD/GUI
	public boolean vrTouchHotbar = true;
	public float hudScale = 1.0f;
	public float hudDistance = 1.25f;
	public float hudPitchOffset = -2f;
	public float hudYawOffset = 0.0f;
	public boolean floatInventory = true; //false not working yet, have to account for rotation and tilt in MCOpenVR>processGui()
	public boolean menuAlwaysFollowFace;
	public int vrHudLockMode = HUD_LOCK_WRIST;
	public boolean hudOcclusion = true;
	public float crosshairScale = 1.0f;
	public boolean crosshairScalesWithDistance = false;
	public int renderInGameCrosshairMode = RENDER_CROSSHAIR_MODE_ALWAYS;
	public int renderBlockOutlineMode = RENDER_BLOCK_OUTLINE_MODE_ALWAYS;
	public float hudOpacity = 1f;
	public boolean menuBackground = false;
	public float   menuCrosshairScale = 1f;
	public boolean useCrosshairOcclusion = true;
	public boolean seatedHudAltMode = true;
	public boolean autoOpenKeyboard = false;
	public int forceHardwareDetection = 0; // 0 = off, 1 = vive, 2 = oculus
	public boolean radialModeHold = true;
	public boolean physicalKeyboard = true;
	public float physicalKeyboardScale = 1.0f;
	public boolean allowAdvancedBindings = false;
	public int chatNotifications = CHAT_NOTIFICATIONS_NONE; // 0 = off, 1 = haptic, 2 = sound, 3 = both
	public String chatNotificationSound = "block.note_block.bell";
	public boolean guiAppearOverBlock = true;
}
