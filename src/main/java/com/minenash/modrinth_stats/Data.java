package com.minenash.modrinth_stats;

import java.util.LinkedHashMap;
import java.util.Map;

public class Data {

    public static final Data EMPTY = new Data();

    public int totalProjects = 0;
    public int authorsJoined = 0;

    public Map<String, Integer> projectType = populate("mod", "modpack", "resourcepack");
    public Map<String, Integer> projectLicenses = new LinkedHashMap<>();

    public Map<String, Integer> modLoaders = populate(
            "fabric", "forge", "quilt", "rift", "modloader", "bukkit", "spigot", "paper", "purpur", "sponge"
    );
    public Map<String, Integer> modCategories = populate(
            "adventure", "cursed", "decoration", "economy", "equipment", "food", "game-mechanics", "library",
            "magic", "management", "minigame", "mobs", "optimization", "social", "storage", "technology",
            "transportation", "utility", "worldgen"
    );
    public Map<String, Integer> modRequiredEnvironment = populate("client", "server", "both");
    public Map<String, Integer> modLicenses = new LinkedHashMap<>();

    public Map<String, Integer> modpackLoaders = populate("fabric", "forge", "quilt");
    public Map<String, Integer> modpackCategories = populate(
            "adventure", "challenging", "combat", "kitchen-sink", "lightweight", "magic", "multiplayer",
            "optimizations", "quests", "technology"
    );
    public Map<String, Integer> modpackLicenses = new LinkedHashMap<>();

    public Map<String, Integer> resourcepackFeatures = populate(
            "audio", "blocks", "core-shaders", "entities", "environment", "equipment", "fonts", "gui",
            "items", "locale", "models"
    );
    public Map<String, Integer> resourcepackCategories = populate(
            "combat", "cursed", "decoration", "modded", "realistic", "simplistic", "themed", "tweaks",
            "utility", "vanilla-like"
    );
    public Map<String, Integer> resourcepackResolutions = populate(
            "8x-", "16x", "32x", "64x", "128x", "256x", "512x+"
    );
    public Map<String, Integer> resourcepackLicenses = new LinkedHashMap<>();


    public static Map<String, Integer> populate(String... keys) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String key : keys)
            map.put(key, 0);
        return map;
    }


}
