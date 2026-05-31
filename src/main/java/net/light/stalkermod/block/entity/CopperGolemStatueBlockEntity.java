package net.light.stalkermod.block.entity;

import net.light.stalkermod.StalkerMod;
import net.light.stalkermod.block.CopperGolemStatueBlock;
import net.light.stalkermod.block.GolemPose;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CopperGolemStatueBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CopperGolemStatueBlockEntity(BlockPos pos, BlockState state) {
        super(StalkerMod.COPPER_GOLEM_STATUE_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "pose_controller", 5, this::predicate));
    }

    private PlayState predicate(AnimationState<CopperGolemStatueBlockEntity> event) {
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public static void serverTick(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state, CopperGolemStatueBlockEntity blockEntity) {
        if (world.isThundering()) {
            if (world.random.nextInt(800) == 0) {
                if (world.isSkyVisible(pos.up())) {
                    net.minecraft.entity.LightningEntity lightning = net.minecraft.entity.EntityType.LIGHTNING_BOLT.create(world);
                    if (lightning != null) {
                        lightning.refreshPositionAfterTeleport(net.minecraft.util.math.Vec3d.ofBottomCenter(pos));
                        world.spawnEntity(lightning);
                    }
                }
            }
        }
    }
}