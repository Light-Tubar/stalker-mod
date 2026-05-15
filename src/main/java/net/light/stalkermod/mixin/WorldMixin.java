package net.light.stalkermod.mixin;

import net.light.stalkermod.StalkerModClient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin {

    
    @Inject(method = "getRainGradient", at = @At("RETURN"), cancellable = true)
    private void smoothShaderClouds(float delta, CallbackInfoReturnable<Float> cir) {
        if (((World)(Object)this).isClient()) {
            if (StalkerModClient.localEmissionTick > 0) {
                float t = 1.0f - (StalkerModClient.localEmissionTick / 2400.0f);

                
                float cloudProgress = Math.min(1.0f, t / 0.083f);

                
                cir.setReturnValue(Math.max(cloudProgress, cir.getReturnValue()));
            } else if (StalkerModClient.postEffectTick > 0) {
                float t = StalkerModClient.postEffectTick / 400.0f;
                cir.setReturnValue(Math.max(t, cir.getReturnValue()));
            }
        }
    }

    
    @Inject(method = "getThunderGradient", at = @At("RETURN"), cancellable = true)
    private void smoothShaderThunder(float delta, CallbackInfoReturnable<Float> cir) {
        if (((World)(Object)this).isClient()) {
            if (StalkerModClient.localEmissionTick > 0) {
                float t = 1.0f - (StalkerModClient.localEmissionTick / 2400.0f);

                
                float thunderProgress = Math.min(1.0f, t / 0.166f);

                
                cir.setReturnValue(Math.max(thunderProgress * 0.5f, cir.getReturnValue()));
            } else if (StalkerModClient.postEffectTick > 0) {
                float t = StalkerModClient.postEffectTick / 400.0f;
                cir.setReturnValue(Math.max(t * 0.5f, cir.getReturnValue()));
            }
        }
    }
}