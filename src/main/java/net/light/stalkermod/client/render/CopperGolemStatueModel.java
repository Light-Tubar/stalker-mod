package net.light.stalkermod.client.render;

import net.light.stalkermod.StalkerMod;
import net.light.stalkermod.ModBlocks;
import net.light.stalkermod.block.CopperGolemStatueBlock;
import net.light.stalkermod.block.GolemPose;
import net.light.stalkermod.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CopperGolemStatueModel extends GeoModel<CopperGolemStatueBlockEntity> {

    @Override
    public Identifier getModelResource(CopperGolemStatueBlockEntity object) {
        GolemPose pose = object.getCachedState().get(CopperGolemStatueBlock.POSE);
        return switch (pose) {
            case SITTING -> Identifier.of(StalkerMod.MOD_ID, "geo/sitting.geo.json");
            case RUNNING -> Identifier.of(StalkerMod.MOD_ID, "geo/running.geo.json");
            case STAR -> Identifier.of(StalkerMod.MOD_ID, "geo/star.geo.json");
            default -> Identifier.of(StalkerMod.MOD_ID, "geo/standing.geo.json");
        };
    }

    @Override
    public Identifier getAnimationResource(CopperGolemStatueBlockEntity object) {
        return null;
    }

    @Override
    public Identifier getTextureResource(CopperGolemStatueBlockEntity object) {
        Block block = object.getCachedState().getBlock();

        if (block == ModBlocks.EXPOSED_COPPER_GOLEM_STATUE || block == ModBlocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE) {
            return Identifier.of(StalkerMod.MOD_ID, "textures/block/exposed_copper_golem.png");
        } else if (block == ModBlocks.WEATHERED_COPPER_GOLEM_STATUE || block == ModBlocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE) {
            return Identifier.of(StalkerMod.MOD_ID, "textures/block/weathered_copper_golem.png");
        } else if (block == ModBlocks.OXIDIZED_COPPER_GOLEM_STATUE || block == ModBlocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE) {
            return Identifier.of(StalkerMod.MOD_ID, "textures/block/oxidized_copper_golem.png");
        }
        return Identifier.of(StalkerMod.MOD_ID, "textures/block/copper_golem.png");
    }
}