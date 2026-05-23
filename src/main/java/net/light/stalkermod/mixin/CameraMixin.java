package net.light.stalkermod.mixin;

import net.light.stalkermod.EffectZoneEntity;
import net.light.stalkermod.StalkerModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Box;
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
        int p = StalkerModClient.postEffectTick;

        float intensity = 0.0f;
        float tickRef = 0.0f;

        if (t > 0) {
            tickRef = t;
            if (t <= 1200 && t > 970) intensity = 1.5f;
            else if (t <= 600 && t > 370) intensity = 2.0f;
            else if (t <= 230 && t > 0) intensity = 3.0f;
        }
        else if (p > 1740) {
            tickRef = p;
            float fade = (p - 1740) / 60.0f;
            intensity = fade * 3.0f;
        }

        if (intensity > 0.0f) {
            float wave = (float) Math.sin(tickRef * 0.5) * (intensity * 0.5f);
            float jitterYaw = (float) ((Math.random() - 0.5) * intensity);
            float jitterPitch = (float) ((Math.random() - 0.5) * intensity);
            this.setRotation(this.getYaw() + wave + jitterYaw, this.getPitch() + jitterPitch);
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.world != null) {
            Box playerBox = client.player.getBoundingBox();
            boolean inPsiZone = false;

            for (EffectZoneEntity zone : client.world.getEntitiesByClass(EffectZoneEntity.class, playerBox.expand(0.5), z -> z.type == 2)) {
                if (zone.getZoneBox().intersects(playerBox)) {
                    inPsiZone = true;
                    break;
                }
            }

            if (inPsiZone && !client.isPaused()) {
                float time = client.player.age + tickDelta;
                float swayYaw = (float) Math.sin(time * 0.05f) * 1.2f;
                float swayPitch = (float) Math.cos(time * 0.04f) * 0.8f;

                this.setRotation(this.getYaw() + swayYaw, this.getPitch() + swayPitch);
            }

            if (StalkerModClient.psiIntensity > 0 && intensity == 0.0f && !net.minecraft.client.MinecraftClient.getInstance().isPaused()) {
                float time = tickDelta + net.minecraft.client.MinecraftClient.getInstance().player.age;
                float psi = StalkerModClient.psiIntensity;

                float swayYaw = (float) Math.sin(time * 0.05f) * 1.5f * psi;
                float swayPitch = (float) Math.cos(time * 0.04f) * 1.0f * psi;

                this.setRotation(this.getYaw() + swayYaw, this.getPitch() + swayPitch);
            }
        }
    }
}