package net.light.stalkermod.mixin;

import net.light.stalkermod.CustomAttackerTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements CustomAttackerTracker {

    @Unique
    private Entity stalkermod$lastAttacker;

    @Override
    public void stalkermod$setLastAttacker(Entity entity) {
        this.stalkermod$lastAttacker = entity;
    }

    @Override
    public Entity stalkermod$getLastAttacker() {
        return this.stalkermod$lastAttacker;
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void captureAttacker(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source != null && source.getAttacker() != null) {
            // Сохраняем любую сущность, которая нанесла урон (включая наши аномалии)
            this.stalkermod$setLastAttacker(source.getAttacker());
        }
    }
}