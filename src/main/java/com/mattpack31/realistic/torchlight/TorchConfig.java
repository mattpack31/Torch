package com.mattpack31.realistic.torchlight;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TorchConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("realistic-torchlight");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static TorchConfig instance;

    public QualityLevel quality = QualityLevel.POTATO;
    public float intensity = 1.0f;
    public float maxRadius = 12.0f;
    public boolean flickerEnabled = true;
    public boolean trueDarknessBoost = false;
    public boolean autoLowerQuality = true;
    public int autoLowerFps = 30;

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("realistic-torchlight.json");
    }

    public static TorchConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static TorchConfig load() {
        Path p = path();
        TorchConfig cfg = null;
        try {
            if (Files.exists(p)) {
                cfg = GSON.fromJson(Files.readString(p), TorchConfig.class);
            }
        } catch (Exception e) {
            LOGGER.warn("No se pudo leer la config, uso valores por defecto", e);
        }
        if (cfg == null) {
            cfg = new TorchConfig();
        }
        cfg.clamp();
        cfg.save();
        return cfg;
    }

    private void clamp() {
        if (quality == null) quality = QualityLevel.POTATO;
        intensity = Math.max(0.1f, Math.min(intensity, 3.0f));
        maxRadius = Math.max(4.0f, Math.min(maxRadius, 24.0f));
        autoLowerFps = Math.max(10, Math.min(autoLowerFps, 120));
    }

    public void save() {
        try {
            Files.writeString(path(), GSON.toJson(this));
        } catch (IOException e) {
            LOGGER.warn("No se pudo guardar la config", e);
        }
    }
}
