package net.light.stalkermod;

import net.minecraft.block.BlockState;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.List;
import net.minecraft.registry.entry.RegistryEntry;


public class ElementalAnomalyEntity extends Entity {
    public int type = 0;
    public float radius = 2.0f;
    public int cooldownTimer = 0;

    
    public int clientActiveTimer = 0;

    private static final TrackedData<Integer> SYNC_TYPE = DataTracker.registerData(ElementalAnomalyEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SYNC_RADIUS = DataTracker.registerData(ElementalAnomalyEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public ElementalAnomalyEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.noClip = true;
        this.setNoGravity(true);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SYNC_TYPE, 0);
        builder.add(SYNC_RADIUS, 2.0f);
    }

    @Override
    public boolean collidesWithStateAtPos(BlockPos pos, BlockState state) {
        return super.collidesWithStateAtPos(pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        this.calculateDimensions();


        
        
        
        if (this.getWorld().isClient()) {
            int currentType = this.dataTracker.get(SYNC_TYPE);
            float currentRadius = this.dataTracker.get(SYNC_RADIUS);

            spawnAmbientParticles(currentType, currentRadius);

            
            if (this.clientActiveTimer > 0) {
                this.clientActiveTimer--;
                spawnActiveParticles(currentType, currentRadius);
            }

            var player = net.minecraft.client.MinecraftClient.getInstance().player;
            if (player != null && player.isHolding(StalkerMod.ANOMALY_SPAWNER) && this.age % 5 == 0) {
                drawAnomalyOutline(currentType, currentRadius);
            }
            return;
        }

        
        
        
        
        
        
        ServerWorld world = (ServerWorld) this.getWorld();
        this.dataTracker.set(SYNC_TYPE, this.type);
        this.dataTracker.set(SYNC_RADIUS, this.radius);

        if (this.cooldownTimer > 0) {
            this.cooldownTimer--; 

            
            if (this.type == 1 && this.cooldownTimer > 20) {
                Box scanBox = new Box(this.getX() - radius, this.getY() - 1.0, this.getZ() - radius,
                        this.getX() + radius, this.getY() + 4.0f, this.getZ() + radius);

                List<Entity> activeTargets = world.getEntitiesByClass(Entity.class, scanBox, entity -> {
                    if (entity instanceof AnomalyEntity || entity instanceof ElementalAnomalyEntity || entity instanceof EffectZoneEntity) return false;
                    if (entity instanceof net.minecraft.entity.decoration.painting.PaintingEntity || entity instanceof net.minecraft.entity.decoration.ItemFrameEntity || entity instanceof net.minecraft.entity.decoration.LeashKnotEntity) return false;
                    if (entity instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) return false;

                    Vec3d center = this.getPos();
                    double dx = Math.abs(entity.getX() - center.x);
                    double dz = Math.abs(entity.getZ() - center.z);
                    return dx <= 0.7 && dz <= 0.7 && entity.getY() >= center.y - 0.5; 
                });

                for (Entity target : activeTargets) {
                    target.setOnFireFor(8); 
                    if (this.age % 10 == 0) {
                        target.damage(world.getDamageSources().inFire(), 4.0f);
                    }
                }
            } 


            if (this.type == 1) {
                
                if (this.cooldownTimer <= 60 && this.cooldownTimer >= 20) {
                    net.minecraft.util.math.BlockPos lightPos = this.getBlockPos();
                    net.minecraft.block.BlockState state = world.getBlockState(lightPos);

                    if (state.isOf(net.minecraft.block.Blocks.LIGHT)) {
                        if (this.cooldownTimer == 20) {
                            
                            world.removeBlock(lightPos, false);
                        } else {
                            
                            int targetLevel = (int) (15.0f * ((this.cooldownTimer - 20) / 40.0f));
                            targetLevel = Math.max(1, Math.min(15, targetLevel)); 

                            
                            if (state.get(net.minecraft.state.property.Properties.LEVEL_15) != targetLevel) {
                                world.setBlockState(lightPos, state.with(net.minecraft.state.property.Properties.LEVEL_15, targetLevel), 3);
                            }
                        }
                    }
                }
            }

            
            if (this.type == 2) {
                
                if (this.cooldownTimer <= 120 && this.cooldownTimer >= 105) {
                    net.minecraft.util.math.BlockPos lightPos = this.getBlockPos();
                    net.minecraft.block.BlockState state = world.getBlockState(lightPos);

                    if (state.isOf(net.minecraft.block.Blocks.LIGHT)) {
                        if (this.cooldownTimer == 105) {
                            
                            world.removeBlock(lightPos, false);
                        } else {
                            
                            int targetLevel = (int) (15.0f * ((this.cooldownTimer - 105) / 15.0f));
                            targetLevel = Math.max(1, Math.min(15, targetLevel));

                            if (state.get(net.minecraft.state.property.Properties.LEVEL_15) != targetLevel) {
                                world.setBlockState(lightPos, state.with(net.minecraft.state.property.Properties.LEVEL_15, targetLevel), 3);
                            }
                        }
                    }
                }
            }

            return; 
        }


        
        float searchH = (type == 1) ? 4.0f : 2.0f;
        Box scanBox = new Box(this.getX() - radius, this.getY() - 1.0, this.getZ() - radius,
                this.getX() + radius, this.getY() + searchH, this.getZ() + radius);

        
        List<Entity> targets = world.getEntitiesByClass(Entity.class, scanBox, entity -> {
            if (entity instanceof AnomalyEntity ||
                    entity instanceof ElementalAnomalyEntity ||
                    entity instanceof EffectZoneEntity) return false;

            if (entity instanceof net.minecraft.entity.decoration.painting.PaintingEntity ||
                    entity instanceof net.minecraft.entity.decoration.ItemFrameEntity ||
                    entity instanceof net.minecraft.entity.decoration.LeashKnotEntity) return false;

            if (entity instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) return false;

            Vec3d center = this.getPos();
            if (type == 0) return entity.squaredDistanceTo(center) <= radius * radius;
            if (type == 1) {
                double dx = Math.abs(entity.getX() - center.x);
                double dz = Math.abs(entity.getZ() - center.z);
                return dx <= 0.7 && dz <= 0.7 && entity.getY() >= center.y - 0.5;
            }
            if (type == 2) {
                double distSq = (entity.getX()-center.x)*(entity.getX()-center.x) + (entity.getZ()-center.z)*(entity.getZ()-center.z);
                return distSq <= radius * radius && Math.abs(entity.getY() - center.y) <= 1.5;
            }
            return false;
        });

        if (!targets.isEmpty()) {
            triggerAnomaly(world, targets);
        }
    }

    private void triggerAnomaly(ServerWorld world, List<Entity> targets) {
        world.sendEntityStatus(this, (byte) 3);

        for (Entity target : targets) {
            float damageAmount = 0.0f;
            if (type == 0) damageAmount = 10.0f;
            else if (type == 1) damageAmount = 16.0f;
            else if (type == 2) damageAmount = 19.0f;

            Vec3d vel = target.getVelocity();
            if (target.damage(world.getDamageSources().indirectMagic(this, this), damageAmount)) {
                target.setVelocity(vel);
                target.velocityModified = true;
            }

            if (target instanceof LivingEntity living) {
                if (type == 0) {
                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1));
                } else if (type == 1) {
                    target.setOnFireFor(8);
                }
            }
        }

        if (type == 0) {
            
            world.playSound(null, getX(), getY(), getZ(), RegistryEntry.of(StalkerMod.ACID_ACTIVATE), SoundCategory.BLOCKS, 2.0f, 1.2f);
            world.playSound(null, getX(), getY(), getZ(), RegistryEntry.of(StalkerMod.ACID_ACTIVATE), SoundCategory.BLOCKS, 2.0f, 1.2f);
            this.cooldownTimer = 80;
        } else if (type == 1) {
            world.playSound(null, getX(), getY(), getZ(), RegistryEntry.of(StalkerMod.BURNER_ACTIVATE), SoundCategory.BLOCKS, 2.0f, 1.0f);
            this.cooldownTimer = 260; 
            net.minecraft.util.math.BlockPos lightPos = this.getBlockPos();
            if (world.getBlockState(lightPos).isReplaceable()) {
                world.setBlockState(lightPos, net.minecraft.block.Blocks.LIGHT.getDefaultState()
                        .with(net.minecraft.state.property.Properties.LEVEL_15, 15), 3);
            }
        } else if (type == 2) {
            
            world.playSound(null, getX(), getY(), getZ(), RegistryEntry.of(StalkerMod.ELECTRO_ACTIVATE), SoundCategory.BLOCKS, 3.0f, 1.0f);
            this.cooldownTimer = 120;

            
            net.minecraft.util.math.BlockPos lightPos = this.getBlockPos();
            if (world.getBlockState(lightPos).isReplaceable()) {
                world.setBlockState(lightPos, net.minecraft.block.Blocks.LIGHT.getDefaultState()
                        .with(net.minecraft.state.property.Properties.LEVEL_15, 15), 3);
            }
        }
    }

    
    @Override
    public void handleStatus(byte status) {
        if (status == 3) {
            int t = this.dataTracker.get(SYNC_TYPE);
            float r = this.dataTracker.get(SYNC_RADIUS);

            if (t == 0) this.clientActiveTimer = 30;
            if (t == 1) this.clientActiveTimer = 240; 
            if (t == 2) this.clientActiveTimer = 15;

            if (t == 0) {
                var player = net.minecraft.client.MinecraftClient.getInstance().player;
                if (player != null && player.squaredDistanceTo(this.getPos()) <= r * r) ZoneOverlay.acidFlash = 0.8f;
            } else if (t == 2) {
                World w = this.getWorld();

                
                for (int i = 0; i < 5; i++) {
                    w.addParticle(ParticleTypes.FLASH,
                            getX() + (w.random.nextDouble() - 0.5) * 1.5,
                            getY() + 0.5 + (w.random.nextDouble() - 0.5) * 1.5,
                            getZ() + (w.random.nextDouble() - 0.5) * 1.5,
                            0, 0, 0);
                }

                
                int numBolts = (int)(r * r * 4) + w.random.nextInt((int)(r * 2) + 1);

                
                numBolts = Math.max(15, numBolts);

                for (int b = 0; b < numBolts; b++) {
                    
                    double angle = w.random.nextDouble() * Math.PI * 2; 
                    double distance = w.random.nextDouble() * r;        

                    
                    double currentX = getX() + Math.cos(angle) * distance;
                    double currentY = getY() + w.random.nextDouble() * 1.5; 
                    double currentZ = getZ() + Math.sin(angle) * distance;

                    
                    if (w.random.nextFloat() < 0.2f) {
                        w.addParticle(ParticleTypes.FLASH, currentX, currentY, currentZ, 0, 0, 0);
                    }

                    
                    double dirX = (w.random.nextDouble() - 0.5) * 2.0;
                    double dirY = (w.random.nextDouble() - 0.5) * 2.0;
                    double dirZ = (w.random.nextDouble() - 0.5) * 2.0;

                    
                    int segments = 2 + w.random.nextInt(4);

                    for (int s = 0; s < segments; s++) {
                        
                        if (w.random.nextFloat() < 0.8f) {
                            
                            dirX = (w.random.nextDouble() - 0.5) * 4.0;
                            dirY = (w.random.nextDouble() - 0.5) * 4.0;
                            dirZ = (w.random.nextDouble() - 0.5) * 4.0;
                        }

                        
                        double nextX = currentX + dirX * 0.25;
                        double nextY = currentY + dirY * 0.25;
                        double nextZ = currentZ + dirZ * 0.25;

                        
                        int particleDensity = 3;
                        for (int i = 0; i <= particleDensity; i++) {
                            double fraction = (double) i / particleDensity;
                            double px = currentX + (nextX - currentX) * fraction;
                            double py = currentY + (nextY - currentY) * fraction;
                            double pz = currentZ + (nextZ - currentZ) * fraction;

                            w.addParticle(ParticleTypes.END_ROD, px, py, pz, 0, 0, 0);

                            
                            if (w.random.nextBoolean()) {
                                w.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz,
                                        (w.random.nextDouble()-0.5)*0.15,
                                        (w.random.nextDouble()-0.5)*0.15,
                                        (w.random.nextDouble()-0.5)*0.15);
                            }
                        }

                        
                        currentX = nextX;
                        currentY = nextY;
                        currentZ = nextZ;
                    }
                }
            }
        }
    }

    
    private void spawnActiveParticles(int type, float r) {
        World w = this.getWorld();
        if (type == 0) {
            
            for(int i=0; i<4; i++) {
                double px = getX() + (w.random.nextDouble()-0.5)*r;
                double pz = getZ() + (w.random.nextDouble()-0.5)*r;
                w.addParticle(ParticleTypes.SNEEZE, px, getY() + 0.2 + w.random.nextDouble()*0.5, pz,
                        0, 0.02 + w.random.nextDouble()*0.02, 0);
            }
            
            if (this.age % 2 == 0) {
                for(int i=0; i<4; i++) {
                    w.addParticle(ParticleTypes.GLOW,
                            getX() + (w.random.nextDouble()-0.5)*r,
                            getY() + 0.1,
                            getZ() + (w.random.nextDouble()-0.5)*r,
                            0, 0, 0);
                }
            }
        } else if (type == 1) {
            
            
            double fadeMultiplier = 1.0;
            if (this.clientActiveTimer <= 40) {
                fadeMultiplier = this.clientActiveTimer / 40.0;
            }

            
            double time = this.age * 0.3;
            
            double waveX = Math.sin(time) * 0.08 * fadeMultiplier;
            double waveZ = Math.cos(time * 0.8) * 0.08 * fadeMultiplier;

            
            int particles = (int) (15 * fadeMultiplier);
            
            if (particles == 0 && w.random.nextDouble() < fadeMultiplier) {
                particles = 1;
            }

            for (int i = 0; i < particles; i++) {
                double px = getX() + (w.random.nextDouble() - 0.5) * 0.15;
                double py = getY() + w.random.nextDouble() * 0.5;
                double pz = getZ() + (w.random.nextDouble() - 0.5) * 0.15;

                double motionX = waveX + (w.random.nextDouble() - 0.5) * 0.015;
                
                
                double motionY = (0.4 + w.random.nextDouble() * 0.3) * fadeMultiplier;
                double motionZ = waveZ + (w.random.nextDouble() - 0.5) * 0.015;

                w.addParticle(ParticleTypes.FLAME, px, py, pz, motionX, motionY, motionZ);

                
                if (w.random.nextFloat() < 0.05f * fadeMultiplier) {
                    w.addParticle(ParticleTypes.LAVA, px, py, pz, motionX, motionY * 1.2, motionZ);
                }
            }

            
            if (this.age % 2 == 0 && fadeMultiplier > 0.2) {
                w.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        getX() + (w.random.nextDouble() - 0.5) * 0.2,
                        getY() + 2.5 * fadeMultiplier + w.random.nextDouble(),
                        getZ() + (w.random.nextDouble() - 0.5) * 0.2,
                        waveX, 0.2 * fadeMultiplier, waveZ);
            }

            
            if (this.age % 2 == 0) {
                w.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        getX() + (w.random.nextDouble() - 0.5) * 0.2,
                        getY() + 2.5 + w.random.nextDouble(),
                        getZ() + (w.random.nextDouble() - 0.5) * 0.2,
                        waveX, 0.2, waveZ);
            }
        } else if (type == 2) {
            
            for (int i = 0; i < 15; i++) {
                double px = getX() + (w.random.nextDouble() - 0.5) * 2.5;
                double py = getY() + 0.5 + (w.random.nextDouble() - 0.5) * 2.5;
                double pz = getZ() + (w.random.nextDouble() - 0.5) * 2.5;

                w.addParticle(ParticleTypes.GLOW, px, py, pz, 0, 0, 0);
            }

            
            if (this.age % 2 == 0) {
                
                w.addParticle(ParticleTypes.FLASH,
                        getX() + (w.random.nextDouble() - 0.5),
                        getY() + 0.5,
                        getZ() + (w.random.nextDouble() - 0.5), 0, 0, 0);

                
                w.addParticle(ParticleTypes.END_ROD, getX(), getY() + 0.5, getZ(), 0, 0, 0);
            }
        }
    }

    private void spawnAmbientParticles(int type, float r) {
        World w = this.getWorld();
        if (type == 0) {
            
            if (w.random.nextFloat() < (0.08f * r)) {
                w.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        getX() + (w.random.nextDouble() - 0.5) * r,
                        getY() + 0.1,
                        getZ() + (w.random.nextDouble() - 0.5) * r,
                        0, 0.01, 0);
            }
        } else if (type == 1) {
            
            if (w.random.nextInt(20) == 0) {
                w.addParticle(ParticleTypes.ASH, getX()+(w.random.nextDouble()-0.5)*0.6, getY()+3.5, getZ()+(w.random.nextDouble()-0.5)*0.6, 0, -0.05, 0);
            }
            
            w.playSound(null, getX(), getY(), getZ(), RegistryEntry.of(StalkerMod.BURNER_IDLE), SoundCategory.AMBIENT, 0.6f, 0.8f + w.random.nextFloat() * 0.4f);
        } else if (type == 2) {
            
            if (w.random.nextInt(4) == 0) {
                double a = w.random.nextDouble()*Math.PI*2;
                double d = w.random.nextDouble()*r;
                w.addParticle(ParticleTypes.ELECTRIC_SPARK, getX()+Math.cos(a)*d, getY()+0.1, getZ()+Math.sin(a)*d, 0, 0, 0);
            }
            
            if (w.random.nextInt(60) == 0) {
                w.playSound(null, getX(), getY(), getZ(), RegistryEntry.of(StalkerMod.ELECTRO_IDLE), SoundCategory.AMBIENT, 0.5f, 0.9f + w.random.nextFloat() * 0.2f); }
        }
    }

    private void drawAnomalyOutline(int type, float r) {
        World w = this.getWorld();
        if (type == 0) { 
            for (int i = 0; i < 15; i++) {
                double a = 2.0 * Math.PI * i / 15;
                
                if (Math.sin(a) >= 0) {
                    w.addParticle(ParticleTypes.HAPPY_VILLAGER, getX() + Math.cos(a)*r, getY() + Math.sin(a)*r, getZ(), 0,0,0);
                    w.addParticle(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + Math.sin(a)*r, getZ() + Math.cos(a)*r, 0,0,0);
                }
                
                w.addParticle(ParticleTypes.HAPPY_VILLAGER, getX() + Math.cos(a)*r, getY(), getZ() + Math.sin(a)*r, 0,0,0);
            }
        } else if (type == 1) { 
            for(double y=0; y<=3.5; y+=0.7) w.addParticle(ParticleTypes.FLAME, getX(), getY()+y, getZ(), 0,0,0);
        } else if (type == 2) { 
            for (int i=0; i<20; i++) {
                double a = 2.0*Math.PI*i/20;
                w.addParticle(ParticleTypes.GLOW, getX()+Math.cos(a)*r, getY()+0.1, getZ()+Math.sin(a)*r, 0,0,0);
            }
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        
        if (player.isSneaking() && player.getStackInHand(hand).isEmpty()) {
            if (!this.getWorld().isClient()) {
                this.discard();
                player.sendMessage(Text.literal("§cАномалия ликвидирована"), true);
            }
            return ActionResult.SUCCESS;
        }

        
        if (player.isCreative()) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.isOf(StalkerMod.ANOMALY_SPAWNER)) {
                int mode = stack.getOrDefault(StalkerMod.MODE_COMPONENT, 0);
                if (mode == 6) {
                    if (!this.getWorld().isClient()) {
                        this.radius = Math.min(10.0f, this.radius + 1.0f);
                        this.dataTracker.set(SYNC_RADIUS, this.radius);
                        player.sendMessage(Text.literal("§bРадиус аномалии: §f" + this.radius), true);
                    }
                    return ActionResult.SUCCESS;
                }
            }
        }

        
        return super.interact(player, hand);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() instanceof PlayerEntity player && player.isCreative()) {
            ItemStack stack = player.getMainHandStack();
            if (stack.isOf(StalkerMod.ANOMALY_SPAWNER)) {
                int mode = stack.getOrDefault(StalkerMod.MODE_COMPONENT, 0);
                if (mode == 6) {
                    this.radius = Math.max(1.0f, this.radius - 1.0f);
                    this.dataTracker.set(SYNC_RADIUS, this.radius);
                    player.sendMessage(Text.literal("§bРадиус аномалии: §f" + this.radius), true);
                    return false;
                }
            }
        }
        return false;
    }

    @Override public boolean canHit() { return !this.isRemoved(); }

    @Override
    protected void readCustomDataFromNbt(net.minecraft.nbt.NbtCompound nbt) {
        this.type = nbt.getInt("TrapType");
        this.radius = nbt.getFloat("TrapRadius");
    }

    @Override
    protected void writeCustomDataToNbt(net.minecraft.nbt.NbtCompound nbt) {
        nbt.putInt("TrapType", this.type);
        nbt.putFloat("TrapRadius", this.radius);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }
    @Override
    public void remove(RemovalReason reason) {
        
        if (!this.getWorld().isClient() && (this.type == 1 || this.type == 2)) {
            net.minecraft.util.math.BlockPos lightPos = this.getBlockPos();
            if (this.getWorld().getBlockState(lightPos).isOf(net.minecraft.block.Blocks.LIGHT)) {
                this.getWorld().removeBlock(lightPos, false);
            }
        }
        super.remove(reason);
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