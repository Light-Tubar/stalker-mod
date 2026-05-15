package net.light.stalkermod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.light.stalkermod.StalkerModClient;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class FogColorMixin {

    @Inject(method = "applyFogColor", at = @At("TAIL"))
    private static void onApplyFogColor(CallbackInfo ci) {
        
        if (StalkerModClient.localEmissionTick > 0) {

            
            float[] fog = RenderSystem.getShaderFogColor();
            net.minecraft.util.math.Vec3d fogVec = new net.minecraft.util.math.Vec3d(fog[0], fog[1], fog[2]);

            
            net.minecraft.util.math.Vec3d newColor = StalkerModClient.getEmissionSkyColor(fogVec);

            
            RenderSystem.setShaderFogColor((float)newColor.x, (float)newColor.y, (float)newColor.z);
            RenderSystem.clearColor((float)newColor.x, (float)newColor.y, (float)newColor.z, 0.0f);
        }
    }
}