package com.halotroop.vrcraft.server;

import com.halotroop.vrcraft.common.network.packet.VRPacketHandler;
import io.github.cottonmc.cotton.config.annotations.ConfigFile;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

import java.util.Arrays;
import java.util.List;

@ConfigFile(name = "VRCraftServer")
public class ServerConfig {
	// General
	public boolean debug = false;
	@Comment("Will check for a newer version and alert any operator when they login to the server")
	public boolean checkForUpdate = true;
	@Comment("Set to true to only allow VR players to play.")
	public boolean vrOnly = false;
	@Comment("If true, creates an exclusion for vrOnly for operators. No effect if vive-only is false.")
	public boolean exceptOP = true;
	@Comment("The message to show kicked non-vive players.")
	public String vrOnlyKickMessage = "This server is configured for VR players only.";
	@Comment("Ticks to wait before kicking a player. The player's client must send Vivecraft/VRCraft Version info in" +
			" that time.")
	public int vrOnlyKickDelay = 200;
	@Comment("Set to false to disable registering Vivecraft's crafting recipes.")
	public boolean viveCrafting = true;
	
	// SendPlayerData
	@Comment("Send player data to all clients with Vivecraft")
	public boolean sendPlayerData = true;
	
	// CreeperRadius
	@Comment("Distance from a Vivecraft player before creeper starts to swell up (vanilla is 3)")
	public boolean enableCreeperRadius = true;
	public double creeperRadius = 1.75;
	
	//setSpigotConfig
	//enabled
	//movedWronglyThreshold
	//movedTooQuickly
	
	// PVP
	@Comment("Allows Standing VR players to Attack other Standing VR Players")
	public boolean pvpVRvsVR = true;
	@Comment("Allows Seated VR Players to attack other Seated VR Players")
	public boolean pvpSeatedVRvsSeatedVR = true;
	@Comment("Allows Standing VR Players to attack Non VR players")
	public boolean pvpVRvsNonVR = true;
	@Comment("Allows Seated VR Players to attack normal players.")
	public boolean pvpSeatedVRvsNonVR = true;
	@Comment("Allows Standing VR Players to attack Seated VR Players")
	public boolean pvpVRvsSeatedVR = true;
	
	// Bow
	@Comment("Archery damage multiplier for Vivecraft (standing) users. Set to 1 to disable")
	public int standingMultiplier = 2;
	@Comment("Archery damage multiplier for Vivecraft (seated) users. Set to 1 to disable")
	public int seatedMultiplier = 1;
	@Comment("Headshot damage multiplier for Vivecraft (standing) users. Set to 1 to disable")
	public int standingHeadshotMultiplier = 3;
	@Comment("Headshot damage multiplier for Vivecraft (seated) users. Set to 1 to disable")
	public int seatedHeadshotMultiplier = 2;
	@Comment("Set if players can headshot mobs (only horizontal mobs)")
	public boolean headshotMobs = true;
	
	// Permissions
	@Comment("Enable setting player groups for vive users.")
	public boolean permsEnabled = false;
	@Comment("Permission group for Vive users")
	public String viveGroup = "vive.vivegroup";
	@Comment("Permission group for non-Vive users")
	public String nonViveGroup = "vive.non-vivegroup";
	@Comment("Permission group for vive users in free move mode.")
	public String viveFreeMoveGroup = "vive.freemovegroup";
	@Comment("Permission to override climb limitations.")
	public String climbPerm = "vive.climbanywhere";
	
	// Welcome message
	public boolean welcomeMsgEnabled = false;
	@Comment("Remove message to not send or set to nothing. ex: leaveMessage = \"\"")
	public String welcomeVR = "&player has joined with standing VR!";
	public String welcomeNonVR = "&player has joined with Non-VR companion!";
	public String welcomeSeated = "&player has joined with seated VR!";
	public String welcomeVanilla = "&player has joined as a Muggle!";
	public String leaveMessage = "&player has disconnected from the server!";
	
	// Climbey
	@Comment("Allows use of jump_boots and climb_claws. Provide with /give (player) climb_claws | jump_boots")
	public boolean enableClimbing = false;
	@Comment("Sets which blocks are climb-able. Options are" +
			"'None': List ignored. All blocks are climbable. " +
			"'Include': Only blocks on the list are climbable. " +
			"'Exclude': All blocks are climbable except those on the list")
	public VRPacketHandler.BlockListMode blockMode = VRPacketHandler.BlockListMode.NONE;
	@Comment("The list of block names for use with include/exclude block mode.")
	public List<String> blockList = Arrays.asList("white_wool", "dirt", "grass_block");
	
	// Crawling
	@Comment("Allows use of roomscale crawling. Disabling does not prevent vanilla crawling.")
	public boolean enableCrawling = false;
	
	// Teleport
	@Comment("Whether direct teleport is enabled. " +
			"It is recommended to leave this enabled for players prone to VR sickness.")
	public boolean enableTeleport = true;
	@Comment("Limit teleport range and frequency in survival")
	public boolean limitedSurvival = false;
	@Comment("Maximum blocks players can teleport up. Set to 0 to disable. Max: 4")
	public int upLimit = 1;
	@Comment("Maximum blocks players can teleport down. Set to 0 to disable. Max 16")
	public int downLimit = 4;
	@Comment("Maximum blocks players can teleport horizontally. Set to 0 to disable. Max: 32")
	public int horizontalLimit = 16;
	
	// World Scale
	@Comment("Limit the range of world scale players can use")
	public boolean limitRange = false;
	@Comment("Upper limit of range")
	public double maxRange = 2;
	@Comment("Lower limit of range")
	public double minRange = 0.5;
	
	@Comment("Whether to allow Vivecraft clients to connect to the server")
	public boolean allowVivecraft = true;
	@Comment("Message to show players who are disconnected for using Vivecraft")
	public String vivecraftDisconnectMessage = "This server does not allow Vivecraft users.";
	
	@Comment("Whether to allow Vivecraft players to connect to the server")
	public boolean allowVRCraft = true;
	@Comment("Message to show players who are disconnected for using VRcraft client")
	public String vrCraftDisconnectMessage = "This server does not allow VRCraft users.";
}