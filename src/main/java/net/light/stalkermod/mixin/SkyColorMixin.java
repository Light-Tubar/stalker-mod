package net.light.stalkermod.mixin;

import net.light.stalkermod.StalkerModClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class SkyColorMixin {

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void onGetSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if (StalkerModClient.localEmissionTick > 0 || StalkerModClient.postEffectTick > 0) {
            cir.setReturnValue(StalkerModClient.getEmissionSkyColor(cir.getReturnValue()));
        }
        else if (StalkerModClient.psiIntensity > 0 && StalkerModClient.isShaderActive()) {
            cir.setReturnValue(new Vec3d(0.0, 1.0, StalkerModClient.psiIntensity));
        }
    }

    @Inject(method = "getCloudsColor", at = @At("RETURN"), cancellable = true)
    private void onGetCloudsColor(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if (StalkerModClient.localEmissionTick > 0 || StalkerModClient.postEffectTick > 0) {
            cir.setReturnValue(StalkerModClient.getEmissionSkyColor(cir.getReturnValue()));
        }
        else if (StalkerModClient.psiIntensity > 0 && StalkerModClient.isShaderActive()) {
            cir.setReturnValue(new Vec3d(0.0, 1.0, StalkerModClient.psiIntensity));
        }
    }
}