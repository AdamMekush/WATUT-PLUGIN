package me.slide.watut;

import java.util.*;

public enum PlayerGuiState {

    NONE,
    CHAT_SCREEN,
    INVENTORY,
    CRAFTING,
    ESCAPE,
    EDIT_SIGN,
    EDIT_BOOK,
    CHEST,
    ENCHANTING_TABLE,
    ANVIL,
    BEACON,
    BREWING_STAND,
    DISPENSER,
    FURNACE,
    GRINDSTONE,
    HOPPER,
    HORSE,
    LOOM,
    VILLAGER,
    COMMAND_BLOCK,
    MISC;

    private static final Map<Integer, PlayerGuiState> lookup = new HashMap<>();
    private static final List<PlayerGuiState> listPointingGuis = new ArrayList<>();
    private static final List<PlayerGuiState> listTypingGuis = new ArrayList<>();
    private static final List<PlayerGuiState> listSoundMakerGuis = new ArrayList<>();

    static {
        for (PlayerGuiState e : EnumSet.allOf(PlayerGuiState.class)) {
            lookup.put(e.ordinal(), e);
            listPointingGuis.add(e);
            listSoundMakerGuis.add(e);
        }
        listPointingGuis.remove(NONE);
        listPointingGuis.remove(CHAT_SCREEN);
        listPointingGuis.remove(EDIT_BOOK);
        listPointingGuis.remove(EDIT_SIGN);
        listPointingGuis.remove(COMMAND_BLOCK);

        listTypingGuis.add(CHAT_SCREEN);
        listTypingGuis.add(EDIT_BOOK);
        listTypingGuis.add(EDIT_SIGN);
        listTypingGuis.add(COMMAND_BLOCK);

        listSoundMakerGuis.remove(NONE);
        listSoundMakerGuis.remove(CHAT_SCREEN);
        listSoundMakerGuis.remove(CHEST);
    }

    public static PlayerGuiState get(int intValue) {
        return lookup.get(intValue);
    }
}
