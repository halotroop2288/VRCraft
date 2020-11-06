package com.halotroop.vrcraft.client.util.external;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.SyntaxError;
import com.halotroop.vrcraft.common.VrCraft;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PatreonImpl {
	private static final List<String> developers = Arrays.asList("halotroop2888", "Techjar", "jrbudda");
	private static HashMap<String, Integer> jrbuddaPatrons = new HashMap<>();
	private static HashMap<String, Integer> carolinePatrons = new HashMap<>();
	
	private static final List<PlayerEntity> hmdPlayers = new LinkedList<>();
	private static final List<PlayerEntity> haloPlayers = new LinkedList<>();
	
	static {
		getJrbuddaPatronInfo();
		getCarolinePatronInfo();
	}
	
	private static void getCarolinePatronInfo() {
		String rawInfo = readUrl("https://gist.githubusercontent.com/halotroop2288/248bfa9d7b659639e595847538eefb7c/raw/b553de713688d7eccfe44f58a997f8f1415ce0b7/patrons.json");
		try {
			CarolinePatronInfo info = Jankson.builder().build().fromJson(rawInfo, CarolinePatronInfo.class);
			carolinePatrons = info.usernameLevel;
		} catch (SyntaxError e) {
			VrCraft.LOGGER.error(e.getLineMessage());
		}
	}
	
	private static class CarolinePatronInfo {
		HashMap<String, Integer> usernameLevel = new HashMap<>();
	}
	
	public static void addPlayerInfo(PlayerEntity player) {
		if (isJrbuddaPatron(player)) hmdPlayers.add(player);
		if (isCarolinePatron(player)) haloPlayers.add(player);
	}
	
	private static void getJrbuddaPatronInfo() {
		String rawInfo = readUrl("http://www.vivecraft.org/patreon/current.txt");
		HashMap<String, Integer> map = new HashMap<>();
		
		String[] lines = rawInfo.split("\\r?\\n");
		for (String string : lines) {
			try {
				String[] bits = string.split(":");
				map.put(bits[0], Integer.parseInt(bits[1]));
			} catch (NumberFormatException ignored) {
				VrCraft.LOGGER.warn("jrbudda's patron info file is malformed!");
			}
		}
		jrbuddaPatrons = map;
	}
	
	private static String readUrl(String urlString) {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder buffer = new StringBuilder();
			int read;
			char[] chars = new char[4096];
			while ((read = reader.read(chars)) != -1) buffer.append(chars, 0, read);
			return buffer.toString();
		} catch (Exception ignored) {
			VrCraft.LOGGER.warn("Failed to download Patron info.");
		} finally {
			try {
				if (reader != null) reader.close();
			} catch (IOException ignored) {
			}
		}
		return "";
	}
	
	public static boolean shouldRenderHMD(PlayerEntity player) {
		return hmdPlayers.contains(player);
	}
	
	public static boolean shouldRenderHalo(PlayerEntity player) {
		return haloPlayers.contains(player);
	}
	
	private static boolean isJrbuddaPatron(PlayerEntity entity) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) return true; // Debug
		String name = entity.getName().asString().toLowerCase();
		return developers.contains(name) || jrbuddaPatrons.getOrDefault(name, 0) > 0;
	}
	
	public static boolean isCarolinePatron(PlayerEntity entity) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) return true; // Debug
		String name = entity.getName().asString().toLowerCase();
		return developers.contains(name) || carolinePatrons.getOrDefault(name, 0) > 0;
	}
}
