package net.light.stalkermod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.light.stalkermod.client.render.ShelfBlockEntityRenderer;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.light.stalkermod.network.EmissionPayload;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

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
    public static float psiIntensity = 0.0f;
    private static boolean cachedShaderState = false;
    private static long lastCheckTime = 0;

    private static Class<?> irisApiClass = null;
    private static java.lang.reflect.Method getInstanceMethod = null;
    private static java.lang.reflect.Method isShaderPackInUseMethod = null;
    private static java.lang.reflect.Method getShaderPackNameMethod = null;
    private static boolean reflectionInitialized = false;

    private static void initReflection() {
        if (reflectionInitialized) return;
        reflectionInitialized = true;
        if (!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("iris")) return;
        try {
            irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            getInstanceMethod = irisApiClass.getMethod("getInstance");
            isShaderPackInUseMethod = irisApiClass.getMethod("isShaderPackInUse");
            getShaderPackNameMethod = irisApiClass.getMethod("getShaderPackName");
        } catch (Throwable ignored) {}
    }

    public static boolean isShaderActive() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCheckTime < 1000) return cachedShaderState;
        lastCheckTime = currentTime;

        initReflection();
        if (irisApiClass == null) {
            cachedShaderState = false;
            return false;
        }

        try {
            Object api = getInstanceMethod.invoke(null);
            boolean isPackInUse = (boolean) isShaderPackInUseMethod.invoke(api);
            if (!isPackInUse) {
                cachedShaderState = false;
                return false;
            }
            String name = getShaderPackNameMethod.invoke(api).toString().toLowerCase();
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

    public static float getPreFlashIntensity(int tick) {
        if (tick <= 0 || tick > 240) return 0.0f;

        int[] flashTicks = {200, 120, 60, 20};

        for (int ft : flashTicks) {
            if (tick <= ft && tick > ft - 15) {
                return (tick - (ft - 15)) / 15.0f;
            }
        }
        return 0.0f;
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

            } else if (progress < 0.90f) {
                float t = smoothstep(0.25f, 0.90f, progress);
                return lerpVec(new net.minecraft.util.math.Vec3d(0.15, 0.15, 0.15), new net.minecraft.util.math.Vec3d(0.6, 0.05, 0.0), t);

            } else {
                float flash = getPreFlashIntensity(localEmissionTick);

                return new net.minecraft.util.math.Vec3d(
                        0.6 + flash * 0.4,
                        0.05 + flash * 0.95,
                        flash * 1.0
                );
            }

        } else if (postEffectTick > 0) {
            float progress = Math.min(1.0f, postEffectTick / 1800.0f);

            if (isShaderActive()) {
                return new net.minecraft.util.math.Vec3d(1.0, progress, 1.0);
            }

            net.minecraft.util.math.Vec3d peakRed = new net.minecraft.util.math.Vec3d(0.6, 0.05, 0.0);
            return lerpVec(vanillaColor, peakRed, progress);
        }

        return vanillaColor;
    }

    @Override
    public void onInitializeClient() {
        StalkerModConfigClient.load();
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ModBlocks.PALE_OAK_SAPLING,
                ModBlocks.PALE_OAK_LEAVES,
                ModBlocks.PALE_OAK_DOOR,
                ModBlocks.PALE_OAK_TRAPDOOR,
                ModBlocks.PALE_HANGING_MOSS,
                ModBlocks.PALE_HANGING_MOSS_TIP);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ModBlocks.COPPER_BARS,
                ModBlocks.EXPOSED_COPPER_BARS,
                ModBlocks.WEATHERED_COPPER_BARS,
                ModBlocks.OXIDIZED_COPPER_BARS);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ModBlocks.COPPER_CHAIN,
                ModBlocks.EXPOSED_COPPER_CHAIN,
                ModBlocks.WEATHERED_COPPER_CHAIN,
                ModBlocks.OXIDIZED_COPPER_CHAIN);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ModBlocks.COPPER_LANTERN,
                ModBlocks.EXPOSED_COPPER_LANTERN,
                ModBlocks.WEATHERED_COPPER_LANTERN,
                ModBlocks.OXIDIZED_COPPER_LANTERN);
        ZoneOverlay.init();
        EntityRendererRegistry.register(StalkerMod.ANOMALY_ENTITY, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(StalkerMod.ELEMENTAL_ANOMALY, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(StalkerMod.EFFECT_ZONE_ENTITY, EffectZoneEntityRenderer::new);
        EntityRendererRegistry.register(net.light.stalkermod.StalkerMod.BOLT_ENTITY_TYPE, net.light.stalkermod.BoltEntityRenderer::new);
        net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlocks(
                net.minecraft.client.render.RenderLayer.getCutout(),
                ModBlocks.PALE_MOSS_CARPET
        );
        net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register(
                StalkerMod.SHELF_BLOCK_ENTITY,
                net.light.stalkermod.client.render.ShelfBlockEntityRenderer::new
        );

        net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register(
                StalkerMod.COPPER_GOLEM_STATUE_ENTITY,
                net.light.stalkermod.client.render.CopperGolemStatueRenderer::new
        );

        ClientPlayNetworking.registerGlobalReceiver(EmissionPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                int serverTick = payload.state();

                if (serverTick == 0) {
                    localEmissionTick = -1;
                    postEffectTick = 1800;
                }
                else if (serverTick < 0) {
                    localEmissionTick = -1;
                    postEffectTick = -serverTick;
                }
                else if (serverTick > 0 && serverTick <= 2600) {
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

            boolean inPsi = false;
            if (client.player != null && client.world != null) {
                net.minecraft.util.math.Box playerBox = client.player.getBoundingBox();
                for (EffectZoneEntity zone : client.world.getEntitiesByClass(EffectZoneEntity.class, playerBox.expand(0.5), z -> z.type == 2)) {
                    if (zone.getZoneBox().intersects(playerBox)) {
                        inPsi = true;
                        break;
                    }
                }
            }

            if (inPsi) {
                if (psiIntensity < 1.0f) psiIntensity += 0.05f;
            } else {
                if (psiIntensity > 0.0f) psiIntensity -= 0.05f;
            }
            psiIntensity = net.minecraft.util.math.MathHelper.clamp(psiIntensity, 0.0f, 1.0f);
        });
    }
}