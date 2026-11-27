package com.mattpack31.realistic.torchlight.client;

import com.mattpack31.realistic.torchlight.RealisticTorchlight;
import com.mattpack31.realistic.torchlight.TorchConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class RealisticTorchlightClient implements ClientModInitializer {
@Override
public void onInitializeClient() {
TorchConfig cfg = TorchConfig.get();
RealisticTorchlight.LOGGER.info("[Torch] Cliente listo. Calidad: {}, luces max: {}",
cfg.quality.displayName, cfg.quality.maxLights);
TorchPipelines.init();
QualityKey.register();
ClientTickEvents.END_CLIENT_TICK.register(LightScanner::tick);
ClientTickEvents.END_CLIENT_TICK.register(QualityKey::tick);
}
}
