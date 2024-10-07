package me.slide.watut;

import me.slide.watut.network.WatutNetworkingBukkit;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

public class PlayerStatusManagerServer extends PlayerStatusManager implements Listener {

    @Override
    public void tickPlayer(Player player) {
        getStatus(player).setTicksToMarkPlayerIdleSyncedForClient(WatutPlugin.getInstance().getConfig().getInt("idle-ticks"));
        super.tickPlayer(player);
    }

    public void receiveAny(Player player, CompoundTag data) {
        data.putString(WatutNetworkingBukkit.NBTDataPlayerUUID, player.getUniqueId().toString());

        if (data.contains(WatutNetworkingBukkit.NBTDataPlayerGuiStatus)) {
            PlayerStatus.PlayerGuiState playerGuiState = PlayerStatus.PlayerGuiState.get(data.getInt(WatutNetworkingBukkit.NBTDataPlayerGuiStatus));
            getStatus(player).setPlayerGuiState(playerGuiState);
        }

        if (data.contains(WatutNetworkingBukkit.NBTDataPlayerChatStatus)) {
            PlayerStatus.PlayerChatState state = PlayerStatus.PlayerChatState.get(data.getInt(WatutNetworkingBukkit.NBTDataPlayerChatStatus));
            getStatus(player).setPlayerChatState(state);
        }

        if (data.contains(WatutNetworkingBukkit.NBTDataPlayerIdleTicks)) {
            handleIdleState(player, data.getInt(WatutNetworkingBukkit.NBTDataPlayerIdleTicks));
            data.putInt(WatutNetworkingBukkit.NBTDataPlayerTicksToGoIdle, WatutPlugin.getInstance().getConfig().getInt("idle-ticks"));
        }

        if (data.contains(WatutNetworkingBukkit.NBTDataPlayerMouseX)) {
            float x = data.getFloat(WatutNetworkingBukkit.NBTDataPlayerMouseX);
            float y = data.getFloat(WatutNetworkingBukkit.NBTDataPlayerMouseY);
            boolean pressed = data.getBoolean(WatutNetworkingBukkit.NBTDataPlayerMousePressed);
            setMouse(player.getUniqueId(), x, y, pressed);
        }

        if (data.contains(WatutNetworkingBukkit.NBTDataPlayerScreenRenderCalls)) {}

        getStatus(player).getNbtCache().merge(data);

        if (data.contains(WatutNetworkingBukkit.NBTDataPlayerGuiStatus) || data.contains(WatutNetworkingBukkit.NBTDataPlayerIdleTicks) || data.contains(WatutNetworkingBukkit.NBTDataPlayerScreenRenderCalls)) {
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
                    broadcast(player.getDisplayName() + " has gone idle");
                }
            } else {
                if (status.isIdle()) {
                    broadcast(player.getDisplayName() + " is no longer idle");
                }
            }
        }
        status.setTicksSinceLastAction(idleTicks);
    }

    public void broadcast(String msg) {
        Bukkit.getServer().broadcastMessage(msg);
    }

    @EventHandler
    public void playerLoggedIn(PlayerJoinEvent event){
        for (Map.Entry<UUID, PlayerStatus> entry : lookupPlayerToStatus.entrySet()) {
            WatutPlugin.getInstance().getLogger().info("sending update all packet for " + entry.getKey().toString() + " to " + event.getPlayer().getDisplayName() + " with status " + PlayerStatus.PlayerGuiState.get(entry.getValue().getNbtCache().getInt(WatutNetworkingBukkit.NBTDataPlayerGuiStatus)));
            WatutNetworkingBukkit.serverSendToClientPlayer(entry.getValue().getNbtCache(), event.getPlayer());
        }
    }
}
