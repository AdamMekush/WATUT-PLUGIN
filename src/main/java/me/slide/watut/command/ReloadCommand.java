package me.slide.watut.command;

import me.slide.watut.WatutPlugin;
import me.slide.watut.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String name, @NotNull String[] strings) {
        if(name.equals("watut")){

            if (Arrays.stream(strings).toList().isEmpty()) {
                commandSender.sendMessage("§cUsage: /watut reload");
                return true;
            }

            if(strings[0].equals("reload")){
                if (!commandSender.hasPermission("watut.reload")) {
                    commandSender.sendMessage("§cYou do not have permission to execute this command.");
                    return true;
                }

                Config config = Config.deserialize(WatutPlugin.getInstance().getConfig().getValues(true));
                WatutPlugin.getInstance().setConfiguration(config);
                WatutPlugin.getInstance().reloadConfig();
                commandSender.sendMessage("§2Configuration successfully reloaded!");
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String name, @NotNull String[] strings) {
        List<String> list = new ArrayList<>();
        if(name.equals("watut")){
            list.add("reload");
        }
        return list;
    }
}
