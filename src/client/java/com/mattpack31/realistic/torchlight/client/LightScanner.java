package com.mattpack31.realistic.torchlight.client;

import com.mattpack31.realistic.torchlight.RealisticTorchlight;
import com.mattpack31.realistic.torchlight.TorchConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class LightScanner {
    private static final List<LightSource> CURRENT = new ArrayList<>();
    private static int tickCounter = 0;
    private static int lastCount = -1;

    private LightScanner() {}

    public static List<LightSource> getLights() {
        return Collections.unmodifiableList(CURRENT);
    }

    public static void tick(Minecraft client) {
        if (client.level == null || client.player == null) {
            CURRENT.clear();
            lastCount = -1;
            return;
        }
        TorchConfig cfg = TorchConfig.get();
        tickCounter++;
        if (tickCounter < cfg.quality.scanIntervalTicks) {
            return;
        }
        tickCounter = 0;
        scan(client, cfg);
    }

    private static void scan(Minecraft client, TorchConfig cfg) {
        double px = client.player.getX();
        double py = client.player.getY();
        double pz = client.player.getZ();
        BlockPos origin = client.player.blockPosition();
        int radius = Math.max(2, Math.min((int) cfg.maxRadius, 10));

        List<LightSource> found = new ArrayList<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    pos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    BlockState state = client.level.getBlockState(pos);
                    int level = state.getLightEmission();
                    if (level > 0) {
                        found.add(new LightSource(pos.getX(), pos.getY(), pos.getZ(), level));
                    }
                }
            }
        }

        found.sort(Comparator.comparingDouble(s -> s.distanceSq(px, py, pz)));
        if (found.size() > cfg.quality.maxLights) {
            found = new ArrayList<>(found.subList(0, cfg.quality.maxLights));
        }

        CURRENT.clear();
        CURRENT.addAll(found);
        TorchLightsUbo.update(CURRENT);

        if (CURRENT.size() != lastCount) {
            lastCount = CURRENT.size();
            RealisticTorchlight.LOGGER.info("[Torch] Luces cercanas: {}", lastCount);
        }
    }
}
