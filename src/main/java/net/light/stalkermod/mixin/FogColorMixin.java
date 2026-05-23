package net.light.stalkermod.mixin;

import net.light.stalkermod.StalkerModClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(BackgroundRenderer.class)
public class FogColorMixin {

    @Shadow private static float red;
    @Shadow private static float green;
    @Shadow private static float blue;

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"))
    private static void modifyClearColor(Args args) {
        if (StalkerModClient.localEmissionTick > 0 || StalkerModClient.postEffectTick > 0) {
            float r = args.get(0);
            float g = args.get(1);
            float b = args.get(2);

            Vec3d customColor = StalkerModClient.getEmissionSkyColor(new Vec3d(r, g, b));

            args.set(0, (float) customColor.x);
            args.set(1, (float) customColor.y);
            args.set(2, (float) customColor.z);

            red = (float) customColor.x;
            green = (float) customColor.y;
            blue = (float) customColor.z;
        }
        else if (StalkerModClient.psiIntensity > 0) {
            float psi = StalkerModClient.psiIntensity;
            float r = args.get(0);
            float g = args.get(1);
            float b = args.get(2);

            float outR = r * (1.0f - psi) + (0.05f * psi);
            float outG = g * (1.0f - psi) + (0.05f * psi);
            float outB = b * (1.0f - psi) + (0.15f * psi);

            args.set(0, outR);
            args.set(1, outG);
            args.set(2, outB);

            red = outR;
            green = outG;
            blue = outB;
        }
    }
}