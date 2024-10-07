package me.slide.watut;

import io.netty.buffer.Unpooled;
import me.slide.watut.network.WatutNetworkingBukkit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

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
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] bytes) {
        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes));
        CompoundTag compoundTag = friendlyByteBuf.readNbt();
        compoundTag.putString("playerUuid", player.getUniqueId().toString());
        playerStatusManagerServer.receiveAny(player, compoundTag);
    }

    public static WatutPlugin getInstance(){
        return getPlugin(WatutPlugin.class);
    }

    public PlayerStatusManagerServer getPlayerStatusManagerServer(){
        return playerStatusManagerServer;
    }

}
