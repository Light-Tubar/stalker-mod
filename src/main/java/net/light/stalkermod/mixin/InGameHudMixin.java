package net.light.stalkermod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
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

        
        if (eTick > 0 && !StalkerModClient.isShaderActive()) {
            float progress = 1.0f - (eTick / 2400.0f);
            
            float darkness = Math.min(0.65f, progress * 1.5f);
            int alpha = (int) (darkness * 255);
            int color = (alpha << 24) | 0x000000; 

            context.fill(0, 0, width, height, color);
        }

        
        if (pTick > 0) {
            
            if (pTick > 390) {
                
                float alpha = (pTick - 390) / 10.0f;
                int alphaHex = (int) (alpha * 255) << 24;
                context.fill(0, 0, width, height, alphaHex | 0xFFFFFF); 
            }
            
            else {
                
                float alpha = Math.min(1.0f, pTick / 200.0f) * 0.7f; 
                int alphaHex = (int) (alpha * 255) << 24;
                context.fill(0, 0, width, height, alphaHex | 0x333333); 
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
    }
}