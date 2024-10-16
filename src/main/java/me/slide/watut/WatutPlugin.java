package me.slide.watut;

import me.slide.watut.network.WatutNetworkingBukkit;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class WatutPlugin extends JavaPlugin implements PluginMessageListener {

    private final PlayerStatusManagerServer playerStatusManagerServer = new PlayerStatusManagerServer();

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, 23632);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, WatutNetworkingBukkit.NBT_PACKET_ID, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, WatutNetworkingBukkit.NBT_PACKET_ID);

        this.getServer().getPluginManager().registerEvents(new PlayerStatusManagerServer(), this);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> getPlayerStatusManagerServer().tickPlayer(player));
            }
        }.runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable() {
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

    public static WatutPlugin getInstance(){
        return getPlugin(WatutPlugin.class);
    }

    public PlayerStatusManagerServer getPlayerStatusManagerServer(){
        return playerStatusManagerServer;
    }

    public static boolean isAbove1_20_2OrEqual(){
        String version = Bukkit.getBukkitVersion();
        String[] versionSplit = version.split("\\.");
        int major = Integer.parseInt(versionSplit[0]);
        int minor = Integer.parseInt(versionSplit[1]);
        return (major > 1 || (major == 1 && minor >= 20 && version.contains("2")));
    }

}
