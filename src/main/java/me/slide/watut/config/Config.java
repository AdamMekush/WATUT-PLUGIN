package me.slide.watut.config;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Config")
public class Config implements ConfigurationSerializable {
    private Integer version;
    private Boolean update;
    private Integer idleTicks;
    private Boolean broadcastEnabled;
    private String idleMessage;
    private String busyMessage;

    public Config(int version, boolean update, int idleTicks, boolean broadcastEnabled, String idleMessage, String busyMessage) {
        this.version = version;
        this.update = update;
        this.idleTicks = idleTicks;
        this.broadcastEnabled = broadcastEnabled;
        this.idleMessage = idleMessage;
        this.busyMessage = busyMessage;
    }

    public Integer getVersion() { return version; }
    public Boolean checkUpdates() { return update; }
    public Integer getIdleTicks() { return idleTicks; }
    public Boolean isBroadcastEnabled() { return broadcastEnabled; }
    public String getIdleMessage() { return idleMessage; }
    public String getBusyMessage() { return busyMessage; }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("version", version);
        data.put("update", update);
        data.put("idle.ticks", idleTicks);
        data.put("idle.broadcast.enabled", broadcastEnabled);
        data.put("idle.broadcast.message.idle", idleMessage);
        data.put("idle.broadcast.message.busy", busyMessage);
        return data;
    }

    public static Config deserialize(Map<String, Object> data) {
        return new Config(
                (Integer) data.get("version"),
                (Boolean) data.get("update"),
                (Integer) data.get("idle.ticks"),
                (Boolean) data.get("idle.broadcast.enabled"),
                (String) data.get("idle.broadcast.message.idle"),
                (String) data.get("idle.broadcast.message.busy")
        );
    }
}