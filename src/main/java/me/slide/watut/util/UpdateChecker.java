package me.slide.watut.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {
    private final JavaPlugin plugin;
    private String modrinthProjectId;
    private final Gson gson = new Gson();
    private final Set<UUID> notifiedPlayers = new HashSet<>();

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Boolean> checkForUpdatesModrinth() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                URL url = new URL("https://api.modrinth.com/v2/project/" + modrinthProjectId + "/version");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                JsonArray versionsArray = gson.fromJson(reader, JsonArray.class);
                reader.close();

                if (versionsArray.size() > 0) {
                    JsonObject latestVersion = versionsArray.get(0).getAsJsonObject();
                    String latestVersionNumber = latestVersion.get("version_number").getAsString();
                    String currentVersion = plugin.getDescription().getVersion();

                    boolean isOutdated = !currentVersion.equalsIgnoreCase(latestVersionNumber);
                    future.complete(isOutdated);
                } else {
                    future.complete(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                future.complete(false);
            }
        });

        return future;
    }

    public UpdateChecker setModrinthProjectId(String projectId){
        this.modrinthProjectId = projectId;
        return this;
    }

    public boolean isPlayerNotified(Player player){
        return notifiedPlayers.contains(player.getUniqueId());
    }

    public void markAsNotified(Player player){
        notifiedPlayers.add(player.getUniqueId());
    }

    public Component getUpdateMessage(){
        return Component.text("A new version of WatutPlugin is now available for you to download!").color(TextColor.color(16755200)).appendSpace().append(Component.text("[Modrinth]").color(TextColor.color(5635925)).clickEvent(ClickEvent.openUrl("https://modrinth.com/plugin/watut-plugin")).hoverEvent(HoverEvent.showText(Component.text("Click!"))));
    }
}