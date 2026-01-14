package org.me.xaeroFlowSpoofer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.Random;

/**
 * Efficient PacketListener for XaeroFlowSpoofer with configurable tick throttling.
 */
public final class PacketListener {

    private final Plugin plugin;
    private final ProtocolManager protocolManager;
    private final Random random = new Random();

    private final double spoofChance;
    private final boolean enableTerrain;
    private final double terrainNoiseChance;
    private final int maxChangesPerTick;

    public PacketListener(Plugin plugin, ProtocolManager protocolManager) {
        this.plugin = plugin;
        this.protocolManager = protocolManager;

        // Load configuration safely
        this.spoofChance = plugin.getConfig().getDouble("spoof-chance", 0.12);
        this.enableTerrain = plugin.getConfig().getBoolean("enable-fake-terrain", true);
        this.terrainNoiseChance = plugin.getConfig().getDouble("terrain-noise-chance", 0.08);
        this.maxChangesPerTick = plugin.getConfig().getInt("max-changes-per-tick", 5);
    }

    public void register() {
        protocolManager.addPacketListener(new PacketAdapter(plugin,
                PacketType.Play.Server.MULTI_BLOCK_CHANGE) {

            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.isCancelled()) return;

                PacketContainer packet = event.getPacket();
                WrappedBlockData[] blocks = packet.getBlockDataArrays().readSafely(0);
                if (blocks == null || blocks.length == 0) return;

                int changes = 0;

                for (int i = 0; i < blocks.length && changes < maxChangesPerTick; i++) {
                    Material type = blocks[i].getType();

                    // --- Fluid spoofing ---
                    if ((type == Material.WATER || type == Material.LAVA) && random.nextDouble() < spoofChance) {
                        blocks[i] = WrappedBlockData.createData(Material.AIR);
                        changes++;
                        continue;
                    }

                    // --- Optional terrain noise ---
                    if (enableTerrain && random.nextDouble() < terrainNoiseChance) {
                        blocks[i] = WrappedBlockData.createData(getRandomSurfaceMaterial());
                        changes++;
                    }
                }

                if (changes > 0) {
                    packet.getBlockDataArrays().write(0, blocks);
                    event.setPacket(packet);
                }
            }
        });
    }

    private Material getRandomSurfaceMaterial() {
        return switch (random.nextInt(4)) {
            case 1 -> Material.DIRT;
            case 2 -> Material.COARSE_DIRT;
            default -> Material.GRASS_BLOCK;
        };
    }

    public void unregister() {
        protocolManager.removePacketListeners(plugin);
    }

    /** Apply new config dynamically (for /xfsreload) */
    public void applyConfig() {
        // Currently no dynamic reload needed; can be extended
    }
}
