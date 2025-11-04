package org.me.xaeroFlowSpoofer;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.me.xaeroFlowSpoofer.commands.ReloadCommand;

/**
 * XaeroFlowSpoofer
 * ----------------
 * Lightweight and configurable ProtocolLib-based visual spoofing plugin.
 * Designed for stability, minimal packet overhead, and runtime configurability.
 */
public final class XaeroFlowSpoofer extends JavaPlugin {

    private ProtocolManager protocolManager;
    private PacketListener packetListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadProtocolLib();
        registerListeners();
        registerCommands();

        getLogger().info("✅ XaeroFlowSpoofer enabled successfully.");
    }

    @Override
    public void onDisable() {
        if (packetListener != null) {
            packetListener.unregister();
            packetListener = null;
        }
        getLogger().info("❎ XaeroFlowSpoofer disabled.");
    }

    /**
     * Reloads the plugin configuration and reapplies settings dynamically.
     */
    public void reloadPluginConfig() {
        reloadConfig();
        if (packetListener != null) {
            packetListener.applyConfig(getConfig());
        }
        getLogger().info("🔄 Configuration reloaded and applied.");
    }

    /**
     * Initializes and verifies ProtocolLib.
     */
    private void loadProtocolLib() {
        try {
            this.protocolManager = ProtocolLibrary.getProtocolManager();
        } catch (Exception e) {
            getLogger().severe("❌ Failed to initialize ProtocolLib. Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    /**
     * Registers the packet listener for spoofing logic.
     */
    private void registerListeners() {
        this.packetListener = new PacketListener(this, protocolManager);
        this.packetListener.register();
    }

    /**
     * Registers all plugin commands.
     */
    private void registerCommands() {
        if (getCommand("xfsreload") != null) {
            getCommand("xfsreload").setExecutor(new ReloadCommand(this));
        } else {
            getLogger().warning("⚠️ Command 'xfsreload' not defined in plugin.yml.");
        }
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
