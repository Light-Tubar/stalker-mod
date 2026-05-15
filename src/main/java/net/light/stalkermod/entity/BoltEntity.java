package net.light.stalkermod.entity;

import net.light.stalkermod.AnomalyEntity;
import net.light.stalkermod.EffectZoneEntity;
import net.light.stalkermod.ElementalAnomalyEntity;
import net.light.stalkermod.StalkerMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoltEntity extends ThrownItemEntity {
    
    private static final TrackedData<Boolean> LANDED = DataTracker.registerData(BoltEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int landedTicks = 0;
    private int bounceCount = 0;

    private float spinPitch = 0.0f;
    private float spinYaw = 0.0f;
    private boolean spinInitialized = false;

    public BoltEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public BoltEntity(World world, LivingEntity owner) {
        super(StalkerMod.BOLT_ENTITY_TYPE, owner, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(LANDED, false);
    }

    @Override
    protected Item getDefaultItem() {
        return StalkerMod.BOLT_ITEM;
    }

    private boolean isLanded() {
        return this.dataTracker.get(LANDED);
    }

    private void setLanded(boolean state) {
        this.dataTracker.set(LANDED, state);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Landed", this.isLanded());
        nbt.putInt("LandedTicks", this.landedTicks);
        nbt.putInt("BounceCount", this.bounceCount);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setLanded(nbt.getBoolean("Landed"));
        this.landedTicks = nbt.getInt("LandedTicks");
        this.bounceCount = nbt.getInt("BounceCount");
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof EffectZoneEntity || entity instanceof AnomalyEntity || entity instanceof ElementalAnomalyEntity) {
            return false;
        }
        return super.canHit(entity);
    }

    @Override
    protected double getGravity() {
        return 0.1;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (this.isLanded()) return;

        this.bounceCount++;
        Direction side = blockHitResult.getSide();
        Vec3d vel = this.getVelocity();

        if (side == Direction.DOWN) {
            this.setVelocity(vel.x * 0.1, -0.05, vel.z * 0.1);
            return;
        }

        if (side == Direction.UP && (this.bounceCount > 1 || vel.lengthSquared() < 0.01)) {
            this.setLanded(true);
            this.setVelocity(Vec3d.ZERO);
            this.setNoGravity(true);
            this.setPosition(this.getX(), blockHitResult.getPos().y + 0.01, this.getZ());
            return;
        }

        if (this.bounceCount > 1) {
            this.setVelocity(0, -0.05, 0);
            return;
        }

        double x = vel.x;
        double y = vel.y;
        double z = vel.z;

        if (side.getAxis() == Direction.Axis.X) {
            x = -x * 0.2;
            y *= 0.8;
            z *= 0.8;
        } else if (side.getAxis() == Direction.Axis.Y) {
            x *= 0.6;
            y = -y * 0.2;
            z *= 0.6;
        } else if (side.getAxis() == Direction.Axis.Z) {
            x *= 0.8;
            y *= 0.8;
            z = -z * 0.2;
        }

        this.setVelocity(x, y, z);

        if (!this.getWorld().isClient) {
            this.getWorld().playSound(null, this.getBlockPos(), StalkerMod.BOLT_HIT, SoundCategory.NEUTRAL, 0.8f, 1.2f + (this.random.nextFloat() * 0.4f));
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.isLanded()) return;

        Vec3d vel = this.getVelocity();
        this.setVelocity(vel.x * -0.05, -0.05, vel.z * -0.05);
    }

    @Override
    public void tick() {
        super.tick();

        boolean landedState = this.isLanded();

        if (!spinInitialized) {
            this.spinPitch = this.getPitch();
            this.spinYaw = this.getYaw();
            this.spinInitialized = true;
        }

        this.prevPitch = this.spinPitch;
        this.prevYaw = this.spinYaw;

        if (!landedState && this.getVelocity().lengthSquared() < 0.001 && this.age > 5) {
            this.setLanded(true);
            landedState = true;
        }

        if (!landedState) {
            this.spinPitch = (this.spinPitch + 45.0f) % 360.0f;
            this.spinYaw = (this.spinYaw + 25.0f) % 360.0f;
        } else {
            this.spinPitch = 90.0f;

            if (!this.getWorld().isClient) {
                BlockPos posBelow = this.getBlockPos().down();
                if (this.getWorld().getBlockState(posBelow).getCollisionShape(this.getWorld(), posBelow).isEmpty()) {
                    this.setLanded(false);
                    this.setNoGravity(false);
                    this.landedTicks = 0;
                } else {
                    this.setVelocity(Vec3d.ZERO);
                    this.landedTicks++;

                    if (this.landedTicks > 60) {
                        this.discard();
                    }
                }
            } else {
                this.setVelocity(Vec3d.ZERO);
            }
        }

        this.setPitch(this.spinPitch);
        this.setYaw(this.spinYaw);
    }
}