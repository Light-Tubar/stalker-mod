package net.light.stalkermod.mixin;

import net.light.stalkermod.StalkerModClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    // Вырезаем видимые летящие капли
    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void cancelEmissionRainDrops(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (StalkerModClient.localEmissionTick > 0 || StalkerModClient.postEffectTick > 0) {
            ci.cancel();
        }
    }

    // Вырезаем звуки дождя и брызги на земле
    @Inject(method = "tickRainSplashing", at = @At("HEAD"), cancellable = true)
    private void cancelEmissionRainSounds(Camera camera, CallbackInfo ci) {
        if (StalkerModClient.localEmissionTick > 0 || StalkerModClient.postEffectTick > 0) {
            ci.cancel();
        }
    }
}