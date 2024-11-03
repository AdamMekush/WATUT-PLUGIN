package me.slide.watut.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private final JavaPlugin plugin;
    private String modrinthProjectId;
    private final Gson gson = new Gson();
    private boolean isOutdated = false;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean checkForUpdatesModrinth() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                URL url = new URL("https://api.modrinth.com/v2/project/" + modrinthProjectId + "/version");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                JsonArray versionsArray = gson.fromJson(reader, JsonArray.class);
                reader.close();

                if (!versionsArray.isEmpty()) {
                    JsonObject latestVersion = versionsArray.get(0).getAsJsonObject();
                    String latestVersionNumber = latestVersion.get("version_number").getAsString();
                    String currentVersion = plugin.getDescription().getVersion();

                    if (!currentVersion.equalsIgnoreCase(latestVersionNumber)) {
                        isOutdated = true;
                    }
                }
            } catch (Exception ignored) {}
        });
        return isOutdated;
    }

    public UpdateChecker setModrinthProjectId(String projectId){
        this.modrinthProjectId = projectId;
        return this;
    }

    public boolean isOutdated(){
        return checkForUpdatesModrinth();
    }
}