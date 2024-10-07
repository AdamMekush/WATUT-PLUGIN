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
    public static String NBTDataPlayerTicksToGoIdle = "playerTicksToGoIdle";
    public static String NBTDataPlayerMouseX = "playerMouseX";
    public static String NBTDataPlayerMouseY = "playerMouseY";
    public static String NBTDataPlayerMousePressed = "playerMousePressed";

    public static void serverSendToClientAll(CompoundTag nbt) {
        FriendlyByteBuf send = new FriendlyByteBuf(Unpooled.buffer(0));
        send.writeNbt(nbt);

        int packetSize = send.readableBytes();
        byte[] data = new byte[packetSize];
        send.getBytes(0, data);

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

        //TEMP
        Bukkit.getOnlinePlayers().forEach(player -> player.sendPluginMessage(WatutPlugin.getInstance(), WatutNetworkingBukkit.NBT_PACKET_ID, data));
    }
}
