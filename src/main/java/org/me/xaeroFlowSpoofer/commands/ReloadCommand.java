package org.me.xaeroFlowSpoofer.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.me.xaeroFlowSpoofer.XaeroFlowSpoofer;

/**
 * Handles the /xfsreload command — reloads XaeroFlowSpoofer's configuration and reapplies it.
 */
public class ReloadCommand implements CommandExecutor {

    private final XaeroFlowSpoofer plugin;

    public ReloadCommand(@NotNull XaeroFlowSpoofer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!sender.hasPermission("xaeroflowspoof.reload")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
            return true;
        }

        long start = System.nanoTime();
        plugin.reloadPluginConfig();
        long duration = (System.nanoTime() - start) / 1_000_000L;

        sender.sendMessage(ChatColor.GREEN + "✔ XaeroFlowSpoofer configuration reloaded and applied.");
        sender.sendMessage(ChatColor.GRAY + "Reload completed in " + duration + " ms.");
        return true;
    }
}
