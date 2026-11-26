package com.mattpack31.realistic.torchlight;

public enum QualityLevel {
    POTATO("Potato", 3, false, false, false, false, 20),
    LOW("Baja", 4, true, false, false, false, 10),
    MEDIUM("Media", 8, true, true, false, false, 10),
    HIGH("Alta", 12, true, true, true, false, 5),
    MAX("Maxima", 16, true, true, true, true, 5);

    public final String displayName;
    public final int maxLights;
    public final boolean flicker;
    public final boolean cornerDarkening;
    public final boolean contactShadows;
    public final boolean voxelShadows;
    public final int scanIntervalTicks;

    QualityLevel(String displayName, int maxLights, boolean flicker,
                 boolean cornerDarkening, boolean contactShadows,
                 boolean voxelShadows, int scanIntervalTicks) {
        this.displayName = displayName;
        this.maxLights = maxLights;
        this.flicker = flicker;
        this.cornerDarkening = cornerDarkening;
        this.contactShadows = contactShadows;
        this.voxelShadows = voxelShadows;
        this.scanIntervalTicks = scanIntervalTicks;
    }

    public QualityLevel next() {
        QualityLevel[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public QualityLevel lower() {
        return ordinal() == 0 ? this : values()[ordinal() - 1];
    }
}
