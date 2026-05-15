package net.light.stalkermod.mixin;

import net.light.stalkermod.StalkerModConfigClient;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSoundInstance.class)
public abstract class AbstractSoundInstanceMixin {

    @Inject(method = "getVolume", at = @At("RETURN"), cancellable = true)
    private void applyStalkerSoundVolume(CallbackInfoReturnable<Float> cir) {
        Identifier id = ((AbstractSoundInstance)(Object)this).getId();

        
        if (id.getNamespace().equals("stalker-mod")) {
            String path = id.getPath();

            
            if (path.contains("emission") || path.contains("blowout") ||
                    path.contains("earthquake") || path.contains("thunder") || path.contains("wind")) {
                cir.setReturnValue(cir.getReturnValue() * (float) StalkerModConfigClient.emissionVolume);
            }
            
            else if (path.contains("anomaly") || path.contains("burner") || path.contains("geiger")) {
                cir.setReturnValue(cir.getReturnValue() * (float) StalkerModConfigClient.anomalyVolume);
            }
        }
    }
}