package org.me.xaeroFlowSpoofer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Random;

/**
 * Highly optimized PacketListener for spoofing block visuals.
 * - Directly modifies outgoing block data packets.
 * - Lightweight and thread-safe — avoids Netty re-entry.
 * - Configurable behavior for spoofing and terrain variation.
 */
public final class PacketListener {

    private final Plugin plugin;
    private final ProtocolManager protocolManager;
    private final Random random = new Random();

    // Configurable options
    private final double spoofChance;
    private final boolean enableTerrain;
    private final double terrainNoiseChance;

    public PacketListener(Plugin plugin, ProtocolManager protocolManager) {
        this.plugin = plugin;
        this.protocolManager = protocolManager;

        // Load configuration values safely
        FileConfiguration config = plugin.getConfig();
        this.spoofChance = config.getDouble("spoof-chance", 0.15);
        this.enableTerrain = config.getBoolean("enable-fake-terrain", true);
        this.terrainNoiseChance = config.getDouble("terrain-noise-chance", 0.08);
    }

    /**
     * Registers the packet listener for multi-block changes.
     */
    public void register() {
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.isCancelled()) return;

                PacketContainer packet = event.getPacket().deepClone();
                WrappedBlockData[] blockDataArray = packet.getBlockDataArrays().readSafely(0);

                if (blockDataArray == null || blockDataArray.length == 0) return;

                boolean modified = false;

                for (int i = 0; i < blockDataArray.length; i++) {
                    Material type = blockDataArray[i].getType();

                    // --- Fluid spoofing ---
                    if ((type == Material.WATER || type == Material.LAVA) && random.nextDouble() < spoofChance) {
                        blockDataArray[i] = WrappedBlockData.createData(Material.AIR);
                        modified = true;
                        continue;
                    }

                    // --- Fake terrain variation ---
                    if (enableTerrain && random.nextDouble() < terrainNoiseChance) {
                        blockDataArray[i] = WrappedBlockData.createData(getRandomSurfaceMaterial());
                        modified = true;
                    }
                }

                if (modified) {
                    packet.getBlockDataArrays().write(0, blockDataArray);
                    event.setPacket(packet); // safely replace outgoing packet
                }
            }
        });
    }

    /**
     * Picks a random surface material for terrain noise.
     */
    private Material getRandomSurfaceMaterial() {
        return switch (random.nextInt(4)) {
            case 1 -> Material.DIRT;
            case 2 -> Material.COARSE_DIRT;
            default -> Material.GRASS_BLOCK;
        };
    }

    /**
     * Unregisters all listeners for this plugin.
     */
    public void unregister() {
        protocolManager.removePacketListeners(plugin);
    }

    /**
     * Applies new configuration settings dynamically (if needed).
     */
    public void applyConfig(FileConfiguration config) {
        // Future use: dynamic config reloads
    }
}
