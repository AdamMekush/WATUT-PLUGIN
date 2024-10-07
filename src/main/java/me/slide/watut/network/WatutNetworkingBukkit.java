package me.slide.watut.network;

import io.netty.buffer.Unpooled;
import me.slide.watut.WatutPlugin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WatutNetworkingBukkit {

    public static final String NBT_PACKET_ID = "watut:nbt";

    public static String NBTDataPlayerUUID = "playerUuid";
    public static String NBTDataPlayerGuiStatus = "playerGuiStatus";
    public static String NBTDataPlayerChatStatus = "playerChatStatus";
    public static String NBTDataPlayerTypingAmp = "playerTypingAmp";
    public static String NBTDataPlayerScreenRenderCalls = "screenRenderCalls";
    public static String NBTDataPlayerIdleTicks = "playerIdleTicks";
    //a bit of a heavy way to sync a server config to client, but itll do for now
    public static String NBTDataPlayerTicksToGoIdle = "playerTicksToGoIdle";
    public static String NBTDataPlayerMouseX = "playerMouseX";
    public static String NBTDataPlayerMouseY = "playerMouseY";
    public static String NBTDataPlayerMousePressed = "playerMousePressed";

    public static void serverSendToClientAll(CompoundTag nbt) {
        FriendlyByteBuf send = new FriendlyByteBuf(Unpooled.buffer(256));
        send.writeNbt(nbt);

        int packetSize = send.readableBytes();
        byte[] data = new byte[packetSize];
        send.getBytes(0, data);

        //Bukkit.getLogger().info(String.format("Sending packet with size: " + send.array().length + "b / " + send.array().length/1024 + "kb" + nbt.toString()));
        Bukkit.getOnlinePlayers().forEach(player -> player.sendPluginMessage(WatutPlugin.getInstance(), WatutNetworkingBukkit.NBT_PACKET_ID, data));
    }

    public static void serverSendToClientPlayer(CompoundTag nbt, Player player) {
        FriendlyByteBuf send = new FriendlyByteBuf(Unpooled.buffer(0));
        send.writeNbt(nbt);

        int packetSize = send.readableBytes();
        byte[] data = new byte[packetSize];
        send.getBytes(0, data);

        player.sendPluginMessage(WatutPlugin.getInstance(), WatutNetworkingBukkit.NBT_PACKET_ID, data);
    }

    public static void serverSendToClientNear(CompoundTag nbt, Location location, int i, World world) {
        FriendlyByteBuf send = new FriendlyByteBuf(Unpooled.buffer());
        send.writeNbt(nbt);

        int packetSize = send.readableBytes();
        byte[] data = new byte[packetSize];
        send.getBytes(0, data);

        //player.sendPluginMessage(Watut.getInstance(), WatutNetworking.NBT_PACKET_ID, send.array());
        //TEMP
        Bukkit.getOnlinePlayers().forEach(player -> player.sendPluginMessage(WatutPlugin.getInstance(), WatutNetworkingBukkit.NBT_PACKET_ID, data));
    }
}
