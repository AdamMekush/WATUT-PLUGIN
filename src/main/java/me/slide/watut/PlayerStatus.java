package me.slide.watut;

import net.minecraft.nbt.CompoundTag;

import java.util.*;

public class PlayerStatus {

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

    private int ticksSinceLastAction = 0;
    private int ticksToMarkPlayerIdleSyncedForClient = 20*60*5;

    private CompoundTag nbtCache = new CompoundTag();

    public PlayerStatus(PlayerGuiState playerGuiState) {
    }

    public void setPlayerGuiState(PlayerGuiState playerGuiState) {
    }

    public void setScreenPosPercentX(float screenPosPercentX) {
    }

    public void setScreenPosPercentY(float screenPosPercentY) {
    }

    public void setPressing(boolean pressing) {
    }

    public void setTicksSinceLastAction(int ticksSinceLastAction) {
        this.ticksSinceLastAction = ticksSinceLastAction;
    }

    public boolean isIdle() {
        return ticksSinceLastAction > ticksToMarkPlayerIdleSyncedForClient;
    }

    public CompoundTag getNbtCache() {
        return nbtCache;
    }

    public void setTicksToMarkPlayerIdleSyncedForClient(int ticksToMarkPlayerIdleSyncedForClient) {
        this.ticksToMarkPlayerIdleSyncedForClient = ticksToMarkPlayerIdleSyncedForClient;
    }

    public void setPlayerChatState(PlayerChatState playerChatState) {
    }
}