package net.light.stalkermod.mixin;

import net.light.stalkermod.StalkerModClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setRotation(float yaw, float pitch);
    @Shadow public abstract float getPitch();
    @Shadow public abstract float getYaw();

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        int t = StalkerModClient.localEmissionTick;

        if (t > 0) {
            float intensity = 0.0f;

            
            if (t <= 1800 && t > 1570) intensity = 0.8f;      
            else if (t <= 1200 && t > 970) intensity = 1.5f;  
            else if (t <= 230 && t > 0) intensity = 3.0f;    

            if (intensity > 0.0f) {
                
                float wave = (float) Math.sin(t * 0.5) * (intensity * 0.5f);
                
                float jitterYaw = (float) ((Math.random() - 0.5) * intensity);
                float jitterPitch = (float) ((Math.random() - 0.5) * intensity);

                this.setRotation(this.getYaw() + wave + jitterYaw, this.getPitch() + jitterPitch);
            }
        }
    }
}