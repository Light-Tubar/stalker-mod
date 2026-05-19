package net.light.stalkermod.item;

import net.light.stalkermod.StalkerMod;
import net.light.stalkermod.entity.BoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BoltItem extends Item {
    public BoltItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }

        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        player.getItemCooldownManager().set(this, 15);

        int charge = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float power = charge / 20.0f;
        if (power > 1.0f) power = 1.0f;
        if (power < 0.5f) power = 0.5f;
        if (!world.isClient) {
            BoltEntity bolt = new BoltEntity(world, player);
            bolt.setItem(stack);
            float finalSpeed = power * 1.5F;
            float pitchOffset = -10.0F * power;
            bolt.setPosition(player.getX(), player.getEyeY() - 0.4, player.getZ());
            bolt.setVelocity(player, player.getPitch() + pitchOffset, player.getYaw(), 0.0F, finalSpeed, 1.0F);
            world.spawnEntity(bolt);
        }
        world.playSound(null, player.getX(), player.getY(), player.getZ(), StalkerMod.BOLT_THROW, SoundCategory.NEUTRAL, 0.3F, 0.8F + world.getRandom().nextFloat() * 0.4F);
    }
}