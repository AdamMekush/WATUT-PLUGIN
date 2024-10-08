package me.slide.watut;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum PlayerChatState {

    NONE,
    CHAT_FOCUSED,
    CHAT_TYPING;

    private static final Map<Integer, PlayerChatState> lookup = new HashMap<>();

    static {
        for (PlayerChatState e : EnumSet.allOf(PlayerChatState.class)) {
            lookup.put(e.ordinal(), e);
        }
    }

    public static PlayerChatState get(int intValue) {
        return lookup.get(intValue);
    }
}
