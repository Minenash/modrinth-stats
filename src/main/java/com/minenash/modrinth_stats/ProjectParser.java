package com.minenash.modrinth_stats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class ProjectParser {

    public static void increment(Map<String, Integer> data, String key) {
        Integer v = data.get(key);
        if (v != null)
            data.put(key, v+1);
    }

    public static void parse(Data data, JsonObject project) {
        data.totalProjects++;
        increment(data.projectType, project.get("project_type").getAsString());
        increment(data.projectLicenses, project.get("license").getAsString());

        switch (project.get("project_type").getAsString()) {
            case "mod" -> parseMod(data, project);
            case "modpack" -> parseModpack(data, project);
            case "resourcepack" -> parseResourcepack(data, project);
        }
    }

    public static void parseMod(Data data, JsonObject project) {
        for (JsonElement jcat : project.get("display_categories").getAsJsonArray()) {
            String cat = jcat.getAsString();
            increment(data.modLoaders, cat);
            increment(data.modCategories, cat);
        }

        boolean client = project.get("client_side").getAsString().equals("required");
        boolean server = project.get("server_side").getAsString().equals("required");

        if (client && !server) increment(data.modRequiredEnvironment, "client");
        if (server && !client) increment(data.modRequiredEnvironment, "server");
        if (client && server)  increment(data.modRequiredEnvironment, "both");

        increment(data.modLicenses, project.get("license").getAsString());
    }

    public static void parseModpack(Data data, JsonObject project) {
        for (JsonElement jcat : project.get("display_categories").getAsJsonArray()) {
            String cat = jcat.getAsString();
            increment(data.modpackLoaders, cat);
            increment(data.modpackCategories, cat);
        }

        increment(data.modpackLicenses, project.get("license").getAsString());
    }

    public static void parseResourcepack(Data data, JsonObject project) {
        for (JsonElement jcat : project.get("categories").getAsJsonArray()) {
            String cat = jcat.getAsString();
            increment(data.resourcepackFeatures, cat);
            increment(data.resourcepackCategories, cat);
            increment(data.resourcepackResolutions, cat);
        }

        increment(data.resourcepackLicenses, project.get("license").getAsString());
    }

}
