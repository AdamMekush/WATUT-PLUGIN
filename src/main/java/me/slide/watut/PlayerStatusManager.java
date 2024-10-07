package me.slide.watut;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStatusManager {

    public HashMap<UUID, PlayerStatus> lookupPlayerToStatus = new HashMap<>();

    protected int nearbyPlayerDataSendDist = 10;

    public void tickPlayer(Player player) {

    }

    public PlayerStatus getStatus(Player player) {
        return getStatus(player.getUniqueId());
    }

    public PlayerStatus getStatus(UUID uuid) {
        return getStatus(uuid, false);
    }

    public PlayerStatus getStatus(UUID uuid, boolean local) {
        if (local) return getStatusLocal();
        PlayerStatus status = lookupPlayerToStatus.get(uuid);
        if (status == null) {
            status = new PlayerStatus(PlayerStatus.PlayerGuiState.NONE);
            lookupPlayerToStatus.put(uuid, status);
        }
        return status;
    }

    public PlayerStatus getStatusLocal() {
        return null;
    }

    public void setMouse(UUID uuid, float x, float y, boolean pressed) {
        PlayerStatus status = getStatus(uuid);
        status.setScreenPosPercentX(x);
        status.setScreenPosPercentY(y);
        status.setPressing(pressed);
    }

}