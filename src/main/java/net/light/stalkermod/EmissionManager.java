package net.light.stalkermod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class EmissionManager {
    public static int emissionTimer = -1;
    public static int nextEmissionInterval = 72000;
    public static boolean isEmissionDamage = false;
    public static int postEffectTimer = -1;

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (emissionTimer > 0 && emissionTimer <= 2600) {
                net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(handler.getPlayer(), new net.light.stalkermod.network.EmissionPayload(emissionTimer));
            } else if (postEffectTimer > 0) {
                net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(handler.getPlayer(), new net.light.stalkermod.network.EmissionPayload(-postEffectTimer));
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (postEffectTimer > 0) {
                postEffectTimer--;
            }

            if (emissionTimer > 0) {
                emissionTimer--;

                if (emissionTimer == 2400) {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if (player.getServerWorld().getRegistryKey() != World.OVERWORLD) continue;
                        player.sendMessage(Text.literal("§c[ВНИМАНИЕ] Зафиксирован всплеск пси-активности. Ищите укрытие!"), false);
                        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, new net.light.stalkermod.network.EmissionPayload(2400));

                        double dx = (player.getServerWorld().random.nextDouble() - 0.5) * 30.0;
                        double dz = (player.getServerWorld().random.nextDouble() - 0.5) * 30.0;
                        playPersonalSound(player, StalkerMod.EMISSION_THUNDER, 5.0f, 0.8f, dx, 25.0, dz);
                    }
                }

                if (emissionTimer < 1600 && emissionTimer > 600) {
                    if (server.getOverworld().random.nextInt(120) == 0) {
                        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                            if (player.getServerWorld().getRegistryKey() != World.OVERWORLD) continue;
                            float randomPitch = 0.6f + (player.getServerWorld().random.nextFloat() * 0.3f);

                            double dx = (player.getServerWorld().random.nextDouble() - 0.5) * 30.0;
                            double dz = (player.getServerWorld().random.nextDouble() - 0.5) * 30.0;
                            playPersonalSound(player, StalkerMod.EMISSION_THUNDER, 4.0f, randomPitch, dx, 15.0, dz);
                        }
                    }
                }

                if (emissionTimer == 1200) {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if (player.getServerWorld().getRegistryKey() != World.OVERWORLD) continue;
                        playPersonalSound(player, StalkerMod.EMISSION_EARTHQUAKE, 4.0f, 1.0f, 0.0, -15.0, 0.0);

                        double dx = (player.getServerWorld().random.nextDouble() - 0.5) * 20.0;
                        double dz = (player.getServerWorld().random.nextDouble() - 0.5) * 20.0;
                        playPersonalSound(player, StalkerMod.EMISSION_WIND, 1.5f, 1.0f, dx, 5.0, dz);
                    }
                }

                if (emissionTimer < 1200 && emissionTimer > 230) {
                    if (server.getOverworld().random.nextInt(100) == 0) {
                        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                            if (player.getServerWorld().getRegistryKey() != World.OVERWORLD) continue;
                            float randomWindVol = 2.0f + (player.getServerWorld().random.nextFloat() * 1.5f);
                            float randomWindPitch = 0.7f + (player.getServerWorld().random.nextFloat() * 0.5f);

                            double dx = (player.getServerWorld().random.nextDouble() - 0.5) * 25.0;
                            double dz = (player.getServerWorld().random.nextDouble() - 0.5) * 25.0;
                            playPersonalSound(player, StalkerMod.EMISSION_WIND, randomWindVol, randomWindPitch, dx, 5.0, dz);
                        }
                    }
                }

                if (emissionTimer == 600) {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if (player.getServerWorld().getRegistryKey() != World.OVERWORLD) continue;
                        playPersonalSound(player, StalkerMod.EMISSION_EARTHQUAKE, 10.0f, 0.9f, 0.0, -20.0, 0.0);
                    }
                }

                if (emissionTimer <= 2400 && emissionTimer % 20 == 0) {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if (player.getServerWorld().getRegistryKey() != World.OVERWORLD) continue;

                        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, new net.light.stalkermod.network.EmissionPayload(emissionTimer));

                        ServerWorld world = player.getServerWorld();
                        Box animalBox = player.getBoundingBox().expand(48.0);
                        List<AnimalEntity> animals = world.getEntitiesByClass(AnimalEntity.class, animalBox, a -> true);

                        for (AnimalEntity animal : animals) {
                            BlockPos pos = animal.getBlockPos();
                            if (world.isSkyVisible(pos)) {
                                if (!animal.getNavigation().isFollowingPath()) {
                                    BlockPos safePos = null;
                                    for (int i = 0; i < 15; i++) {
                                        BlockPos checkPos = pos.add(world.random.nextInt(31) - 15, world.random.nextInt(7) - 3, world.random.nextInt(31) - 15);
                                        if (!world.isSkyVisible(checkPos) && world.getBlockState(checkPos).isAir() && world.getBlockState(checkPos.down()).isSolid()) {
                                            safePos = checkPos;
                                            break;
                                        }
                                    }
                                    if (safePos != null) {
                                        animal.getNavigation().startMovingTo(safePos.getX(), safePos.getY(), safePos.getZ(), 1.4);
                                    }
                                }
                            } else {
                                animal.getNavigation().stop();
                            }
                        }
                    }
                }

                if (emissionTimer == 230) {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if (player.getServerWorld().getRegistryKey() != World.OVERWORLD) continue;
                        playPersonalSound(player, StalkerMod.EMISSION_EARTHQUAKE, 15.0f, 0.6f, 0.0, -25.0, 0.0);
                    }
                }
                if (emissionTimer <= 30 && emissionTimer > 0) {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if (player.getServerWorld().getRegistryKey() != World.OVERWORLD) continue;
                        if (player.isCreative() || player.isSpectator()) continue;

                        if (!isPlayerSafe(player)) {
                            if (emissionTimer % 10 == 0) {
                                playPersonalSound(player, StalkerMod.HEARTBEAT, 2.0f, 1.0f, 0.0, 0.0, 0.0);
                            }
                        }
                    }
                }

                if (emissionTimer == 20) {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if (player.getServerWorld().getRegistryKey() != World.OVERWORLD) continue;
                        playPersonalSound(player, StalkerMod.EMISSION_BLOWOUT, 5.0f, 1.0f, 0.0, 20.0, 0.0);
                    }
                }

            } else if (emissionTimer == 0) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (player.getServerWorld().getRegistryKey() != World.OVERWORLD) continue;
                    if (player.isCreative() || player.isSpectator()) continue;

                    if (isPlayerSafe(player)) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 160, 0, false, false));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 300, 0, false, false));

                        playPersonalSound(player, StalkerMod.TINNITUS, 1.5f, 1.0f, 0.0, 0.0, 0.0);
                        player.sendMessage(Text.literal("§aВыброс миновал. Вы в безопасности."), false);
                    }
                    else {
                        isEmissionDamage = true;

                        player.damage(player.getServerWorld().getDamageSources().magic(), 9999999.0f);

                        isEmissionDamage = false;
                        player.sendMessage(Text.literal("§4Ваша нервная система выжжена Выбросом."), false);
                    }
                    net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, new net.light.stalkermod.network.EmissionPayload(0));
                }
                server.getOverworld().setWeather(0, 180, true, false);

                postEffectTimer = 1800;
                emissionTimer = nextEmissionInterval;
            }

            ServerWorld overworld = server.getOverworld();
            if (overworld != null) {
                EmissionSaveData saveData = EmissionSaveData.get(overworld);

                if (saveData.savedTimer != emissionTimer || saveData.savedIsEmissionDamage != isEmissionDamage || saveData.savedPostEffectTimer != postEffectTimer) {
                    saveData.savedTimer = emissionTimer;
                    saveData.savedIsEmissionDamage = isEmissionDamage;
                    saveData.savedPostEffectTimer = postEffectTimer;

                    saveData.markDirty();
                }
            }
        });
    }

    private static void playPersonalSound(ServerPlayerEntity player, SoundEvent sound, float volume, float pitch, double offsetX, double offsetY, double offsetZ) {
        player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket(
                net.minecraft.registry.entry.RegistryEntry.of(sound),
                net.minecraft.sound.SoundCategory.WEATHER,
                player.getX() + offsetX, player.getY() + offsetY, player.getZ() + offsetZ,
                volume, pitch,
                player.getServerWorld().random.nextLong()
        ));
    }

    private static boolean isPlayerSafe(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        if (world.isSkyVisible(pos) || world.isSkyVisible(pos.up())) return false;

        int wallCount = 0;
        int[][] directions = {{0,-1},{0,1},{-1,0},{1,0},{-1,-1},{1,-1},{-1,1},{1,1}};
        int headY = pos.getY() + 1;

        for (int[] dir : directions) {
            for (int step = 1; step <= 15; step++) {
                BlockPos checkPos = new BlockPos(pos.getX() + (dir[0] * step), headY, pos.getZ() + (dir[1] * step));
                if (!world.getBlockState(checkPos).isAir() && !world.getBlockState(checkPos).getCollisionShape(world, checkPos).isEmpty()) {
                    wallCount++;
                    break;
                }
            }
        }
        return wallCount >= 6;
    }
}