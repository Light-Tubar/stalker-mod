package net.light.stalkermod.mixin;

import net.light.stalkermod.AnomalyEntity;
import net.light.stalkermod.CustomAttackerTracker;
import net.light.stalkermod.EffectZoneEntity;
import net.light.stalkermod.ElementalAnomalyEntity;
import net.light.stalkermod.EmissionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageTracker.class)
public abstract class DamageTrackerMixin {

    @Shadow @Final private LivingEntity entity;

    @Inject(method = "getDeathMessage", at = @At("HEAD"), cancellable = true)
    private void customDeathMessage(CallbackInfoReturnable<Text> cir) {
        String name = entity.getName().getString();

        if (EmissionManager.isEmissionDamage) {
            cir.setReturnValue(Text.translatable("death.stalkermod.emission", name));
            return;
        }

        DamageSource source = entity.getRecentDamageSource();
        Entity attacker = (source != null) ? source.getAttacker() : null;

        if (attacker == null && entity instanceof CustomAttackerTracker tracker) {
            attacker = tracker.stalkermod$getLastAttacker();
        }

        if (attacker == null) return;

        if (attacker instanceof AnomalyEntity) {
            cir.setReturnValue(Text.translatable("death.stalkermod.trampoline", name));
        } else if (attacker instanceof ElementalAnomalyEntity elem) {
            if (elem.type == 0) cir.setReturnValue(Text.translatable("death.stalkermod.acid", name));
            else if (elem.type == 1) cir.setReturnValue(Text.translatable("death.stalkermod.burner", name));
            else if (elem.type == 2) cir.setReturnValue(Text.translatable("death.stalkermod.electro", name));
        } else if (attacker instanceof EffectZoneEntity zone) {
            if (zone.type == 1) cir.setReturnValue(Text.translatable("death.stalkermod.radiation", name));
            else if (zone.type == 2) cir.setReturnValue(Text.translatable("death.stalkermod.psi", name));
        }
    }
}