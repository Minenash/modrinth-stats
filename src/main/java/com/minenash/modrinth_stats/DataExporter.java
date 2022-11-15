package com.minenash.modrinth_stats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DataExporter {

    public static void export(Map<String,Data> data, Path path) throws IOException {
        StringBuilder str = new StringBuilder();

        str.append("all, , , , , , , ,");
        appendPointKeysParent(str, "mods", Data.EMPTY.modLoaders, Data.EMPTY.modCategories, Data.EMPTY.modRequiredEnvironment);
        appendPointKeysParent(str, "modpacks", Data.EMPTY.modpackLoaders, Data.EMPTY.modpackCategories);
        appendPointKeysParent(str, "resourcepacks", Data.EMPTY.resourcepackFeatures, Data.EMPTY.resourcepackCategories, Data.EMPTY.resourcepackResolutions);


        str.append("\ndate, authors, total, , ");
        appendPointKeysParent(str, "type", Data.EMPTY.projectType);
//            appendPointKeysParent(str, "licenses", Data.EMPTY.projectLicenses);

        appendPointKeysParent(str, "loaders", Data.EMPTY.modLoaders);
        appendPointKeysParent(str, "categories", Data.EMPTY.modCategories);
        appendPointKeysParent(str, "required_environment", Data.EMPTY.modRequiredEnvironment);
//            appendPointKeysParent(str, "licenses", Data.EMPTY.modLicenses);

        appendPointKeysParent(str, "loaders", Data.EMPTY.modpackLoaders);
        appendPointKeysParent(str, "categories", Data.EMPTY.modpackCategories);
//            appendPointKeysParent(str, "licenses", Data.EMPTY.modpackLicenses);

        appendPointKeysParent(str, "features", Data.EMPTY.resourcepackFeatures);
        appendPointKeysParent(str, "categories", Data.EMPTY.resourcepackCategories);
        appendPointKeysParent(str, "resolutions", Data.EMPTY.resourcepackResolutions);
//            appendPointKeysParent(str, "licenses", Data.EMPTY.resourcepackLicenses);

        str.append("\ndate, authors, total, , ");
        appendPointKeys(str, Data.EMPTY.projectType);
//            appendPointKeys(str, Data.EMPTY.projectLicenses);

        appendPointKeys(str, Data.EMPTY.modLoaders);
        appendPointKeys(str, Data.EMPTY.modCategories);
        appendPointKeys(str, Data.EMPTY.modRequiredEnvironment);
//            appendPointKeys(str, Data.EMPTY.modLicenses);

        appendPointKeys(str, Data.EMPTY.modpackLoaders);
        appendPointKeys(str, Data.EMPTY.modpackCategories);
//            appendPointKeys(str, Data.EMPTY.modpackLicenses);

        appendPointKeys(str, Data.EMPTY.resourcepackFeatures);
        appendPointKeys(str, Data.EMPTY.resourcepackCategories);
        appendPointKeys(str, Data.EMPTY.resourcepackResolutions);
//            appendPointKeys(str, Data.EMPTY.resourcepackLicenses);


        for (var entry : data.entrySet()) {
            Data d = entry.getValue();
            str.append("\n").append(entry.getKey()).append(", ");
            str.append(d.authorsJoined).append(", ");
            str.append(d.totalProjects).append(", , ");

            appendPoint(str, d.projectType);
//            appendPoint(str, d.projectLicenses);

            appendPoint(str, d.modLoaders);
            appendPoint(str, d.modCategories);
            appendPoint(str, d.modRequiredEnvironment);
//            appendPoint(str, d.modLicenses);

            appendPoint(str, d.modpackLoaders);
            appendPoint(str, d.modpackCategories);
//            appendPoint(str, d.modpackLicenses);

            appendPoint(str, d.resourcepackFeatures);
            appendPoint(str, d.resourcepackCategories);
            appendPoint(str, d.resourcepackResolutions);
//            appendPoint(str, d.resourcepackLicenses);

        }

        BufferedWriter out = new BufferedWriter(new FileWriter(path.resolve("stats.csv").toFile()));
        out.write(str.toString());
        out.flush();
        out.close();


    }

    public static void appendPoint(StringBuilder str, Map<String, Integer> point) {
        for (int entry : point.values())
            str.append(entry).append(", ");
        str.append(", ");
    }

    public static void appendPointKeys(StringBuilder str, Map<String, Integer> point) {
        for (String entry : point.keySet())
            str.append(entry).append(", ");
        str.append(", ");
    }

    public static void appendPointKeysParent(StringBuilder str, String key, Map<String, Integer>... points) {
        str.append(key);
        for (var point : points)
            str.append(", ".repeat(point.keySet().size()));
        str.append(", ".repeat(points.length));
    }

}
