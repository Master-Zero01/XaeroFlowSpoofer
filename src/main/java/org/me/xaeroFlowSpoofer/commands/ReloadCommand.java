package org.First.pumpkinRitualDupe.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.First.pumpkinRitualDupe.PumpkinRitualDupe;

import org.jetbrains.annotations.NotNull;

/**
 * Command to reload the PumpkinRitualDupe config.
 */
public class ReloadCommand implements CommandExecutor {

    private final PumpkinRitualDupe plugin;

    public ReloadCommand(PumpkinRitualDupe plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull[] args) {
        if (!sender.hasPermission("pumpkindupe.reload") && !sender.isOp()) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        plugin.reloadConfig();
        plugin.loadConfigSettings();
        sender.sendMessage(plugin.getMessage("config-reloaded"));
        return true;
    }
}
