package net.light.stalkermod;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.sound.SoundEvent;
import net.light.stalkermod.item.BoltItem;
import net.light.stalkermod.entity.BoltEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class StalkerMod implements ModInitializer {
    public static final String MOD_ID = "stalker-mod";
    public static final Item BOLT_ITEM = new BoltItem(new Item.Settings().maxCount(64));

    public static final EntityType<BoltEntity> BOLT_ENTITY_TYPE;

    static {
        BOLT_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "bolt_entity"),
                FabricEntityTypeBuilder.<BoltEntity>create(SpawnGroup.MISC, BoltEntity::new)
                        .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                        .trackRangeChunks(4)
                        .trackedUpdateRate(10)
                        .build()
        );
    }

    public static final GameRules.Key<GameRules.IntRule> ANOMALY_SPAWN_MULTIPLIER = GameRuleRegistry.register(
            "anomalySpawnMultiplier",
            GameRules.Category.SPAWNING,
            GameRuleFactory.createIntRule(100)
    );

    public static final Identifier BOLT_THROW_ID = Identifier.of(MOD_ID, "bolt_throw");
    public static final SoundEvent BOLT_THROW = Registry.register(Registries.SOUND_EVENT, BOLT_THROW_ID, SoundEvent.of(BOLT_THROW_ID));

    public static final Identifier BOLT_HIT_ID = Identifier.of(MOD_ID, "bolt_hit");
    public static final SoundEvent BOLT_HIT = Registry.register(Registries.SOUND_EVENT, BOLT_HIT_ID, SoundEvent.of(BOLT_HIT_ID));

    public static final net.minecraft.util.Identifier EMISSION_EARTHQUAKE_ID = net.minecraft.util.Identifier.of("stalker-mod", "emission_earthquake");
    public static final net.minecraft.sound.SoundEvent EMISSION_EARTHQUAKE = net.minecraft.registry.Registry.register(
            net.minecraft.registry.Registries.SOUND_EVENT,
            EMISSION_EARTHQUAKE_ID,
            net.minecraft.sound.SoundEvent.of(EMISSION_EARTHQUAKE_ID)
    );
    public static final net.minecraft.util.Identifier EMISSION_THUNDER_ID = net.minecraft.util.Identifier.of("stalker-mod", "emission_thunder");
    public static final net.minecraft.sound.SoundEvent EMISSION_THUNDER = net.minecraft.registry.Registry.register(
            net.minecraft.registry.Registries.SOUND_EVENT,
            EMISSION_THUNDER_ID,
            net.minecraft.sound.SoundEvent.of(EMISSION_THUNDER_ID)
    );

    public static final Identifier ACID_ACTIVATE_ID = Identifier.of(MOD_ID, "acid_activate");
    public static final SoundEvent ACID_ACTIVATE = SoundEvent.of(ACID_ACTIVATE_ID);
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final net.minecraft.util.Identifier GEIGER_1_ID = net.minecraft.util.Identifier.of(MOD_ID, "geiger_1");
    public static final net.minecraft.sound.SoundEvent GEIGER_1 = net.minecraft.sound.SoundEvent.of(GEIGER_1_ID);

    public static final net.minecraft.util.Identifier GEIGER_2_ID = net.minecraft.util.Identifier.of(MOD_ID, "geiger_2");
    public static final net.minecraft.sound.SoundEvent GEIGER_2 = net.minecraft.sound.SoundEvent.of(GEIGER_2_ID);

    public static final net.minecraft.util.Identifier GEIGER_3_ID = net.minecraft.util.Identifier.of(MOD_ID, "geiger_3");
    public static final net.minecraft.sound.SoundEvent GEIGER_3 = net.minecraft.sound.SoundEvent.of(GEIGER_3_ID);

    public static final net.minecraft.util.Identifier GEIGER_4_ID = net.minecraft.util.Identifier.of(MOD_ID, "geiger_4");
    public static final net.minecraft.sound.SoundEvent GEIGER_4 = net.minecraft.sound.SoundEvent.of(GEIGER_4_ID);

    public static final Identifier EMISSION_SIREN_ID = Identifier.of(MOD_ID, "emission_siren");
    public static final SoundEvent EMISSION_SIREN = SoundEvent.of(EMISSION_SIREN_ID);

    public static final Identifier EMISSION_WIND_ID = Identifier.of(MOD_ID, "emission_wind");
    public static final SoundEvent EMISSION_WIND = SoundEvent.of(EMISSION_WIND_ID);

    public static final Identifier EMISSION_BLOWOUT_ID = Identifier.of(MOD_ID, "emission_blowout");
    public static final SoundEvent EMISSION_BLOWOUT = SoundEvent.of(EMISSION_BLOWOUT_ID);

    public static final Identifier TRAMPOLINE_IDLE_ID = Identifier.of(MOD_ID, "trampoline_idle");
    public static final SoundEvent TRAMPOLINE_IDLE = SoundEvent.of(TRAMPOLINE_IDLE_ID);
    public static final Identifier TRAMPOLINE_ACTIVATE_ID = Identifier.of(MOD_ID, "trampoline_activate");
    public static final SoundEvent TRAMPOLINE_ACTIVATE = SoundEvent.of(TRAMPOLINE_ACTIVATE_ID);

    public static final Identifier BURNER_IDLE_ID = Identifier.of(MOD_ID, "burner_idle");
    public static final SoundEvent BURNER_IDLE = SoundEvent.of(BURNER_IDLE_ID);
    public static final Identifier BURNER_ACTIVATE_ID = Identifier.of(MOD_ID, "burner_activate");
    public static final SoundEvent BURNER_ACTIVATE = SoundEvent.of(BURNER_ACTIVATE_ID);

    public static final Identifier ELECTRO_IDLE_ID = Identifier.of(MOD_ID, "electro_idle");
    public static final SoundEvent ELECTRO_IDLE = SoundEvent.of(ELECTRO_IDLE_ID);
    public static final Identifier ELECTRO_ACTIVATE_ID = Identifier.of(MOD_ID, "electro_activate");
    public static final SoundEvent ELECTRO_ACTIVATE = SoundEvent.of(ELECTRO_ACTIVATE_ID);

    public static final Identifier HEARTBEAT_ID = Identifier.of(MOD_ID, "heartbeat");
    public static final SoundEvent HEARTBEAT = SoundEvent.of(HEARTBEAT_ID);
    public static final Identifier TINNITUS_ID = Identifier.of(MOD_ID, "tinnitus");
    public static final SoundEvent TINNITUS = SoundEvent.of(TINNITUS_ID);

    public static final EntityType<ElementalAnomalyEntity> ELEMENTAL_ANOMALY = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(MOD_ID, "elemental_anomaly"),
            EntityType.Builder.<ElementalAnomalyEntity>create(ElementalAnomalyEntity::new, SpawnGroup.MISC).dimensions(1.0f, 1.0f).build("elemental_anomaly"));

    public static final ComponentType<Integer> MODE_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE, Identifier.of(MOD_ID, "mode"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static final EntityType<AnomalyEntity> ANOMALY_ENTITY = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(MOD_ID, "anomaly"),
            EntityType.Builder.<AnomalyEntity>create(AnomalyEntity::new, SpawnGroup.MISC).dimensions(1.0f, 1.0f).build("anomaly")
    );

    public static final EntityType<EffectZoneEntity> EFFECT_ZONE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(MOD_ID, "effect_zone"),
            EntityType.Builder.<EffectZoneEntity>create(EffectZoneEntity::new, SpawnGroup.MISC).dimensions(1.0f, 1.0f).build("effect_zone")
    );

    public static final net.minecraft.world.gen.feature.Feature<net.minecraft.world.gen.feature.DefaultFeatureConfig> ANOMALY_FEATURE =
            new AnomalyFeature(net.minecraft.world.gen.feature.DefaultFeatureConfig.CODEC);

    public static final Item ANOMALY_SPAWNER = new Item(new Item.Settings().maxCount(1)) {

        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            World world = context.getWorld();
            PlayerEntity user = context.getPlayer();
            if (user == null || !user.isCreative()) return ActionResult.PASS;

            ItemStack stack = context.getStack();
            int mode = stack.getOrDefault(MODE_COMPONENT, 0);

            if (!world.isClient()) {
                BlockPos pos = context.getBlockPos();
                NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

                if (mode == 0) {
                    AnomalyEntity anomaly = new AnomalyEntity(ANOMALY_ENTITY, world);
                    anomaly.setPosition(context.getHitPos().add(0, 0.1, 0));
                    world.spawnEntity(anomaly);

                    user.sendMessage(Text.translatable("message.stalkermod.trampoline_placed"), true);
                    return ActionResult.SUCCESS;
                }

                if (mode >= 1 && mode <= 3) {
                    ElementalAnomalyEntity trap = new ElementalAnomalyEntity(ELEMENTAL_ANOMALY, world);
                    trap.setPosition(context.getHitPos().add(0, 0.1, 0));
                    trap.type = mode - 1;

                    if (trap.type == 0) trap.radius = 2.0f;
                    if (trap.type == 2) trap.radius = 4.0f;

                    world.spawnEntity(trap);

                    user.sendMessage(Text.translatable("message.stalkermod.anomaly_placed"), true);
                    return ActionResult.SUCCESS;
                }

                if (mode == 4 || mode == 5) {
                    nbt.putInt("P2X", pos.getX());
                    nbt.putInt("P2Y", pos.getY());
                    nbt.putInt("P2Z", pos.getZ());
                    stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

                    user.sendMessage(Text.translatable("message.stalkermod.point2_set"), true);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.SUCCESS;
        }

        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            ItemStack itemStack = user.getStackInHand(hand);

            if (!user.isCreative()) return TypedActionResult.pass(itemStack);

            int mode = itemStack.getOrDefault(MODE_COMPONENT, 0);

            if (user.isSneaking()) {
                if (!world.isClient()) {
                    mode = (mode + 1) % 7;
                    itemStack.set(MODE_COMPONENT, mode);

                    String[] modeKeys = {
                            "message.stalkermod.mode.trampoline",
                            "message.stalkermod.mode.acid",
                            "message.stalkermod.mode.burner",
                            "message.stalkermod.mode.electro",
                            "message.stalkermod.mode.radiation",
                            "message.stalkermod.mode.psi",
                            "message.stalkermod.mode.setting"
                    };

                    user.sendMessage(Text.translatable("message.stalkermod.mode_format", Text.translatable(modeKeys[mode])), true);
                }
                return TypedActionResult.success(itemStack);
            }

            if ((mode == 4 || mode == 5) && !world.isClient()) {
                NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                if (nbt.contains("P1X") && nbt.contains("P2X")) {
                    BlockPos p1 = new BlockPos(nbt.getInt("P1X"), nbt.getInt("P1Y"), nbt.getInt("P1Z"));
                    BlockPos p2 = new BlockPos(nbt.getInt("P2X"), nbt.getInt("P2Y"), nbt.getInt("P2Z"));

                    EffectZoneEntity zone = new EffectZoneEntity(EFFECT_ZONE_ENTITY, world);
                    int zoneType = (mode == 4) ? 1 : 2;
                    float zoneStrength = nbt.contains("ZoneStrength") ? nbt.getFloat("ZoneStrength") : 1.0f;

                    zone.setZoneData(p1, p2, zoneType, zoneStrength);
                    world.spawnEntity(zone);

                    nbt.remove("ZoneStrength");
                    nbt.remove("P1X");
                    nbt.remove("P1Y");
                    nbt.remove("P1Z");
                    nbt.remove("P2X");
                    nbt.remove("P2Y");
                    nbt.remove("P2Z");
                    itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

                    user.sendMessage(Text.translatable("message.stalkermod.zone_created"), true);
                } else {
                    user.sendMessage(Text.translatable("message.stalkermod.no_selection"), true);
                }
                return TypedActionResult.success(itemStack);
            }

            return TypedActionResult.success(itemStack);
        }
    };

    public static float getInventoryRadiation(PlayerEntity player) {
        float totalRad = 0.0f;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            net.minecraft.component.type.NbtComponent nbtComp = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (nbtComp != null && nbtComp.contains("ArtifactRad")) {
                totalRad += nbtComp.copyNbt().getFloat("ArtifactRad");
            }
        }
        return totalRad;
    }

    public static float getInventoryPsi(PlayerEntity player) {
        float totalPsi = 0.0f;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            net.minecraft.component.type.NbtComponent nbtComp = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (nbtComp != null && nbtComp.contains("ArtifactPsi")) {
                totalPsi += nbtComp.copyNbt().getFloat("ArtifactPsi");
            }
        }
        return totalPsi;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Инициализация Stalker Mod...");

        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C().register(net.light.stalkermod.network.EmissionPayload.ID, net.light.stalkermod.network.EmissionPayload.CODEC);
        EmissionManager.register();
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "anomaly_spawner"), ANOMALY_SPAWNER);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "bolt"), BOLT_ITEM);
        Registry.register(Registries.SOUND_EVENT, TRAMPOLINE_IDLE_ID, TRAMPOLINE_IDLE);
        Registry.register(Registries.SOUND_EVENT, TRAMPOLINE_ACTIVATE_ID, TRAMPOLINE_ACTIVATE);
        Registry.register(Registries.SOUND_EVENT, BURNER_IDLE_ID, BURNER_IDLE);
        Registry.register(Registries.SOUND_EVENT, BURNER_ACTIVATE_ID, BURNER_ACTIVATE);
        Registry.register(Registries.SOUND_EVENT, ELECTRO_IDLE_ID, ELECTRO_IDLE);
        Registry.register(Registries.SOUND_EVENT, ELECTRO_ACTIVATE_ID, ELECTRO_ACTIVATE);
        Registry.register(Registries.SOUND_EVENT, ACID_ACTIVATE_ID, ACID_ACTIVATE);
        Registry.register(Registries.SOUND_EVENT, EMISSION_SIREN_ID, EMISSION_SIREN);
        Registry.register(Registries.SOUND_EVENT, EMISSION_WIND_ID, EMISSION_WIND);
        Registry.register(Registries.SOUND_EVENT, EMISSION_BLOWOUT_ID, EMISSION_BLOWOUT);
        Registry.register(Registries.SOUND_EVENT, HEARTBEAT_ID, HEARTBEAT);
        Registry.register(Registries.SOUND_EVENT, TINNITUS_ID, TINNITUS);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.add(ANOMALY_SPAWNER);
            content.add(BOLT_ITEM);
        });
        net.minecraft.registry.Registry.register(net.minecraft.registry.Registries.SOUND_EVENT, GEIGER_1_ID, GEIGER_1);
        net.minecraft.registry.Registry.register(net.minecraft.registry.Registries.SOUND_EVENT, GEIGER_2_ID, GEIGER_2);
        net.minecraft.registry.Registry.register(net.minecraft.registry.Registries.SOUND_EVENT, GEIGER_3_ID, GEIGER_3);
        net.minecraft.registry.Registry.register(net.minecraft.registry.Registries.SOUND_EVENT, GEIGER_4_ID, GEIGER_4);

        Registry.register(Registries.FEATURE, Identifier.of(MOD_ID, "world_anomalies"), ANOMALY_FEATURE);
        net.fabricmc.fabric.api.biome.v1.BiomeModifications.addFeature(
                net.fabricmc.fabric.api.biome.v1.BiomeSelectors.all(),
                net.minecraft.world.gen.GenerationStep.Feature.SURFACE_STRUCTURES,
                net.minecraft.registry.RegistryKey.of(net.minecraft.registry.RegistryKeys.PLACED_FEATURE, Identifier.of(MOD_ID, "world_anomalies"))
        );

        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(net.minecraft.server.command.CommandManager.literal("emission")
                    .requires(source -> source.hasPermissionLevel(2))

                    .then(net.minecraft.server.command.CommandManager.literal("start")
                            .executes(context -> {
                                EmissionManager.emissionTimer = 2600;
                                context.getSource().sendFeedback(() -> Text.translatable("message.stalkermod.emission_started"), true);
                                return 1;
                            })

                            .then(net.minecraft.server.command.CommandManager.literal("warning")
                                    .executes(context -> {
                                        EmissionManager.emissionTimer = 2410;
                                        context.getSource().sendFeedback(() -> Text.translatable("message.stalkermod.emission_started_warning"), true);
                                        return 1;
                                    }))

                            .then(net.minecraft.server.command.CommandManager.literal("active")
                                    .executes(context -> {
                                        EmissionManager.emissionTimer = 1210;
                                        context.getSource().sendFeedback(() -> Text.translatable("message.stalkermod.emission_started_active"), true);
                                        return 1;
                                    }))

                            .then(net.minecraft.server.command.CommandManager.literal("peak")
                                    .executes(context -> {
                                        EmissionManager.emissionTimer = 240;
                                        context.getSource().sendFeedback(() -> Text.translatable("message.stalkermod.emission_started_peak"), true);
                                        return 1;
                                    }))
                    )

                    .then(net.minecraft.server.command.CommandManager.literal("stop")
                            .executes(context -> {
                                EmissionManager.emissionTimer = -1;
                                context.getSource().sendFeedback(() -> Text.translatable("message.stalkermod.emission_stopped"), true);
                                return 1;
                            }))
            );
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!player.isCreative()) return ActionResult.PASS;
            ItemStack stack = player.getStackInHand(hand);

            if (stack.isOf(ANOMALY_SPAWNER)) {
                if (!world.isClient()) {
                    NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                    int mode = stack.getOrDefault(MODE_COMPONENT, 0);

                    if (player.isSneaking() && mode == 6) {
                        float currentStrength = nbt.contains("ZoneStrength") ? nbt.getFloat("ZoneStrength") : 0.0f;
                        float newStrength = currentStrength >= 10.0f ? 1.0f : currentStrength + 1.0f;
                        nbt.putFloat("ZoneStrength", newStrength);
                        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

                        player.sendMessage(Text.translatable("message.stalkermod.tool_power_set", newStrength), true);
                        return ActionResult.SUCCESS;
                    }

                    if (mode == 4 || mode == 5) {
                        nbt.putInt("P1X", pos.getX());
                        nbt.putInt("P1Y", pos.getY());
                        nbt.putInt("P1Z", pos.getZ());
                        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

                        player.sendMessage(Text.translatable("message.stalkermod.point1_set"), true);
                        return ActionResult.SUCCESS;
                    }
                } else {
                    int mode = stack.getOrDefault(MODE_COMPONENT, 0);
                    if (player.isSneaking() || mode == 4 || mode == 5) return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

        net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.LOAD.register((server, world) -> {
            if (world == server.getOverworld()) {
                EmissionSaveData data = EmissionSaveData.get(world);

                EmissionManager.emissionTimer = data.savedTimer;
                EmissionManager.isEmissionDamage = data.savedIsEmissionDamage;
                EmissionManager.postEffectTimer = data.savedPostEffectTimer;

                LOGGER.info("Данные Выброса успешно загружены! Таймер: " + EmissionManager.emissionTimer + ", Остывание: " + EmissionManager.postEffectTimer);
            }
        });
    }
}