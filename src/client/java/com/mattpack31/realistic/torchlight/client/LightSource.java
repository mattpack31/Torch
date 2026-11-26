package com.mattpack31.realistic.torchlight.client;

public record LightSource(int x, int y, int z, int level) {
public double distanceSq(double px, double py, double pz) {
double dx = x + 0.5 - px;
double dy = y + 0.5 - py;
double dz = z + 0.5 - pz;
return dx * dx + dy * dy + dz * dz;
}
}
