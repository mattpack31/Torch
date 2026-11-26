package com.mattpack31.realistic.torchlight.client;

import com.mattpack31.realistic.torchlight.RealisticTorchlight;
import com.mattpack31.realistic.torchlight.TorchConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class QualityKey {
private static KeyMapping cycleKey;

private QualityKey() {}

public static void register() {
cycleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
"key.realistic-torchlight.cycle",
InputConstants.Type.KEYSYM,
InputConstants.KEY_K,
KeyMapping.Category.MISC));
}

public static void tick(Minecraft client) {
while (cycleKey.consumeClick()) {
TorchConfig cfg = TorchConfig.get();
cfg.quality = cfg.quality.next();
cfg.save();
RealisticTorchlight.LOGGER.info("[Torch] Calidad: {}", cfg.quality.displayName);
if (client.player != null) {
client.player.sendOverlayMessage(Component.literal("Torch: " + cfg.quality.displayName));
}
}
}
}
