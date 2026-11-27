#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:chunksection.glsl>

layout(std140) uniform TorchLights {
    ivec4 LightCount;
    ivec4 LightPos[16];
    vec4 LightColor[16];
};

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec3 torchPos;

out vec4 fragColor;

vec4 sampleNearest(sampler2D source, vec2 uv, vec2 pixelSize, vec2 du, vec2 dv, vec2 texelScreenSize) {
    vec2 uvTexelCoords = uv / pixelSize;
    vec2 texelCenter = round(uvTexelCoords) - 0.5f;
    vec2 texelOffset = uvTexelCoords - texelCenter;

    texelOffset = (texelOffset - 0.5f) * pixelSize / texelScreenSize + 0.5f;
    texelOffset = clamp(texelOffset, 0.0f, 1.0f);

    uv = (texelCenter + texelOffset) * pixelSize;
    return textureGrad(source, uv, du, dv);
}

vec4 sampleNearest(sampler2D source, vec2 uv, vec2 pixelSize) {
    vec2 du = dFdx(uv);
    vec2 dv = dFdy(uv);
    vec2 texelScreenSize = sqrt(du * du + dv * dv);
    return sampleNearest(source, uv, pixelSize, du, dv, texelScreenSize);
}

vec4 sampleRGSS(sampler2D source, vec2 uv, vec2 pixelSize) {
    vec2 du = dFdx(uv);
    vec2 dv = dFdy(uv);

    vec2 texelScreenSize = sqrt(du * du + dv * dv);
    float maxTexelSize = max(texelScreenSize.x, texelScreenSize.y);

    float minPixelSize = min(pixelSize.x, pixelSize.y);

    float transitionStart = minPixelSize * 1.0;
    float transitionEnd = minPixelSize * 2.0;
    float blendFactor = smoothstep(transitionStart, transitionEnd, maxTexelSize);

    float duLength = length(du);
    float dvLength = length(dv);
    float minDerivative = min(duLength, dvLength);
    float maxDerivative = max(duLength, dvLength);

    float effectiveDerivative = sqrt(minDerivative * maxDerivative);

    float mipLevelExact = max(0.0, log2(effectiveDerivative / minPixelSize));

    float mipLevelLow = floor(mipLevelExact);
    float mipLevelHigh = mipLevelLow + 1.0;
    float mipBlend = fract(mipLevelExact);

    const vec2 offsets[4] = vec2[](
    vec2(0.125, 0.375),
    vec2(-0.125, -0.375),
    vec2(0.375, -0.125),
    vec2(-0.375, 0.125)
    );

    vec4 rgssColorLow = vec4(0.0);
    vec4 rgssColorHigh = vec4(0.0);
    for (int i = 0; i < 4; ++i) {
        vec2 sampleUV = uv + offsets[i] * pixelSize;
        rgssColorLow += textureLod(source, sampleUV, mipLevelLow);
        rgssColorHigh += textureLod(source, sampleUV, mipLevelHigh);
    }
    rgssColorLow *= 0.25;
    rgssColorHigh *= 0.25;

    vec4 rgssColor = mix(rgssColorLow, rgssColorHigh, mipBlend);

    vec4 nearestColor = sampleNearest(source, uv, pixelSize, du, dv, texelScreenSize);

    return mix(nearestColor, rgssColor, blendFactor);
}

vec3 torchLight(vec3 p) {
    vec3 n = normalize(cross(dFdx(p), dFdy(p)));
    if (dot(n, -p) < 0.0) {
        n = -n;
    }
    float t = GameTime * 1200.0;
    float intensity = float(LightCount.z) / 100.0;
    float maxRadius = float(LightCount.w);
    int count = min(LightCount.x, 16);
    vec3 sum = vec3(0.0);
    for (int i = 0; i < count; i++) {
        vec3 lp = vec3(LightPos[i].xyz - CameraBlockPos) + CameraOffset + vec3(0.5);
        vec3 toLight = lp - p;
        float dist = length(toLight);
        float radius = min(float(LightPos[i].w) * 0.9, maxRadius);
        float att = clamp(1.0 - dist / radius, 0.0, 1.0);
        att *= att;
        float ndl = clamp(dot(n, toLight / max(dist, 0.001)), 0.0, 1.0);
        ndl = 0.35 + 0.65 * ndl;
        float flick = 1.0;
        if (LightCount.y == 1) {
            float seed = LightColor[i].a;
            flick = 1.0 + 0.10 * sin(t * 7.0 + seed * 40.0) + 0.06 * sin(t * 13.0 + seed * 90.0);
        }
        sum += LightColor[i].rgb * att * ndl * flick;
    }
    return sum * intensity;
}

void main() {
    vec4 albedo = UseRgss == 1 ? sampleRGSS(Sampler0, texCoord0, 1.0f / TextureSize) : sampleNearest(Sampler0, texCoord0, 1.0f / TextureSize);
    vec4 color = albedo * vertexColor;
    color.rgb += albedo.rgb * torchLight(torchPos);
    color = mix(FogColor * vec4(1, 1, 1, color.a), color, ChunkVisibility);
#ifdef ALPHA_CUTOUT
    if (color.a < ALPHA_CUTOUT) {
        discard;
    }
#endif
    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
