package net.light.stalkermod.mixin;

import net.light.stalkermod.StalkerModConfigClient;
import net.light.stalkermod.StalkerModConfigClient;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin {

    
    @Inject(method = "getOptions", at = @At("RETURN"), cancellable = true)
    private static void addStalkerSoundSliders(GameOptions gameOptions, CallbackInfoReturnable<SimpleOption<?>[]> cir) {
        SimpleOption<?>[] original = cir.getReturnValue();

        
        SimpleOption<?>[] modified = Arrays.copyOf(original, original.length + 2);

        
        modified[original.length] = StalkerModConfigClient.EMISSION_VOLUME_OPTION;
        modified[original.length + 1] = StalkerModConfigClient.ANOMALY_VOLUME_OPTION;

        cir.setReturnValue(modified);
    }
}