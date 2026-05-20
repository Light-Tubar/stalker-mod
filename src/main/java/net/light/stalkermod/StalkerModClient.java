package net.light.stalkermod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.light.stalkermod.network.EmissionPayload;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.light.stalkermod.StalkerModConfigClient;

public class StalkerModClient implements ClientModInitializer {

    public static int localEmissionTick = -1;
    public static int postEffectTick = -1;

    private static Vec3d lerpColor(Vec3d v1, Vec3d v2, float t) {
        return new Vec3d(
                MathHelper.lerp(t, v1.x, v2.x),
                MathHelper.lerp(t, v1.y, v2.y),
                MathHelper.lerp(t, v1.z, v2.z)
        );
    }

    
    private static boolean cachedShaderState = false;
    private static long lastCheckTime = 0;

    
    public static boolean isShaderActive() {
        long currentTime = System.currentTimeMillis();

        
        if (currentTime - lastCheckTime < 1000) {
            return cachedShaderState;
        }
        lastCheckTime = currentTime;

        if (!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("iris")) {
            cachedShaderState = false;
            return false;
        }

        try {
            Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object api = apiClass.getMethod("getInstance").invoke(null);

            
            boolean isPackInUse = (boolean) apiClass.getMethod("isShaderPackInUse").invoke(api);
            if (!isPackInUse) {
                cachedShaderState = false;
                return false;
            }

            
            
            String name = apiClass.getMethod("getShaderPackName").invoke(api).toString().toLowerCase();
            if (name.contains("internal") || name.contains("vanilla") || name.contains("empty")) {
                cachedShaderState = false;
                return false;
            }

            cachedShaderState = true;
            return true;

        } catch (Throwable t) {
            
            
            cachedShaderState = true;
            return true;
        }
    }

    
    private static float smoothstep(float edge0, float edge1, float x) {
        float t = Math.max(0.0f, Math.min(1.0f, (x - edge0) / (edge1 - edge0)));
        return t * t * (3.0f - 2.0f * t);
    }

    
    private static net.minecraft.util.math.Vec3d lerpVec(net.minecraft.util.math.Vec3d a, net.minecraft.util.math.Vec3d b, float t) {
        return new net.minecraft.util.math.Vec3d(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t
        );
    }

    
    public static net.minecraft.util.math.Vec3d getEmissionSkyColor(net.minecraft.util.math.Vec3d vanillaColor) {
        if (localEmissionTick > 0) {
            int s = 2400 - localEmissionTick;
            float progress = Math.min(1.0f, s / 2400.0f);

            if (isShaderActive()) {
                
                return new net.minecraft.util.math.Vec3d(1.0, progress, 0.0);
            }

            
            if (progress < 0.25f) {
                
                float t = smoothstep(0.0f, 0.25f, progress);
                return lerpVec(vanillaColor, new net.minecraft.util.math.Vec3d(0.15, 0.15, 0.15), t);

            } else if (progress < 0.85f) {
                
                float t = smoothstep(0.25f, 0.85f, progress);
                return lerpVec(new net.minecraft.util.math.Vec3d(0.15, 0.15, 0.15), new net.minecraft.util.math.Vec3d(0.6, 0.05, 0.0), t);

            } else {
                
                float t = smoothstep(0.85f, 1.0f, progress);
                float pulseSpeed = 10.0f + (20.0f * t);

                float time = (float) (System.currentTimeMillis() % 10000L) / 1000.0f;
                float flash = (float) Math.pow(Math.sin(time * pulseSpeed) * 0.5 + 0.5, 4.0);

                
                return new net.minecraft.util.math.Vec3d(0.6 + flash * 0.6, 0.05, 0.0);
            }

        } else if (postEffectTick > 0) {
            float progress = Math.min(1.0f, postEffectTick / 400.0f);

            if (isShaderActive()) {
                
                return new net.minecraft.util.math.Vec3d(1.0, progress, 1.0);
            }

            
            float t = smoothstep(0.0f, 1.0f, Math.max(0.0f, Math.min(1.0f, (1.0f - progress) / 0.25f)));
            return lerpVec(new net.minecraft.util.math.Vec3d(0.15, 0.15, 0.15), vanillaColor, t);
        }

        return vanillaColor;
    }

    @Override
    public void onInitializeClient() {
        StalkerModConfigClient.load();
        ZoneOverlay.init();
        EntityRendererRegistry.register(StalkerMod.ANOMALY_ENTITY, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(StalkerMod.ELEMENTAL_ANOMALY, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(StalkerMod.EFFECT_ZONE_ENTITY, EffectZoneEntityRenderer::new);
        EntityRendererRegistry.register(net.light.stalkermod.StalkerMod.BOLT_ENTITY_TYPE, net.light.stalkermod.BoltEntityRenderer::new);

        
        ClientPlayNetworking.registerGlobalReceiver(EmissionPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                int serverTick = payload.state();

                if (serverTick == 0) {
                    
                    localEmissionTick = -1;
                    postEffectTick = 400; 
                }
                else if (serverTick > 0) {
                    
                    postEffectTick = -1;

                    if (localEmissionTick <= 0) {
                        
                        localEmissionTick = serverTick;
                    } else {
                        
                        
                        if (Math.abs(localEmissionTick - serverTick) > 10) {
                            localEmissionTick = serverTick;
                        }
                    }
                }
            });
        });


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.isPaused()) return;
            GeigerSoundManager.tick(client);
            if (localEmissionTick > 0) localEmissionTick--;
            if (postEffectTick > 0) postEffectTick--;
        });
    }
}