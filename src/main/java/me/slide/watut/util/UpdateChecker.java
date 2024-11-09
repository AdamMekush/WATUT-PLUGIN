package me.slide.watut.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.slide.watut.WatutPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker implements Listener {
    private static final String MODRINTH_API_URL = "https://api.modrinth.com/v2/project/%s/version";
    private final String MODRINTH_PROJECT_ID = "watut-plugin";
    private final JavaPlugin plugin;
    private final String pluginVersion;
    private String modrinthVersion;
    private boolean updateAvailable;
    private final Gson gson;

    public UpdateChecker(JavaPlugin i) {
        this.plugin = i;
        this.pluginVersion = i.getDescription().getVersion();
        this.gson = new Gson();
    }

    public boolean hasUpdateAvailable() {
        return this.updateAvailable;
    }

    public String getModrinthVersion() {
        return this.modrinthVersion;
    }

    public void fetch() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                String urlString = String.format(MODRINTH_API_URL, MODRINTH_PROJECT_ID);
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                JsonArray jsonArray = gson.fromJson(response.toString(), JsonArray.class);
                if (jsonArray.size() > 0) {
                    JsonObject latestVersionData = jsonArray.get(0).getAsJsonObject();
                    modrinthVersion = latestVersionData.get("version_number").getAsString();
                }
                in.close();
            } catch (IOException ignored) {}

            if (this.modrinthVersion != null && !this.modrinthVersion.isEmpty()) {
                this.updateAvailable = Version.isOlderVersion(Version.getVersion(pluginVersion), Version.getVersion(modrinthVersion));
                if (this.updateAvailable) {
                    this.plugin.getLogger().warning("An update for WatutPlugin (v" + this.getModrinthVersion() + ") is available at:");
                    this.plugin.getLogger().warning("https://modrinth.com/plugin/watut-plugin");
                    Bukkit.getPluginManager().registerEvents(this, this.plugin);
                }
            }
        });
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("watut.updatenotify")) {
            WatutPlugin.getInstance().adventure().player(event.getPlayer()).sendMessage(Component.text("An update for WatutPlugin (v" + this.getModrinthVersion() + ") is available at:").appendSpace().append(Component.text("[MODRINTH]").decorate(TextDecoration.BOLD).color(TextColor.color(5635925)).clickEvent(ClickEvent.openUrl("https://modrinth.com/plugin/watut-plugin")).hoverEvent(HoverEvent.showText(Component.text("Click!")))).decorate(TextDecoration.UNDERLINED));
        }

    }
}
