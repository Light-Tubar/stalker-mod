package net.light.stalkermod.mixin;

import net.light.stalkermod.StalkerMod;
import net.light.stalkermod.StalkerModClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        int eTick = StalkerModClient.localEmissionTick;
        int pTick = StalkerModClient.postEffectTick;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        if (eTick > 0 && eTick <= 2600 && !StalkerModClient.isShaderActive()) {
            float progress = net.minecraft.util.math.MathHelper.clamp(1.0f - (eTick / 2400.0f), 0.0f, 1.0f);
            float darkness = Math.min(0.65f, progress * 1.5f);
            int alpha = (int) (darkness * 255);
            int color = (alpha << 24) | 0x000000;

            context.fill(0, 0, width, height, color);
        }

        if (eTick > 0 && eTick <= 240) {
            float preFlashAlpha = StalkerModClient.getPreFlashIntensity(eTick);
            if (preFlashAlpha > 0.0f) {
                int a = (int) (preFlashAlpha * 220);
                context.fill(0, 0, width, height, (a << 24) | 0xFFFFFF);
            }
        }

        if (eTick > 0 && eTick <= 30 && !StalkerModClient.isShaderActive()) {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player != null && !client.player.isCreative() && !client.player.isSpectator()) {
                boolean isUnsafe = client.world.isSkyVisible(client.player.getBlockPos()) || client.world.isSkyVisible(client.player.getBlockPos().up());
                if (isUnsafe) {
                    float t = 1.0f - (eTick / 30.0f); //
                    int a = (int) (t * 200);
                    context.fill(0, 0, width, height, (a << 24) | 0xFF0000);
                }
            }
        }

        if (pTick > 0) {
            if (pTick > 1785) {
                float alpha = (pTick - 1785) / 15.0f;
                int alphaHex = (int) (alpha * 255) << 24;
                context.fill(0, 0, width, height, alphaHex | 0xFFFFFF);
            }
            else if (pTick > 1740) {
                float time = (float) (System.currentTimeMillis() % 1500L) / 1500.0f;
                float pulse = (float) (Math.sin(time * Math.PI * 2.0) * 0.12 + 0.68);
                float fade = 1.0f - ((pTick - 1740) / 45.0f);

                int alphaHex = (int) (fade * pulse * 255) << 24;
                context.fill(0, 0, width, height, alphaHex | 0x990000);
            }
            else if (pTick > 1540) {
                float progress = (pTick - 1540) / 200.0f;
                float alpha = 0.25f + (progress * 0.40f);
                int alphaHex = (int) (alpha * 255) << 24;

                int r = (int) (0x11 + (progress * (0x99 - 0x11)));
                int g = 0x11;
                int b = 0x11;
                int color = (r << 16) | (g << 8) | b;

                context.fill(0, 0, width, height, alphaHex | color);
            }
            else {
                float progress = pTick / 1540.0f;
                float alpha = progress * 0.25f;
                int alphaHex = (int) (alpha * 255) << 24;
                context.fill(0, 0, width, height, alphaHex | 0x111111);
            }
        }

        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        net.minecraft.entity.player.PlayerEntity player = client.player;

        if (player != null && player.isUsingItem() && player.getActiveItem().getItem() == StalkerMod.BOLT_ITEM) {
            int useTime = player.getItemUseTime();
            float charge = Math.min(1.0f, useTime / 20.0f);

            int barWidth = 50;
            int barHeight = 4;
            int x = (width / 2) - (barWidth / 2);
            int y = (height / 2) + 15;

            context.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);

            int r = (int) (255 * charge);
            int g = (int) (255 * (1.0f - charge));
            int color = 0xFF000000 | (r << 16) | (g << 8);

            int currentWidth = (int) (barWidth * charge);
            context.fill(x, y, x + currentWidth, y + barHeight, color);
        }

        if (StalkerModClient.psiIntensity > 0 && eTick <= 0 && pTick <= 0) {
            float psi = StalkerModClient.psiIntensity;
            float time = (float) (System.currentTimeMillis() % 2000L) / 2000.0f;
            float pulse = (float) (Math.sin(time * Math.PI * 2.0) * 0.2 + 0.8);

            int a = (int) (psi * pulse * 130);
            context.fill(0, 0, width, height, (a << 24) | 0x050515);
        }
    }
}