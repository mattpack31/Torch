package com.mattpack31.realistic.torchlight.client;

import com.mattpack31.realistic.torchlight.RealisticTorchlight;
import com.mattpack31.realistic.torchlight.TorchConfig;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.List;

public final class TorchLightsUbo {
public static final int MAX_LIGHTS = 16;
private static final int SIZE;
private static @Nullable GpuBuffer buffer;

static {
Std140SizeCalculator calc = new Std140SizeCalculator().putIVec4();
for (int i = 0; i < MAX_LIGHTS; i++) {
calc.putIVec4();
}
for (int i = 0; i < MAX_LIGHTS; i++) {
calc.putVec4();
}
SIZE = calc.get();
}

private TorchLightsUbo() {}

public static @Nullable GpuBuffer get() {
return buffer;
}

public static void update(List<LightSource> lights) {
if (buffer == null) {
buffer = RenderSystem.getDevice().createBuffer(
() -> "Torch Lights UBO",
GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST,
SIZE);
RealisticTorchlight.LOGGER.info("[Torch] UBO de luces creado ({} bytes)", SIZE);
}
TorchConfig cfg = TorchConfig.get();
boolean flicker = cfg.quality.flicker && cfg.flickerEnabled;
int count = Math.min(lights.size(), MAX_LIGHTS);
try (MemoryStack stack = MemoryStack.stackPush()) {
Std140Builder b = Std140Builder.onStack(stack, SIZE);
b.putIVec4(count, flicker ? 1 : 0, Math.round(cfg.intensity * 100.0F), (int) cfg.maxRadius);
for (int i = 0; i < MAX_LIGHTS; i++) {
if (i < count) {
LightSource l = lights.get(i);
b.putIVec4(l.x(), l.y(), l.z(), l.level());
} else {
b.putIVec4(0, 0, 0, 0);
}
}
for (int i = 0; i < MAX_LIGHTS; i++) {
if (i < count) {
LightSource l = lights.get(i);
float seed = ((l.x() * 31 + l.y() * 17 + l.z() * 13) & 255) / 255.0F;
b.putVec4(1.0F, 0.62F, 0.30F, seed);
} else {
b.putVec4(0, 0, 0, 0);
}
}
ByteBuffer data = b.get();
RenderSystem.getDevice().createCommandEncoder().writeToBuffer(buffer.slice(), data);
}
}
}
