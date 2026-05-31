package net.light.stalkermod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public class ZoneOverlay {
    public static float acidFlash = 0.0f;
    public static float psiExposure;
    public static float radExposure;

    
    private static float currentRadAlpha = 0.0f;
    private static float currentPsiAlpha = 0.0f;
    private static float currentAcidAlpha = 0.0f;

    
    private static float targetRadAlpha = 0.0f;
    private static float targetPsiAlpha = 0.0f;
    private static float radSpeed = 0.005f;
    private static float psiSpeed = 0.005f;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.isPaused() || client.player == null) return;
            PlayerEntity player = client.player;

            if (player.age % 10 == 0) {
                float activeRadZoneStrength = 0.0f;
                float activePsiZoneStrength = 0.0f;

                java.util.List<EffectZoneEntity> zones = player.getWorld().getEntitiesByClass(
                        EffectZoneEntity.class, player.getBoundingBox().expand(50.0),
                        zone -> zone.type == 1 || zone.type == 2
                );

                for (EffectZoneEntity zone : zones) {
                    if (zone.getZoneBox().contains(player.getPos())) {
                        if (zone.type == 1 && zone.strength > activeRadZoneStrength) activeRadZoneStrength = zone.strength;
                        if (zone.type == 2 && zone.strength > activePsiZoneStrength) activePsiZoneStrength = zone.strength;
                    }
                }

                float invRad = StalkerMod.getInventoryRadiation(player);
                float invPsi = StalkerMod.getInventoryPsi(player);

                float effectiveRadStrength = Math.max(0.0f, activeRadZoneStrength + invRad);
                float effectivePsiStrength = Math.max(0.0f, activePsiZoneStrength + invPsi);

                targetRadAlpha = effectiveRadStrength > 0 ? Math.min(255, effectiveRadStrength * 25) : 0;
                targetPsiAlpha = effectivePsiStrength > 0 ? Math.min(255, effectivePsiStrength * 25) : 0;

                radSpeed = effectiveRadStrength > 0
                        ? Math.min(0.02f, 0.0001f * (float) Math.pow(1.8, effectiveRadStrength))
                        : 0.005f;

                psiSpeed = effectivePsiStrength > 0
                        ? Math.min(0.02f, 0.0001f * (float) Math.pow(1.8, effectivePsiStrength))
                        : 0.005f;
            }
            acidFlash = Math.max(0.0f, acidFlash - 0.05f);
        });

        
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (MinecraftClient.getInstance().player == null) return;

            int width = drawContext.getScaledWindowWidth();
            int height = drawContext.getScaledWindowHeight();


            currentRadAlpha += (targetRadAlpha - currentRadAlpha) * radSpeed;
            currentPsiAlpha += (targetPsiAlpha - currentPsiAlpha) * psiSpeed;

            float targetAcidAlpha = acidFlash > 0 ? Math.min(255, acidFlash * 200) : 0;
            currentAcidAlpha += (targetAcidAlpha - currentAcidAlpha) * 0.05f;

            
            if (currentRadAlpha > 1.0f) {
                int color = ((int)currentRadAlpha << 24) | 0x19FF19;
                drawContext.fill(0, 0, width, height, color);
            }

            
            if (currentPsiAlpha > 1.0f) {
                int color = ((int)currentPsiAlpha << 24) | 0x8360A8;
                drawContext.fill(0, 0, width, height, color);
            }

            
            if (currentAcidAlpha > 1.0f) {
                int color = ((int)currentAcidAlpha << 24) | 0x123524;
                drawContext.fill(0, 0, width, height, color);
            }
        });
    }
}