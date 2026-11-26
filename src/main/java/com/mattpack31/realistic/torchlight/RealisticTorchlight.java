package com.mattpack31.realistic.torchlight;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealisticTorchlight implements ModInitializer {
public static final String MOD_ID = "realistic-torchlight";
public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

@Override
public void onInitialize() {
LOGGER.info("[Torch] Mod cargado (Fabric 26.2)");
}

public static Identifier id(String path) {
return Identifier.fromNamespaceAndPath(MOD_ID, path);
}
}
