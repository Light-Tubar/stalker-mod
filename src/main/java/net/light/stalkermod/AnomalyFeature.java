package net.light.stalkermod;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class AnomalyFeature extends Feature<DefaultFeatureConfig> {

    public AnomalyFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos randomPos = context.getOrigin();
        Random random = context.getRandom();

        int multiplierSetting = world.toServerWorld().getGameRules().getInt(StalkerMod.ANOMALY_SPAWN_MULTIPLIER);
        if (multiplierSetting <= 0) return false;
        float mult = multiplierSetting / 100.0f;

        if (random.nextFloat() < 0.15f) {
            BlockPos surfacePos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, randomPos);

            if (world.getBlockState(surfacePos.down()).isOpaqueFullCube(world, surfacePos.down()) && world.isAir(surfacePos)) {

                boolean isSwamp = world.getBiome(surfacePos).matchesKey(BiomeKeys.SWAMP) || world.getBiome(surfacePos).matchesKey(BiomeKeys.MANGROVE_SWAMP);
                if (isSwamp && random.nextFloat() < (0.02f * mult)) { // 10% шанс для болота
                    spawnElemental(world, surfacePos, 0, 2.0f);
                    return true;
                }

                if (surfacePos.getY() > 100 && random.nextFloat() < (0.05f * mult)) { // 15% шанс для гор
                    spawnElemental(world, surfacePos, 2, 4.0f);
                    return true;
                }
            }
        }

        if (!world.isAir(randomPos)) return false;

        if (random.nextFloat() < (0.03f * mult)) {
            if (isTrampolineLocation(world, randomPos)) {
                spawnAnomaly(world, randomPos);
                return true;
            }
        }

        BlockPos floorPos = null;
        for (int i = 0; i <= 15; i++) {
            BlockPos check = randomPos.down(i);
            if (world.getBlockState(check).isSolidBlock(world, check)) {
                floorPos = check.up();
                break;
            }
        }

        if (floorPos != null && world.isAir(floorPos)) {
            if (random.nextFloat() < (0.05f * mult) && isNearLava(world, floorPos)) {
                spawnElemental(world, floorPos, 1, 2.0f);
                return true;
            }
        }

        return false;
    }

    private boolean isTrampolineLocation(StructureWorldAccess world, BlockPos pos) {
        for (int i = 1; i <= 4; i++) {
            if (!world.isAir(pos.down(i))) return false;
            if (!world.isAir(pos.up(i))) return false;
        }

        boolean hasFloor = false;
        for (int i = 5; i <= 15; i++) {
            if (world.getBlockState(pos.down(i)).isSolidBlock(world, pos.down(i))) {
                hasFloor = true; break;
            }
        }
        if (!hasFloor) return false;

        boolean hasCeiling = false;
        for (int i = 5; i <= 20; i++) {
            if (world.getBlockState(pos.up(i)).isSolidBlock(world, pos.up(i))) {
                hasCeiling = true; break;
            }
        }

        int wallCount = 0;
        if (hasRockWall(world, pos, 1, 0)) wallCount++;
        if (hasRockWall(world, pos, -1, 0)) wallCount++;
        if (hasRockWall(world, pos, 0, 1)) wallCount++;
        if (hasRockWall(world, pos, 0, -1)) wallCount++;

        return hasCeiling || wallCount >= 2;
    }

    private boolean hasRockWall(StructureWorldAccess world, BlockPos pos, int dx, int dz) {
        for (int i = 4; i <= 15; i++) {
            BlockPos check = pos.add(dx * i, 0, dz * i);
            BlockState state = world.getBlockState(check);
            if (state.isOpaqueFullCube(world, check) && !state.isIn(BlockTags.LOGS) && !state.isIn(BlockTags.LEAVES)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNearLava(StructureWorldAccess world, BlockPos pos) {
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = -2; y <= 2; y++) {
                    if (world.getBlockState(pos.add(x, y, z)).isOf(Blocks.LAVA)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void spawnElemental(StructureWorldAccess world, BlockPos pos, int type, float radius) {
        ElementalAnomalyEntity anomaly = new ElementalAnomalyEntity(StalkerMod.ELEMENTAL_ANOMALY, world.toServerWorld());
        anomaly.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        anomaly.type = type;
        anomaly.radius = radius;
        world.spawnEntityAndPassengers(anomaly);
    }

    private void spawnAnomaly(StructureWorldAccess world, BlockPos pos) {
        AnomalyEntity anomaly = new AnomalyEntity(StalkerMod.ANOMALY_ENTITY, world.toServerWorld());
        anomaly.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        world.spawnEntityAndPassengers(anomaly);
    }
}