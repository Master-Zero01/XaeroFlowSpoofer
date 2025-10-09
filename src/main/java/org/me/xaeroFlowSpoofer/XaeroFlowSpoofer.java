package org.me.xaeroFlowSpoofer;

import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;

public class XaeroFlowSpoofer extends JavaPlugin {

    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        XaeroFlowSpoofer ProtocolLibrary = new XaeroFlowSpoofer();
        protocolManager = ProtocolLibrary.getProtocolManager();
        new PacketListener(this, protocolManager).register();
        getLogger().info("XaeroFlowSpoofer enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("XaeroFlowSpoofer disabled.");
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
