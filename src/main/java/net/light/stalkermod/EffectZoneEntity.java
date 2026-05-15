package net.light.stalkermod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity; 
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.List;

public class EffectZoneEntity extends Entity {
    public BlockPos pos1 = BlockPos.ORIGIN;
    public BlockPos pos2 = BlockPos.ORIGIN;
    public int type = 1;

    public float strength = 1.0f;

    private static final TrackedData<BlockPos> SYNC_P1 = DataTracker.registerData(EffectZoneEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<BlockPos> SYNC_P2 = DataTracker.registerData(EffectZoneEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Integer> SYNC_TYPE = DataTracker.registerData(EffectZoneEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SYNC_STRENGTH = DataTracker.registerData(EffectZoneEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public EffectZoneEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;        
        this.setNoGravity(true);   
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SYNC_P1, BlockPos.ORIGIN);
        builder.add(SYNC_P2, BlockPos.ORIGIN);
        builder.add(SYNC_TYPE, 1);
        builder.add(SYNC_STRENGTH, 1.0f);
    }

    public Box getZoneBox() {
        BlockPos p1 = this.getWorld().isClient() ? this.dataTracker.get(SYNC_P1) : this.pos1;
        BlockPos p2 = this.getWorld().isClient() ? this.dataTracker.get(SYNC_P2) : this.pos2;
        return new Box(p1.toCenterPos(), p2.toCenterPos()).expand(0.5);
    }

    @Override
    public void tick() {
        super.tick();
        this.calculateDimensions();

        
        
        
        if (!this.getWorld().isClient()) {
            
            
            
            if (this.pos1 != null) this.dataTracker.set(SYNC_P1, this.pos1);
            if (this.pos2 != null) this.dataTracker.set(SYNC_P2, this.pos2);
            this.dataTracker.set(SYNC_TYPE, this.type);
            this.dataTracker.set(SYNC_STRENGTH, this.strength);
        } else {
            
            this.pos1 = this.dataTracker.get(SYNC_P1);
            this.pos2 = this.dataTracker.get(SYNC_P2);
            this.type = this.dataTracker.get(SYNC_TYPE);
            this.strength = this.dataTracker.get(SYNC_STRENGTH);
        }

        
        
        
        if (this.pos1 == null || this.pos2 == null) {
            return;
        }

        
        
        
        Box fullZoneBox = getZoneBox();
        Vec3d center = fullZoneBox.getCenter();



        this.setPosition(center);

        
        
        
        if (!this.getWorld().isClient()) {
            if (this.age % 20 == 0) {
                List<LivingEntity> targets = this.getWorld().getEntitiesByClass(LivingEntity.class, fullZoneBox, e -> {
                    if (!e.isAlive()) return false;
                    if (e instanceof PlayerEntity player) {
                        return !player.isCreative() && !player.isSpectator();
                    }
                    return true;
                });

                for (LivingEntity target : targets) {
                    applyServerEffects(target, (net.minecraft.server.world.ServerWorld) this.getWorld());
                }
            }
        }

        
        
        

        
        
        if (this.getWorld().isClient()) {
            
            if (this.getRandom().nextFloat() < (0.05f * this.strength)) {
                double x = fullZoneBox.minX + this.getRandom().nextDouble() * (fullZoneBox.maxX - fullZoneBox.minX);
                double y = fullZoneBox.minY + this.getRandom().nextDouble() * (fullZoneBox.maxY - fullZoneBox.minY);
                double z = fullZoneBox.minZ + this.getRandom().nextDouble() * (fullZoneBox.maxZ - fullZoneBox.minZ);
                this.getWorld().addParticle(this.type == 1 ? net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER : net.minecraft.particle.ParticleTypes.WITCH, x, y, z, 0, 0, 0);
            }

            
            var player = net.minecraft.client.MinecraftClient.getInstance().player;
            if (player != null && player.isHolding(StalkerMod.ANOMALY_SPAWNER)) {
                drawBoxOutline(fullZoneBox);
            }
        }
    }

    private void applyServerEffects(LivingEntity entity, ServerWorld world) {
        float effectiveStrength = this.strength;

        if (entity instanceof PlayerEntity player) {
            if (this.type == 1) { 
                effectiveStrength += StalkerMod.getInventoryRadiation(player);
            }
            else if (this.type == 2) { 
                effectiveStrength += StalkerMod.getInventoryPsi(player);
            }
        }

        
        effectiveStrength = Math.max(0.0f, effectiveStrength);

        if (effectiveStrength == 0.0f) return; 

        float damage = (effectiveStrength * effectiveStrength) / 5.0f;
        entity.damage(world.getDamageSources().magic(), damage);

        if (type == 1) { 
            int radAmplifier = (int)(effectiveStrength / 2);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 60, radAmplifier, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 60, radAmplifier));
        } else { 
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 120, 0));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));

            if (effectiveStrength >= 3.0f) {
                int intervalSeconds = Math.max(1, 8 - (int)effectiveStrength);
                if (this.age % (intervalSeconds * 20) == 0) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 40, 0));
                }
            }
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!player.isCreative()) return ActionResult.PASS;
        ItemStack stack = player.getStackInHand(hand);

        if (stack.isEmpty() && player.isSneaking() && player.isCreative()) {
            if (!this.getWorld().isClient()) {
                this.discard();
                player.sendMessage(Text.literal("§7Область деактивирована"), true);
            }
            return ActionResult.SUCCESS;
        }

        if (stack.isOf(StalkerMod.ANOMALY_SPAWNER)) {
            int mode = stack.getOrDefault(StalkerMod.MODE_COMPONENT, 0);
            if (mode == 6) {
                if (!this.getWorld().isClient()) {
                    this.strength = Math.min(10.0f, this.strength + 1.0f);
                    this.dataTracker.set(SYNC_STRENGTH, this.strength);
                    player.sendMessage(Text.literal("§aСила зоны увеличена: §c" + this.strength), true);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() instanceof PlayerEntity player && player.isCreative()) {
            ItemStack stack = player.getMainHandStack();

            if (stack.isOf(StalkerMod.ANOMALY_SPAWNER)) {
                int mode = stack.getOrDefault(StalkerMod.MODE_COMPONENT, 0);
                if (mode == 6) {
                    this.strength = Math.max(1.0f, this.strength - 1.0f);
                    this.dataTracker.set(SYNC_STRENGTH, this.strength);
                    player.sendMessage(Text.literal("§eСила зоны уменьшена: §c" + this.strength), true);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.type = nbt.getInt("ZoneType");
        this.strength = nbt.getFloat("Strength");

        if (nbt.contains("P1X")) {
            this.pos1 = new BlockPos(nbt.getInt("P1X"), nbt.getInt("P1Y"), nbt.getInt("P1Z"));
        }
        if (nbt.contains("P2X")) {
            this.pos2 = new BlockPos(nbt.getInt("P2X"), nbt.getInt("P2Y"), nbt.getInt("P2Z"));
        }
    }


    
    @Override
    protected void writeCustomDataToNbt(net.minecraft.nbt.NbtCompound nbt) {
        nbt.putInt("ZoneType", this.type);
        nbt.putFloat("Strength", this.strength);

        
        if (this.pos1 != null) {
            nbt.putInt("P1X", this.pos1.getX());
            nbt.putInt("P1Y", this.pos1.getY());
            nbt.putInt("P1Z", this.pos1.getZ());
        }

        
        if (this.pos2 != null) {
            nbt.putInt("P2X", this.pos2.getX());
            nbt.putInt("P2Y", this.pos2.getY());
            nbt.putInt("P2Z", this.pos2.getZ());
        }
    }


    private void drawBoxOutline(Box b) {
        if (this.age % 4 != 0) return;
        World w = this.getWorld();
        double step = 1.0;

        for (double x = b.minX; x <= b.maxX; x += step) {
            w.addParticle(ParticleTypes.END_ROD, x, b.minY, b.minZ, 0,0,0);
            w.addParticle(ParticleTypes.END_ROD, x, b.maxY, b.minZ, 0,0,0);
            w.addParticle(ParticleTypes.END_ROD, x, b.minY, b.maxZ, 0,0,0);
            w.addParticle(ParticleTypes.END_ROD, x, b.maxY, b.maxZ, 0,0,0);
        }
        for (double y = b.minY; y <= b.maxY; y += step) {
            w.addParticle(ParticleTypes.END_ROD, b.minX, y, b.minZ, 0,0,0);
            w.addParticle(ParticleTypes.END_ROD, b.maxX, y, b.minZ, 0,0,0);
            w.addParticle(ParticleTypes.END_ROD, b.minX, y, b.maxZ, 0,0,0);
            w.addParticle(ParticleTypes.END_ROD, b.maxX, y, b.maxZ, 0,0,0);
        }
        for (double z = b.minZ; z <= b.maxZ; z += step) {
            w.addParticle(ParticleTypes.END_ROD, b.minX, b.minY, z, 0,0,0);
            w.addParticle(ParticleTypes.END_ROD, b.maxX, b.minY, z, 0,0,0);
            w.addParticle(ParticleTypes.END_ROD, b.minX, b.maxY, z, 0,0,0);
            w.addParticle(ParticleTypes.END_ROD, b.maxX, b.maxY, z, 0,0,0);
        }
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public net.minecraft.entity.EntityDimensions getDimensions(net.minecraft.entity.EntityPose pose) {
        
        if (this.getWorld().isClient()) {
            var player = net.minecraft.client.MinecraftClient.getInstance().player;
            
            if (player != null && player.isCreative()) {
                return net.minecraft.entity.EntityDimensions.fixed(1.0f, 1.0f);
            }
            
            return net.minecraft.entity.EntityDimensions.fixed(0.0f, 0.0f);
        }

        
        boolean adminNearby = false;
        for (PlayerEntity p : this.getWorld().getPlayers()) {
            if (p.isCreative() && p.squaredDistanceTo(this) <= 400.0) {
                adminNearby = true;
                break;
            }
        }
        return adminNearby ? net.minecraft.entity.EntityDimensions.fixed(1.0f, 1.0f) : net.minecraft.entity.EntityDimensions.fixed(0.0f, 0.0f);
    }
}