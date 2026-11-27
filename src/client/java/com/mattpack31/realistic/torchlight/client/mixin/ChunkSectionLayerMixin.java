package com.mattpack31.realistic.torchlight.client.mixin;

import com.mattpack31.realistic.torchlight.RealisticTorchlight;
import com.mattpack31.realistic.torchlight.client.LightScanner;
import com.mattpack31.realistic.torchlight.client.TorchLightsUbo;
import com.mattpack31.realistic.torchlight.client.TorchPipelines;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSectionLayer.class)
public class ChunkSectionLayerMixin {
    @Inject(method = "pipeline", at = @At("HEAD"), cancellable = true)
    private void torch$pipeline(CallbackInfoReturnable<RenderPipeline> cir) {
        if (LightScanner.getLights().isEmpty() || TorchLightsUbo.get() == null) {
            return;
        }
        Object self = this;
        RenderPipeline chosen = null;
        if (self == ChunkSectionLayer.SOLID) {
            chosen = TorchPipelines.SOLID_TERRAIN;
        } else if (self == ChunkSectionLayer.CUTOUT) {
            chosen = TorchPipelines.CUTOUT_TERRAIN;
        } else if (self == ChunkSectionLayer.TRANSLUCENT) {
            chosen = TorchPipelines.TRANSLUCENT_TERRAIN;
        }
        if (chosen != null) {
            if (!TorchPipelines.used) {
                TorchPipelines.used = true;
                RealisticTorchlight.LOGGER.info("[Torch] Pipeline de terreno propio en uso");
            }
            cir.setReturnValue(chosen);
        }
    }
}
