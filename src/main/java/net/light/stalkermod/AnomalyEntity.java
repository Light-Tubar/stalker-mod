package net.light.stalkermod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.List;
import net.minecraft.registry.entry.RegistryEntry;


public class AnomalyEntity extends Entity {
    private int triggerTimer = 0;
    private boolean isTriggered = false;
    private int cooldown = 0;

    public float radius = 3.0f;
    public float damage = 16.0f;
    public float repulsionForce = 0.5f;

    public AnomalyEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;
        this.setNoGravity(true);
        this.intersectionChecked = true;
    }

    private static final net.minecraft.entity.data.TrackedData<Float> SYNC_RADIUS = net.minecraft.entity.data.DataTracker.registerData(AnomalyEntity.class, net.minecraft.entity.data.TrackedDataHandlerRegistry.FLOAT);
    private static final net.minecraft.entity.data.TrackedData<Boolean> SYNC_READY = net.minecraft.entity.data.DataTracker.registerData(AnomalyEntity.class, net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN);
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SYNC_RADIUS, 3.0f);
        builder.add(SYNC_READY, true); 
    }


    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!player.isCreative()) return ActionResult.PASS;
        ItemStack stack = player.getStackInHand(hand);

        if (stack.isEmpty() && player.isSneaking()) {
            if (!this.getWorld().isClient()) {
                this.discard();
                player.sendMessage(Text.translatable("message.stalkermod.anomaly_discharged"), true);
            }
            return ActionResult.SUCCESS;
        }

        if (stack.isOf(StalkerMod.ANOMALY_SPAWNER)) {
            int mode = stack.getOrDefault(StalkerMod.MODE_COMPONENT, 0);
            if (mode == 6) {
                if (!this.getWorld().isClient()) {
                    if (player.isSneaking()) {
                        this.damage = (this.damage >= 40.0f) ? 2.0f : this.damage + 2.0f;
                        player.sendMessage(Text.translatable("message.stalkermod.anomaly_damage_set", (this.damage / 2)), true);
                    } else {
                        this.radius = (this.radius >= 15.0f) ? 2.0f : this.radius + 1.0f;
                        player.sendMessage(Text.translatable("message.stalkermod.anomaly_radius_set", this.radius), true);
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();
        this.calculateDimensions();

        

        if (this.getWorld().isClient()) {
            if (this.dataTracker.get(SYNC_READY)) {
                spawnIdleDebris();


                if (this.age % 80 == 0) {
                    this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                            RegistryEntry.of(StalkerMod.TRAMPOLINE_IDLE), net.minecraft.sound.SoundCategory.AMBIENT,
                            0.6f, 0.8f + this.random.nextFloat() * 0.4f);
                }
            }

            float currentRadius = this.dataTracker.get(SYNC_RADIUS);
            var player = net.minecraft.client.MinecraftClient.getInstance().player;

            if (player != null && player.isHolding(StalkerMod.ANOMALY_SPAWNER)) {
                if (this.age % 5 == 0) drawSphereOutline(currentRadius);
            }
            return;
        }

        
        ServerWorld world = (ServerWorld) this.getWorld();
        this.dataTracker.set(SYNC_RADIUS, this.radius);

        
        this.dataTracker.set(SYNC_READY, this.cooldown <= 0 && !this.isTriggered);

        if (cooldown > 0) {
            cooldown--;
            return;
        }


        Box scanBox = new Box(this.getPos().add(-radius, -radius, -radius),
                this.getPos().add(radius, radius, radius));

        List<Entity> targets = world.getOtherEntities(this, scanBox, entity -> {

            if (entity instanceof AnomalyEntity ||
                    entity instanceof ElementalAnomalyEntity ||
                    entity instanceof EffectZoneEntity) return false;

            
            if (entity instanceof net.minecraft.entity.decoration.painting.PaintingEntity ||
                    entity instanceof net.minecraft.entity.decoration.ItemFrameEntity ||
                    entity instanceof net.minecraft.entity.decoration.LeashKnotEntity) return false;

            
            if (entity.squaredDistanceTo(this.getPos()) > radius * radius) return false;

            
            if (entity instanceof PlayerEntity player) {
                return !player.isCreative() && !player.isSpectator();
            }

            
            
            
            return true;
        });

        if (!isTriggered) {
            if (!targets.isEmpty()) {
                isTriggered = true;
                triggerTimer = 40;
                
                world.playSound(null, this.getX(), this.getY(), this.getZ(),
                        RegistryEntry.of(StalkerMod.TRAMPOLINE_ACTIVATE),
                        net.minecraft.sound.SoundCategory.BLOCKS, 2.0f, 1.0f);
            }
        } else {
            triggerTimer--;
            spawnDistortionEffect(world);

            Vec3d center = this.getPos().add(0, 0.5, 0);
            double pullMultiplier = (40.0 - triggerTimer) / 40.0;
            double maxPullForce = 0.05;

            for (Entity entity : targets) {
                Vec3d direction = center.subtract(entity.getPos()).normalize();
                Vec3d pullVec = direction.multiply(maxPullForce * pullMultiplier);
                entity.addVelocity(pullVec.x, pullVec.y, pullVec.z);
                entity.velocityModified = true;
                entity.velocityDirty = true;
            }

            if (triggerTimer <= 0) {
                performImplosion(world, targets);
            }
        }
    }

    private void spawnIdleDebris() {
        World w = this.getWorld();

        
        if (w.random.nextFloat() < 0.5f) return;

        float currentRadius = this.dataTracker.get(SYNC_RADIUS);

        double angle = w.random.nextDouble() * 2 * Math.PI;
        double dist = w.random.nextDouble() * currentRadius;

        double px = getX() + Math.cos(angle) * dist;
        
        double py = getY() + w.random.nextDouble() * (currentRadius * 0.5);
        double pz = getZ() + Math.sin(angle) * dist;

        double pullX = getX() - px;
        double pullZ = getZ() - pz;

        
        double motionX = (pullX * 0.01) + (pullZ * 0.03);
        double motionY = 0.01 + w.random.nextDouble() * 0.03; 
        double motionZ = (pullZ * 0.01) - (pullX * 0.03);

        w.addParticle(ParticleTypes.WHITE_ASH, px, py, pz, motionX, motionY, motionZ);

        
        if (w.random.nextFloat() < 0.05f) {
            w.addParticle(ParticleTypes.CRIT, px, py, pz, motionX, motionY * 1.5, motionZ);
        }
    }

    private void drawSphereOutline(float r) {
        World w = this.getWorld();
        int segments = 15;
        for (int i = 0; i < segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            double cos = Math.cos(angle) * r;
            double sin = Math.sin(angle) * r;
            double cx = this.getX();
            double cy = this.getY() + 0.5;
            double cz = this.getZ();

            
            w.addParticle(ParticleTypes.SOUL_FIRE_FLAME, cx + cos, cy + sin, cz, 0, 0, 0);
            w.addParticle(ParticleTypes.SOUL_FIRE_FLAME, cx + cos, cy, cz + sin, 0, 0, 0);
            w.addParticle(ParticleTypes.SOUL_FIRE_FLAME, cx, cy + cos, cz + sin, 0, 0, 0);
        }
    }

    private void performImplosion(ServerWorld world, List<Entity> targets) {
        Vec3d center = this.getPos();

        
        world.spawnParticles(net.minecraft.particle.ParticleTypes.EXPLOSION, center.x, center.y + 1, center.z, 3, 0.5, 0.5, 0.5, 0.1);
        world.playSound(null, center.x, center.y, center.z,
                net.minecraft.sound.SoundEvents.ENTITY_BREEZE_WIND_BURST,
                net.minecraft.sound.SoundCategory.HOSTILE, 2.0f, 0.8f);

        for (Entity entity : targets) {

            if (entity instanceof net.minecraft.entity.ItemEntity || entity instanceof net.minecraft.entity.projectile.ProjectileEntity) {
                world.spawnParticles(net.minecraft.particle.ParticleTypes.POOF, entity.getX(), entity.getY(), entity.getZ(), 5, 0.1, 0.1, 0.1, 0.05);
                entity.discard();
                continue;
            }

            entity.requestTeleport(center.x, center.y, center.z);

            if (entity instanceof LivingEntity living) {
                living.damage(this.getWorld().getDamageSources().indirectMagic(this, this), this.damage);
            }

            entity.setVelocity(0, repulsionForce * 2.5, 0);
            entity.velocityModified = true;
            entity.velocityDirty = true;

        }

        isTriggered = false;
        cooldown = 60; 
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() instanceof PlayerEntity player && player.isCreative()) {
            ItemStack stack = player.getMainHandStack();

            if (stack.isOf(StalkerMod.ANOMALY_SPAWNER)) {
                int mode = stack.getOrDefault(StalkerMod.MODE_COMPONENT, 0);
                if (mode == 6) {
                    this.repulsionForce = (this.repulsionForce >= 4.0f) ? 0.5f : this.repulsionForce + 0.5f;
                    player.sendMessage(Text.translatable("message.stalkermod.anomaly_repulsion_set", this.repulsionForce), true);
                    return false;
                }
            }

            this.discard();
            return true;
        }
        return false;
    }

    
    @Override
    public boolean isFireImmune() {
        return true;
    }

    
    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    protected void writeCustomDataToNbt(net.minecraft.nbt.NbtCompound nbt) {
        nbt.putFloat("AnomalyRadius", this.radius);
        nbt.putFloat("AnomalyDamage", this.damage);
        nbt.putFloat("AnomalyForce", this.repulsionForce);
    }

    @Override
    protected void readCustomDataFromNbt(net.minecraft.nbt.NbtCompound nbt) {
        if (nbt.contains("AnomalyRadius")) {
            this.radius = nbt.getFloat("AnomalyRadius");
        }
        if (nbt.contains("AnomalyDamage")) {
            this.damage = nbt.getFloat("AnomalyDamage");
        }
        if (nbt.contains("AnomalyForce")) {
            this.repulsionForce = nbt.getFloat("AnomalyForce");
        }
    }

    private void spawnDistortionEffect(ServerWorld world) {
        int density = 15;
        for (int i = 0; i < density; i++) {
            double dx = (Math.random() - 0.5) * 2 * radius;
            double dy = (Math.random() - 0.5) * 2 * radius;
            double dz = (Math.random() - 0.5) * 2 * radius;
            if (dx*dx + dy*dy + dz*dz <= radius*radius) {
                world.spawnParticles(ParticleTypes.CLOUD, this.getX() + dx, this.getY() + 1 + dy, this.getZ() + dz, 1, 0, 0, 0, 0.10);
            }
        }
    }

    @Override
    public net.minecraft.entity.EntityDimensions getDimensions(net.minecraft.entity.EntityPose pose) {
        if (this.getWorld().isClient()) {
            var player = net.minecraft.client.MinecraftClient.getInstance().player;
            if (player != null && (player.isCreative() || player.isSpectator())) {
                return net.minecraft.entity.EntityDimensions.fixed(1.0f, 1.0f);
            }
            return net.minecraft.entity.EntityDimensions.fixed(0.0f, 0.0f);
        }
        return net.minecraft.entity.EntityDimensions.fixed(1.0f, 1.0f);
    }
}