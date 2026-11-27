package com.mattpack31.realistic.torchlight.client;

import com.mattpack31.realistic.torchlight.TorchConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class TorchStatus {
    private static int lastLights = -1;
    private static boolean lastUsed = false;

    private TorchStatus() {}

    public static void tick(Minecraft client) {
        if (client.player == null) {
            lastLights = -1;
            return;
        }
        TorchConfig cfg = TorchConfig.get();
        if (!cfg.debugOverlay) {
            return;
        }
        int n = LightScanner.getLights().size();
        boolean used = TorchPipelines.used;
        if (n != lastLights || used != lastUsed) {
            lastLights = n;
            lastUsed = used;
            String msg = "Torch: " + cfg.quality.displayName
                    + " | luces " + n
                    + " | shader " + (used ? "si" : "no");
            client.player.sendOverlayMessage(Component.literal(msg));
        }
    }
}
