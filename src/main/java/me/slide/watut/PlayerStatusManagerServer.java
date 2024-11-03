package me.slide.watut;

import me.clip.placeholderapi.PlaceholderAPI;
import me.slide.watut.network.WatutNetworkingBukkit;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.Map;
import java.util.UUID;

public class PlayerStatusManagerServer extends PlayerStatusManager implements Listener {

    public void tickPlayer(Player player) {
        getStatus(player).setTicksToMarkPlayerIdleSyncedForClient(WatutPlugin.getInstance().getConfiguration().getIdleTicks());
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
            if(!isVanished(player)){
                handleIdleState(player, data.getInt(WatutNetworkingBukkit.NBTDataPlayerIdleTicks));
            }
            data = CompoundBinaryTag.builder().put(data).putInt(WatutNetworkingBukkit.NBTDataPlayerTicksToGoIdle, WatutPlugin.getInstance().getConfiguration().getIdleTicks()).build();
        }

        if (data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerMouseX)) {
            float x = data.getFloat(WatutNetworkingBukkit.NBTDataPlayerMouseX);
            float y = data.getFloat(WatutNetworkingBukkit.NBTDataPlayerMouseY);
            boolean pressed = data.getBoolean(WatutNetworkingBukkit.NBTDataPlayerMousePressed);
            setMouse(player.getUniqueId(), x, y, pressed);
        }

        if (data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerScreenRenderCalls)) {}

        getStatus(player).mergeNbtCache(data);

        if (data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerGuiStatus) || data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerIdleTicks) || data.keySet().contains(WatutNetworkingBukkit.NBTDataPlayerScreenRenderCalls)) {
            WatutNetworkingBukkit.serverSendToClientAll(data);
        } else {
            WatutNetworkingBukkit.serverSendToClientNear(data, player.getLocation(), nearbyPlayerDataSendDist, player.getWorld());
        }
    }

    public void handleIdleState(Player player, Integer idleTicks) {
        PlayerStatus status = getStatus(player);
        if(WatutPlugin.getInstance().getConfiguration().isBroadcastEnabled()){
            if (WatutPlugin.getInstance().getServer().getOnlinePlayers().size() - getNumberOfVanishedPlayers() > 1) {
                if (idleTicks > WatutPlugin.getInstance().getConfiguration().getIdleTicks()) {
                    if (!status.isIdle()) {
                        String text = WatutPlugin.getInstance().getConfiguration().getIdleMessage();
                        if(WatutPlugin.getInstance().isPlaceholderApiEnabled()){
                            text = PlaceholderAPI.setPlaceholders(player, text);
                        }
                        else {
                            text = text.replace("%player_name%", player.getName());
                        }
                        Component message = MiniMessage.miniMessage().deserialize(text);
                        WatutPlugin.getInstance().adventure().players().sendMessage(message);
                    }
                } else {
                    if (status.isIdle()) {
                        String text = WatutPlugin.getInstance().getConfiguration().getBusyMessage();
                        if(WatutPlugin.getInstance().isPlaceholderApiEnabled()){
                            text = PlaceholderAPI.setPlaceholders(player, text);
                        }
                        else {
                            text = text.replace("%player_name%", player.getName());
                        }
                        Component message = MiniMessage.miniMessage().deserialize(text);
                        WatutPlugin.getInstance().adventure().players().sendMessage(message);
                    }
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

    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    private int getNumberOfVanishedPlayers(){
        return WatutPlugin.getInstance().getServer().getOnlinePlayers().stream().filter(this::isVanished).distinct().toArray().length;
    }
}
