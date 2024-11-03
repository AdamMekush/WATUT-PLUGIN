package me.slide.watut;

import me.slide.watut.command.ReloadCommand;
import me.slide.watut.config.Config;
import me.slide.watut.network.WatutNetworkingBukkit;
import me.slide.watut.util.UpdateChecker;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static me.slide.watut.util.Version.isAbove1_20_2OrEqual;

public final class WatutPlugin extends JavaPlugin implements PluginMessageListener {

    private final PlayerStatusManagerServer playerStatusManagerServer = new PlayerStatusManagerServer();
    public Config config;
    private boolean isPlaceholderApiEnabled = false;
    private BukkitAudiences adventure;
    private final UpdateChecker updateChecker = new UpdateChecker(this).setModrinthProjectId("watut-plugin");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigurationSerialization.registerClass(Config.class);
        config = Config.deserialize(getConfig().getValues(true));

        this.adventure = BukkitAudiences.create(this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            isPlaceholderApiEnabled = true;
        }

        this.getServer().getMessenger().registerIncomingPluginChannel(this, WatutNetworkingBukkit.NBT_PACKET_ID, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, WatutNetworkingBukkit.NBT_PACKET_ID);

        this.getServer().getPluginManager().registerEvents(playerStatusManagerServer, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> getPlayerStatusManagerServer().tickPlayer(player));
            }
        }.runTaskTimer(this, 0L, 1L);

        ReloadCommand reloadCommand = new ReloadCommand();
        getServer().getPluginCommand("watut").setExecutor(reloadCommand);
        getServer().getPluginCommand("watut").setTabCompleter(reloadCommand);

        Metrics metrics = new Metrics(this, 23632);
        if(config.checkUpdates()){
            updateChecker.checkForUpdatesModrinth()
                    .thenAccept(isOutdated -> {
                        if (isOutdated) {
                            adventure.console().sendMessage(updateChecker.getUpdateMessage().appendSpace().append(Component.text("https://modrinth.com/plugin/watut-plugin").color(TextColor.color(5635925))));
                        }
                    });
        }
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] bytes) {
        try {
            CompoundBinaryTag nbt;
            ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(bytes);
            if(isAbove1_20_2OrEqual()){
                nbt = BinaryTagIO.unlimitedReader().readNameless(byteArrayOutputStream);
            }
            else {
                nbt = BinaryTagIO.readInputStream(byteArrayOutputStream);
            }
            playerStatusManagerServer.receiveAny(player, nbt);
        } catch (IOException e) {
            getLogger().severe(e.toString());
        }
    }

    public PlayerStatusManagerServer getPlayerStatusManagerServer(){
        return playerStatusManagerServer;
    }

    public boolean isPlaceholderApiEnabled() {
        return isPlaceholderApiEnabled;
    }

    public Config getConfiguration() {
        return config;
    }

    public void setConfiguration(Config config){
        this.config = config;
    }

    public static WatutPlugin getInstance(){
        return getPlugin(WatutPlugin.class);
    }

    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
}
