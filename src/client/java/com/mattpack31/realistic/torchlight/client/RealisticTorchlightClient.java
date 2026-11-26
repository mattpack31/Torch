package com.mattpack31.realistic.torchlight.client;

import com.mattpack31.realistic.torchlight.RealisticTorchlight;
import com.mattpack31.realistic.torchlight.TorchConfig;
import net.fabricmc.api.ClientModInitializer;

public class RealisticTorchlightClient implements ClientModInitializer {
@Override
public void onInitializeClient() {
TorchConfig cfg = TorchConfig.get();
RealisticTorchlight.LOGGER.info("[Torch] Cliente listo. Calidad: {}, luces max: {}",
cfg.quality.displayName, cfg.quality.maxLights);
}
}
