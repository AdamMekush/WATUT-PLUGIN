package me.slide.watut;

import me.slide.watut.network.WatutNetworkingBukkit;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

public class PlayerStatusManagerServer extends PlayerStatusManager implements Listener {

    public void tickPlayer(Player player) {
        getStatus(player).setTicksToMarkPlayerIdleSyncedForClient(WatutPlugin.getInstance().getConfig().getInt("idle-ticks"));
    }

    public void receiveAny(Player player, CompoundBinaryTag data) {
        data = CompoundBinaryTag.builder().put(data).putString(WatutNetworkingBukkit.NBTDataPlayerUUID, player.getUniqueId().toString()).build();

        if (data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerGuiStatus)) {
            PlayerGuiState playerGuiState = PlayerGuiState.get(data.getInt(WatutNetworkingBukkit.NBTDataPlayerGuiStatus));
            getStatus(player).setPlayerGuiState(playerGuiState);
        }

        if (data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerChatStatus)) {
            PlayerChatState state = PlayerChatState.get(data.getInt(WatutNetworkingBukkit.NBTDataPlayerChatStatus));
            getStatus(player).setPlayerChatState(state);
        }

        if (data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerIdleTicks)) {
            handleIdleState(player, data.getInt(WatutNetworkingBukkit.NBTDataPlayerIdleTicks));
            data = CompoundBinaryTag.builder().put(data).putInt(WatutNetworkingBukkit.NBTDataPlayerTicksToGoIdle, WatutPlugin.getInstance().getConfig().getInt("idle-ticks")).build();
        }

        //if (data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerMouseX)) {
        //    float x = data.getFloat(WatutNetworkingBukkit.NBTDataPlayerMouseX);
        //    float y = data.getFloat(WatutNetworkingBukkit.NBTDataPlayerMouseY);
        //    boolean pressed = data.getBoolean(WatutNetworkingBukkit.NBTDataPlayerMousePressed);
        //    setMouse(player.getUniqueId(), x, y, pressed);
        //}

        if (data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerScreenRenderCalls)) {}

        getStatus(player).mergeNbtCache(data);

        if (data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerGuiStatus) || data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerIdleTicks) || data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerScreenRenderCalls)) {
            WatutNetworkingBukkit.serverSendToClientAll(data);
        } else {
            WatutNetworkingBukkit.serverSendToClientNear(data, player.getLocation(), nearbyPlayerDataSendDist, player.getWorld());
        }
    }

    public void handleIdleState(Player player, int idleTicks) {
        PlayerStatus status = getStatus(player);
        if (Bukkit.getOnlinePlayers().size() > 1) {
            if (idleTicks > WatutPlugin.getInstance().getConfig().getInt("idle-ticks")) {
                if (!status.isIdle()) {
                    Bukkit.getServer().broadcastMessage(player.getDisplayName() + " has gone idle");
                }
            } else {
                if (status.isIdle()) {
                    Bukkit.getServer().broadcastMessage(player.getDisplayName() + " is no longer idle");
                }
            }
        }
        status.setTicksSinceLastAction(idleTicks);
    }

    @EventHandler
    public void playerLoggedIn(PlayerJoinEvent event){
        for (Map.Entry<UUID, PlayerStatus> entry : lookupPlayerToStatus.entrySet()) {
            WatutNetworkingBukkit.serverSendToClientPlayer(entry.getValue().getNbtCache(), event.getPlayer());
        }
    }
}
