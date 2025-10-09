package org.me.xaeroFlowSpoofer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PacketListener {

    private final Plugin plugin;
    private final ProtocolManager protocolManager;
    private final Random rng = new Random();

    private final double spoofChance;
    private final boolean onlyChunkEdges;
    private final int edgeDistance;

    private final boolean enableTerrain;
    private final double terrainNoiseChance;
    private final int terrainMaxEdits;

    // Tick throttle for fake terrain (every N ticks)
    private int tickCounter = 0;
    private static final int TICKS_BETWEEN_FAKE_TERRAIN = 5;

    public PacketListener(Plugin plugin, ProtocolManager protocolManager) {
        this.plugin = plugin;
        this.protocolManager = protocolManager;

        spoofChance = plugin.getConfig().getDouble("spoof-chance", 0.15);
        onlyChunkEdges = plugin.getConfig().getBoolean("only-chunk-edges", true);
        edgeDistance = plugin.getConfig().getInt("edge-distance", 1);

        enableTerrain = plugin.getConfig().getBoolean("enable-fake-terrain", true);
        terrainNoiseChance = plugin.getConfig().getDouble("terrain-noise-chance", 0.08);
        terrainMaxEdits = plugin.getConfig().getInt("terrain-max-edits-per-chunk", 50);
    }

    public void register() {
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.MULTI_BLOCK_CHANGE) {

            @Override
            public void onPacketSending(PacketEvent event) {
                handleFluidSpoof(event);

                if (enableTerrain) {
                    tickCounter++;
                    if (tickCounter >= TICKS_BETWEEN_FAKE_TERRAIN) {
                        tickCounter = 0;
                        handleFakeTerrain(event);
                    }
                }
            }
        });
    }

    private void handleFluidSpoof(PacketEvent event) {
        if (event.getPacket().getBlockData() == null) return;

        WrappedBlockData blockData = event.getPacket().getBlockData().read(0);
        Material type = blockData.getType();

        if (!(type == Material.WATER || type == Material.LAVA)) return;
        if (rng.nextDouble() > spoofChance) return;

        int x = event.getPacket().getIntegers().read(0);
        int z = event.getPacket().getIntegers().read(2);

        if (onlyChunkEdges) {
            int localX = x & 0xF;
            int localZ = z & 0xF;
            if (!(localX < edgeDistance || localX > 15 - edgeDistance ||
                    localZ < edgeDistance || localZ > 15 - edgeDistance)) return;
        }

        Material fake = rng.nextBoolean() ? Material.AIR : type;
        event.getPacket().getBlockData().write(0, WrappedBlockData.createData(fake));
    }

    private void handleFakeTerrain(PacketEvent event) {
        int baseX = event.getPacket().getIntegers().read(0) & 0xF;
        int baseZ = event.getPacket().getIntegers().read(2) & 0xF;

        List<WrappedBlockData> blockDataList = new ArrayList<>();
        List<BlockPosition> positionList = new ArrayList<>();
        int edits = 0;

        while (edits < terrainMaxEdits) {
            if (rng.nextDouble() > terrainNoiseChance) break;

            int x = rng.nextInt(16);
            int z = rng.nextInt(16);

            if (onlyChunkEdges && !(x < edgeDistance || x > 15 - edgeDistance ||
                    z < edgeDistance || z > 15 - edgeDistance)) continue;

            int y = rng.nextInt(5) + 63; // approximate surface height
            Material mat = pickRandomSurfaceMaterial();
            WrappedBlockData data = WrappedBlockData.createData(mat);

            BlockPosition pos = new BlockPosition(baseX + x, y, baseZ + z);
            blockDataList.add(data);
            positionList.add(pos);

            edits++;
        }

        if (!blockDataList.isEmpty()) {
            PacketContainer packet =
                    new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
            packet.getBlockDataArrays().write(0, blockDataList.toArray(new WrappedBlockData[0]));
            packet.getBlockDataArrays().write(0, (WrappedBlockData[]) positionList.toArray());

            protocolManager.broadcastServerPacket(packet);
        }
    }

    private Material pickRandomSurfaceMaterial() {
        int pick = rng.nextInt(4);
        return switch (pick) {
            case 1 -> Material.DIRT;
            case 2 -> Material.COARSE_DIRT;
            default -> Material.GRASS_BLOCK;
        };
    }
}
