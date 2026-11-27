package com.mattpack31.realistic.torchlight.client.mixin;

import com.mattpack31.realistic.torchlight.client.TorchLightsUbo;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
@Inject(method = "bindDefaultUniforms", at = @At("RETURN"))
private static void torch$bindLights(RenderPass renderPass, CallbackInfo ci) {
GpuBuffer lights = TorchLightsUbo.get();
if (lights != null) {
renderPass.setUniform("TorchLights", lights);
}
}
}
