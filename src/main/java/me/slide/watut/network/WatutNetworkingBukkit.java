package me.slide.watut.network;

import me.slide.watut.WatutPlugin;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    public static void serverSendToClientAll(CompoundBinaryTag nbt) {
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            BinaryTagIO.writer().writeNameless(nbt, data);

            Bukkit.getOnlinePlayers().forEach(player -> player.sendPluginMessage(WatutPlugin.getInstance(), WatutNetworkingBukkit.NBT_PACKET_ID, data.toByteArray()));
            data.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void serverSendToClientPlayer(CompoundBinaryTag nbt, Player player) {
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            BinaryTagIO.writer().writeNameless(nbt, data);

            player.sendPluginMessage(WatutPlugin.getInstance(), WatutNetworkingBukkit.NBT_PACKET_ID, data.toByteArray());
            data.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void serverSendToClientNear(CompoundBinaryTag nbt, Location location, int i, World world) {
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            BinaryTagIO.writer().writeNameless(nbt, data);

            world.getNearbyEntities(location, i, i, i).forEach(entity -> {
                if(entity instanceof Player){
                    ((Player) entity).sendPluginMessage(WatutPlugin.getInstance(), WatutNetworkingBukkit.NBT_PACKET_ID, data.toByteArray());
                }
            });
            data.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
