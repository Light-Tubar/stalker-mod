package net.light.stalkermod;

import net.light.stalkermod.block.ShelfBlock;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.item.SignItem;
import net.minecraft.item.HangingSignItem;

public class ModBlocks {

    public static final Block COPPER_GOLEM_STATUE = registerBlock("copper_golem_statue",
            new net.light.stalkermod.block.CopperGolemStatueBlock(Oxidizable.OxidationLevel.UNAFFECTED, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK).nonOpaque()));

    public static final Block EXPOSED_COPPER_GOLEM_STATUE = registerBlock("exposed_copper_golem_statue",
            new net.light.stalkermod.block.CopperGolemStatueBlock(Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK).nonOpaque()));

    public static final Block WEATHERED_COPPER_GOLEM_STATUE = registerBlock("weathered_copper_golem_statue",
            new net.light.stalkermod.block.CopperGolemStatueBlock(Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK).nonOpaque()));

    public static final Block OXIDIZED_COPPER_GOLEM_STATUE = registerBlock("oxidized_copper_golem_statue",
            new net.light.stalkermod.block.CopperGolemStatueBlock(Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK).nonOpaque()));

    public static final Block WAXED_COPPER_GOLEM_STATUE = registerBlock("waxed_copper_golem_statue",
            new net.light.stalkermod.block.CopperGolemStatueBlock(Oxidizable.OxidationLevel.UNAFFECTED, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK).nonOpaque()));

    public static final Block WAXED_EXPOSED_COPPER_GOLEM_STATUE = registerBlock("waxed_exposed_copper_golem_statue",
            new net.light.stalkermod.block.CopperGolemStatueBlock(Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK).nonOpaque()));

    public static final Block WAXED_WEATHERED_COPPER_GOLEM_STATUE = registerBlock("waxed_weathered_copper_golem_statue",
            new net.light.stalkermod.block.CopperGolemStatueBlock(Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK).nonOpaque()));

    public static final Block WAXED_OXIDIZED_COPPER_GOLEM_STATUE = registerBlock("waxed_oxidized_copper_golem_statue",
            new net.light.stalkermod.block.CopperGolemStatueBlock(Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK).nonOpaque()));

    public static final BlockSetType PALE_OAK_BLOCK_SET_TYPE = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(Identifier.of(StalkerMod.MOD_ID, "pale_oak"));

    public static final WoodType PALE_OAK_WOOD_TYPE = WoodTypeBuilder.copyOf(WoodType.OAK).register(Identifier.of(StalkerMod.MOD_ID, "pale_oak"), PALE_OAK_BLOCK_SET_TYPE);

    public static final Block PALE_OAK_LOG = registerBlock("pale_oak_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG).mapColor(MapColor.TERRACOTTA_GRAY)));

    public static final Block STRIPPED_PALE_OAK_LOG = registerBlock("stripped_pale_oak_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));

    public static final Block PALE_OAK_PLANKS = registerBlock("pale_oak_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GRAY)));

    public static final Block PALE_OAK_LEAVES = registerBlock("pale_oak_leaves",
            new LeavesBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));

    public static final Block PALE_OAK_SAPLING = registerBlock("pale_oak_sapling",
            new SaplingBlock(SaplingGenerator.OAK, AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)));

    public static final Block PALE_OAK_DOOR = registerBlock("pale_oak_door",
            new DoorBlock(PALE_OAK_BLOCK_SET_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_DOOR)));

    public static final Block PALE_OAK_TRAPDOOR = registerBlock("pale_oak_trapdoor",
            new TrapdoorBlock(PALE_OAK_BLOCK_SET_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_TRAPDOOR)));

    public static final Block PALE_OAK_STAIRS = registerBlock("pale_oak_stairs",
            new net.minecraft.block.StairsBlock(PALE_OAK_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.OAK_STAIRS).mapColor(MapColor.TERRACOTTA_GRAY)));

    public static final Block PALE_OAK_SLAB = registerBlock("pale_oak_slab",
            new net.minecraft.block.SlabBlock(AbstractBlock.Settings.copy(Blocks.OAK_SLAB).mapColor(MapColor.TERRACOTTA_GRAY)));

    public static final Block PALE_OAK_SIGN = Registry.register(Registries.BLOCK, Identifier.of(StalkerMod.MOD_ID, "pale_oak_sign"),
            new SignBlock(PALE_OAK_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_SIGN)));

    public static final Block PALE_OAK_WALL_SIGN = Registry.register(Registries.BLOCK, Identifier.of(StalkerMod.MOD_ID, "pale_oak_wall_sign"),
            new WallSignBlock(PALE_OAK_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_WALL_SIGN).dropsLike(PALE_OAK_SIGN)));

    public static final Block PALE_OAK_HANGING_SIGN = Registry.register(Registries.BLOCK, Identifier.of(StalkerMod.MOD_ID, "pale_oak_hanging_sign"),
            new HangingSignBlock(PALE_OAK_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_HANGING_SIGN)));

    public static final Block PALE_OAK_WALL_HANGING_SIGN = Registry.register(Registries.BLOCK, Identifier.of(StalkerMod.MOD_ID, "pale_oak_wall_hanging_sign"),
            new WallHangingSignBlock(PALE_OAK_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_WALL_HANGING_SIGN).dropsLike(PALE_OAK_HANGING_SIGN)));

    public static final Block OAK_SHELF = registerBlock("oak_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block SPRUCE_SHELF = registerBlock("spruce_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block BIRCH_SHELF = registerBlock("birch_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block JUNGLE_SHELF = registerBlock("jungle_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block ACACIA_SHELF = registerBlock("acacia_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block DARK_OAK_SHELF = registerBlock("dark_oak_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block MANGROVE_SHELF = registerBlock("mangrove_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block CHERRY_SHELF = registerBlock("cherry_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block PALE_OAK_SHELF = registerBlock("pale_oak_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block BAMBOO_SHELF = registerBlock("bamboo_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block CRIMSON_SHELF = registerBlock("crimson_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block WARPED_SHELF = registerBlock("warped_shelf",
            new net.light.stalkermod.block.ShelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).nonOpaque()));

    public static final Block PALE_MOSS_BLOCK = registerBlock("pale_moss_block",
            new Block(AbstractBlock.Settings.copy(Blocks.MOSS_BLOCK).mapColor(MapColor.TERRACOTTA_GRAY)));

    public static final Block PALE_MOSS_CARPET = registerBlock("pale_moss_carpet",
            new net.light.stalkermod.block.PaleMossCarpetBlock(AbstractBlock.Settings.copy(Blocks.MOSS_CARPET).mapColor(MapColor.TERRACOTTA_GRAY)));

    public static final Block PALE_HANGING_MOSS = registerBlock("pale_hanging_moss",
            new net.light.stalkermod.block.PaleHangingMossBlock(AbstractBlock.Settings.copy(Blocks.HANGING_ROOTS)));

    public static final Block PALE_HANGING_MOSS_TIP = registerBlock("pale_hanging_moss_tip",
            new net.light.stalkermod.block.PaleHangingMossBlock(AbstractBlock.Settings.copy(Blocks.HANGING_ROOTS)));

    public static final Block COPPER_BARS = registerBlock("copper_bars",
            new PaneBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).mapColor(MapColor.ORANGE)));

    public static final Block EXPOSED_COPPER_BARS = registerBlock("exposed_copper_bars",
            new PaneBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));

    public static final Block WEATHERED_COPPER_BARS = registerBlock("weathered_copper_bars",
            new PaneBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).mapColor(MapColor.CYAN)));

    public static final Block OXIDIZED_COPPER_BARS = registerBlock("oxidized_copper_bars",
            new PaneBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).mapColor(MapColor.TEAL)));

    public static final Block COPPER_CHAIN = registerBlock("copper_chain",
            new ChainBlock(AbstractBlock.Settings.copy(Blocks.CHAIN).mapColor(MapColor.ORANGE)));

    public static final Block EXPOSED_COPPER_CHAIN = registerBlock("exposed_copper_chain",
            new ChainBlock(AbstractBlock.Settings.copy(Blocks.CHAIN).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));

    public static final Block WEATHERED_COPPER_CHAIN = registerBlock("weathered_copper_chain",
            new ChainBlock(AbstractBlock.Settings.copy(Blocks.CHAIN).mapColor(MapColor.CYAN)));

    public static final Block OXIDIZED_COPPER_CHAIN = registerBlock("oxidized_copper_chain",
            new ChainBlock(AbstractBlock.Settings.copy(Blocks.CHAIN).mapColor(MapColor.TEAL)));

    public static final Block COPPER_LANTERN = registerBlock("copper_lantern",
            new LanternBlock(AbstractBlock.Settings.copy(Blocks.LANTERN).mapColor(MapColor.ORANGE).luminance(state -> 15)));

    public static final Block EXPOSED_COPPER_LANTERN = registerBlock("exposed_copper_lantern",
            new LanternBlock(AbstractBlock.Settings.copy(Blocks.LANTERN).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).luminance(state -> 14)));

    public static final Block WEATHERED_COPPER_LANTERN = registerBlock("weathered_copper_lantern",
            new LanternBlock(AbstractBlock.Settings.copy(Blocks.LANTERN).mapColor(MapColor.CYAN).luminance(state -> 13)));

    public static final Block OXIDIZED_COPPER_LANTERN = registerBlock("oxidized_copper_lantern",
            new LanternBlock(AbstractBlock.Settings.copy(Blocks.LANTERN).mapColor(MapColor.TEAL).luminance(state -> 12)));

    public static final Item PALE_OAK_SIGN_ITEM = Registry.register(Registries.ITEM, Identifier.of(StalkerMod.MOD_ID, "pale_oak_sign"),
            new SignItem(new Item.Settings().maxCount(16), PALE_OAK_SIGN, PALE_OAK_WALL_SIGN));

    public static final Item PALE_OAK_HANGING_SIGN_ITEM = Registry.register(Registries.ITEM, Identifier.of(StalkerMod.MOD_ID, "pale_oak_hanging_sign"),
            new HangingSignItem(PALE_OAK_HANGING_SIGN, PALE_OAK_WALL_HANGING_SIGN, new Item.Settings().maxCount(16)));

    private static Block registerBlock(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(StalkerMod.MOD_ID, name), new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, Identifier.of(StalkerMod.MOD_ID, name), block);
    }

    public static void registerModBlocks() {
        StalkerMod.LOGGER.info("Загрузка Блоков из 1.21.10 для " + StalkerMod.MOD_ID);
        net.fabricmc.fabric.api.registry.StrippableBlockRegistry.register(PALE_OAK_LOG, STRIPPED_PALE_OAK_LOG);
        net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry.registerOxidizableBlockPair(COPPER_GOLEM_STATUE, EXPOSED_COPPER_GOLEM_STATUE);
        net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_COPPER_GOLEM_STATUE, WEATHERED_COPPER_GOLEM_STATUE);
        net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_COPPER_GOLEM_STATUE, OXIDIZED_COPPER_GOLEM_STATUE);
        net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry.registerWaxableBlockPair(COPPER_GOLEM_STATUE, WAXED_COPPER_GOLEM_STATUE);
        net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_COPPER_GOLEM_STATUE, WAXED_EXPOSED_COPPER_GOLEM_STATUE);
        net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_COPPER_GOLEM_STATUE, WAXED_WEATHERED_COPPER_GOLEM_STATUE);
        net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_COPPER_GOLEM_STATUE, WAXED_OXIDIZED_COPPER_GOLEM_STATUE);
    }
}