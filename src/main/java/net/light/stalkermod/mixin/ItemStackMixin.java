package net.light.stalkermod.mixin;

import net.light.stalkermod.StalkerMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    private void appendSpawnerTooltip(
            Item.TooltipContext context,
            PlayerEntity player,
            net.minecraft.item.tooltip.TooltipType type,
            CallbackInfoReturnable<List<Text>> cir
    ) {
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.isOf(StalkerMod.ANOMALY_SPAWNER)) {
            List<Text> tooltip = cir.getReturnValue();

            if (Screen.hasShiftDown()) {
                tooltip.add(Text.literal(""));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.title"));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.mode"));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.delete"));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.trampoline_r"));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.trampoline_d"));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.trampoline_f"));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.elemental"));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.zones"));

                tooltip.add(Text.literal(""));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.zone_guide_title"));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.zone_step1"));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.zone_step2"));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.zone_step3"));
            } else {
                tooltip.add(Text.literal(""));
                tooltip.add(Text.translatable("tooltip.stalkermod.spawner.hold_shift"));
            }
        }
    }
}