package me.slide.watut;

import me.slide.watut.network.WatutNetworkingBukkit;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
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
            ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(bytes);
            CompoundBinaryTag nbt = BinaryTagIO.unlimitedReader().readNameless(byteArrayOutputStream);
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

}
