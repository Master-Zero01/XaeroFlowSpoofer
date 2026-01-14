package org.me.xaeroFlowSpoofer;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.me.xaeroFlowSpoofer.commands.ReloadCommand;

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

    public void reloadPluginConfig() {
        reloadConfig();
        if (packetListener != null) {
            packetListener.unregister();
            packetListener = new PacketListener(this, protocolManager); // reinitialize with new config
            packetListener.register();
        }
        getLogger().info("🔄 Configuration reloaded and applied.");
    }

    private void loadProtocolLib() {
        try {
            protocolManager = ProtocolLibrary.getProtocolManager();
        } catch (Exception e) {
            getLogger().severe("❌ Failed to initialize ProtocolLib. Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void registerListeners() {
        packetListener = new PacketListener(this, protocolManager);
        packetListener.register();
    }

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
