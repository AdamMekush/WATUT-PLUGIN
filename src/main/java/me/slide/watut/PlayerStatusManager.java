package me.slide.watut;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStatusManager {

    public HashMap<UUID, PlayerStatus> lookupPlayerToStatus = new HashMap<>();

    protected int nearbyPlayerDataSendDist = 10;

    public PlayerStatus getStatus(Player player) {
        return getStatus(player.getUniqueId());
    }

    public PlayerStatus getStatus(UUID uuid) {
        PlayerStatus status = lookupPlayerToStatus.get(uuid);
        if (status == null) {
            status = new PlayerStatus(PlayerGuiState.NONE);
            lookupPlayerToStatus.put(uuid, status);
        }
        return status;
    }

    public void setMouse(UUID uuid, float x, float y, boolean pressed) {
        PlayerStatus status = getStatus(uuid);
        status.setScreenPosPercentX(x);
        status.setScreenPosPercentY(y);
        status.setPressing(pressed);
    }

}