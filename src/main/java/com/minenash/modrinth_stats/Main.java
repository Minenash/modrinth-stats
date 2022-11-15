package com.minenash.modrinth_stats;

import com.google.gson.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;


public class Main {

    public static Map<String, Data> projects = new TreeMap<>();
    public static Set<String> projectIds = new HashSet<>();

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        System.out.println();
        try {
            int total;
            for (int i = 0; true; i++) {
                JsonObject results = getProjectData(i * 100);
                JsonArray projects = results.get("hits").getAsJsonArray();
                total = results.get("total_hits").getAsInt() / 100;

                for (JsonElement project : projects)
                    processProjectData(project.getAsJsonObject());

                if (projects.isEmpty()) {
                    System.out.print("\rGrabbing Projects from Search: Done   \n");
                    break;
                }

                System.out.print("\rGrabbing Projects from Search: " + (99*(i+1)/ total) + "%   ");
            }
            addAuthors();
            projects = (Map<String, Data>) addMissingDates(projects, new Data());

            System.out.print("Generating CSV File: ...");
            DataExporter.export(projects, Path.of(""));
            System.out.print("\rGenerating CSV File: Done\n");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static JsonObject getProjectData(int offset) throws IOException {
        return getURLData("https://api.modrinth.com/v2/search?limit=100&index=newest&offset=" + offset).getAsJsonObject();

    }

    public static JsonElement getURLData(String urlstr) throws IOException {
        URL url = new URL(urlstr);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();

        if (responseCode != 200)
            throw new RuntimeException("HttpResponseCode: " + responseCode);

        return JsonParser.parseString( new String(conn.getInputStream().readAllBytes()) );
    }

    public static void processProjectData(JsonObject project) {
        projectIds.add(project.get("project_id").getAsString());

        String date = project.get("date_created").getAsString().substring(0,10);
        Data data = projects.getOrDefault(date, new Data());
        ProjectParser.parse(data, project);
        projects.put(date, data);
    }

    public static void addAuthors() throws IOException {

        int count = 0;
        int counts = projectIds.size()/444 + 1;

        Map<String,String> userToDate = new HashMap<>();
        List<String> ids = new ArrayList<>(444);
        for (String id : projectIds) {
            if (ids.size() >= 444) {
                addAuthors(userToDate, ids);
                ids.clear();
                System.out.print("\rGrabbing Authors from Projects: " + (100*count++/counts) + "%   ");
            }
            ids.add(id);
        }
        addAuthors(userToDate, ids);
        System.out.print("\rGrabbing Authors from Projects: Done   \n");

        for (var entry : userToDate.entrySet()) {
            Data data = projects.getOrDefault(entry.getValue(), new Data());
            data.authorsJoined++;
        }

    }

    public static void addAuthors(Map<String,String> userToDate, List<String> ids) throws IOException {

        StringBuilder projectRequestBuilder = new StringBuilder("https://api.modrinth.com/v2/projects?ids=[");
        for (String id : ids)
            projectRequestBuilder.append("%22").append(id).append("%22,");


        String projectRequest = projectRequestBuilder.substring(0, projectRequestBuilder.length() - 1) + "]";
        JsonArray projects = getURLData(projectRequest).getAsJsonArray();

        StringBuilder teamRequestBuilder = new StringBuilder("https://api.modrinth.com/v2/teams?ids=[");
        for (JsonElement project : projects)
            teamRequestBuilder.append("%22").append(project.getAsJsonObject().get("team").getAsString()).append("%22,");

        String teamRequest = teamRequestBuilder.substring(0, teamRequestBuilder.length() - 1) + "]";
        JsonArray teams = getURLData(teamRequest).getAsJsonArray();
        for (JsonElement team : teams) {
            for (JsonElement member : team.getAsJsonArray()) {
                String userId = member.getAsJsonObject().get("user").getAsJsonObject().get("id").getAsString();
                String date = member.getAsJsonObject().get("user").getAsJsonObject().get("created").getAsString().substring(0, 10);
                userToDate.put(userId, date);
            }
        }

    }

    public static Map<String, ?> addMissingDates(Map<String, ?> map, Object fillWith) {

        List<Map.Entry<String, ?>> list = new ArrayList<>(map.entrySet());

        for (int i = 0; i < list.size()-1; i++) {
            int day1 = Integer.parseInt( list.get(i).getKey().substring(8) );
            int day2 = Integer.parseInt( list.get(i+1).getKey().substring(8) );

            if (day2 == (day1+1)) continue;

            if (day2 > day1) {
                String dateDay = day1 >= 9 ? "" + (day1+1) : "0" + (day1+1);
                String date = list.get(i).getKey().substring(0,8) + dateDay;
                list.add(i+1, new AbstractMap.SimpleEntry<>(date, fillWith));
                continue;
            }

            if (day2 > 1) {
                for (int j = 1; j < day2; j++) {
                    String date = list.get(i+1).getKey().substring(0,8) + "0" + j;
                    list.add(i+j, new AbstractMap.SimpleEntry<>(date, fillWith));
                }
                day2 = Integer.parseInt( list.get(i+1).getKey().substring(8) );
            }

            if (day2 == 1) {
                int endOfMonth = endOfMonth(list.get(i).getKey());
                for (int j = day1+1; j <= endOfMonth; j++) {
                    String date = list.get(i).getKey().substring(0,8) + j;
                    list.add(i+(j-day1), new AbstractMap.SimpleEntry<>(date, fillWith));
                }
            }

        }

        Map<String, Object> newMap = new TreeMap<>();
        for (var e : list)
            newMap.put(e.getKey(), e.getValue());
        return newMap;

    }

    public static int endOfMonth(String date) {
        return switch (Integer.parseInt( date.substring(5,7))) {
            case 2 -> Integer.parseInt( date.substring(0,4))%4 == 0? 29 : 28;
            case 1,3,5,7,8,10,12 -> 31;
            default -> 30;
        };
    }

}