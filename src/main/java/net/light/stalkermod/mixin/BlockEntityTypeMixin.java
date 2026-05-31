package net.light.stalkermod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {

    @Inject(method = "supports", at = @At("HEAD"), cancellable = true)
    private void stalkermod$supportCustomSigns(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof SignBlock || state.getBlock() instanceof WallSignBlock) {
            if ((Object) this == BlockEntityType.SIGN) {
                cir.setReturnValue(true);
            }
        }
        if (state.getBlock() instanceof HangingSignBlock || state.getBlock() instanceof WallHangingSignBlock) {
            if ((Object) this == BlockEntityType.HANGING_SIGN) {
                cir.setReturnValue(true);
            }
        }
    }
}