package com.mattpack31.realistic.torchlight.client;

import com.mattpack31.realistic.torchlight.RealisticTorchlight;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import net.minecraft.client.renderer.RenderPipelines;

public final class TorchPipelines {
public static final BindGroupLayout TORCH_LIGHTS = BindGroupLayout.builder()
.withUniform("TorchLights", UniformType.UNIFORM_BUFFER)
.build();

public static final RenderPipeline SOLID_TERRAIN = RenderPipelines.register(
base("torch_solid_terrain").build());

public static final RenderPipeline CUTOUT_TERRAIN = RenderPipelines.register(
base("torch_cutout_terrain").withShaderDefine("ALPHA_CUTOUT", 0.5F).build());

public static final RenderPipeline TRANSLUCENT_TERRAIN = RenderPipelines.register(
base("torch_translucent_terrain")
.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
.withShaderDefine("ALPHA_CUTOUT", 0.1F)
.build());

private TorchPipelines() {}

private static RenderPipeline.Builder base(String name) {
return RenderPipeline.builder(RenderPipelines.TERRAIN_SNIPPET)
.withLocation(RealisticTorchlight.id("pipeline/" + name))
.withVertexShader(RealisticTorchlight.id("core/torch_terrain"))
.withFragmentShader(RealisticTorchlight.id("core/torch_terrain"))
.withBindGroupLayout(TORCH_LIGHTS);
}

public static void init() {
RealisticTorchlight.LOGGER.info("[Torch] Pipelines de terreno registrados");
}
}
