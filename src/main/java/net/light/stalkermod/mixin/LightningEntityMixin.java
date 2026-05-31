package net.light.stalkermod.mixin;

import net.light.stalkermod.block.CopperGolemStatueBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin extends Entity {

    @Unique
    private boolean stalkermod$redirected = false;

    public LightningEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void redirectAndCleanLightning(CallbackInfo ci) {
        if (!stalkermod$redirected && !this.getWorld().isClient()) {
            this.stalkermod$redirected = true;

            World world = this.getWorld();
            BlockPos currentPos = this.getBlockPos();

            Optional<BlockPos> golemPos = BlockPos.findClosest(
                    currentPos,
                    16,
                    16,
                    pos -> {
                        if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) return false;
                        return world.getBlockState(pos).getBlock() instanceof CopperGolemStatueBlock;
                    }
            );

            if (golemPos.isPresent()) {
                BlockPos target = golemPos.get();

                this.setPosition(target.getX() + 0.5, target.getY() + 1.0, target.getZ() + 0.5);

                BlockState state = world.getBlockState(target);
                BlockState currentState = state;

                for (int i = 0; i < 2; i++) {
                    Optional<BlockState> decreased = Oxidizable.getDecreasedOxidationState(currentState);
                    if (decreased.isPresent()) {
                        currentState = decreased.get();
                    } else {
                        break;
                    }
                }

                if (currentState != state) {
                    world.setBlockState(target, currentState);
                    world.syncWorldEvent(3002, target, -1); // Звук и частицы очистки меди
                }
            }
        }
    }
}