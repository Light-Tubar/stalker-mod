package net.light.stalkermod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarpetBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class PaleMossCarpetBlock extends CarpetBlock {
    public static final BooleanProperty BOTTOM = BooleanProperty.of("bottom");
    public static final EnumProperty<CarpetShape> NORTH = EnumProperty.of("north", CarpetShape.class);
    public static final EnumProperty<CarpetShape> EAST = EnumProperty.of("east", CarpetShape.class);
    public static final EnumProperty<CarpetShape> SOUTH = EnumProperty.of("south", CarpetShape.class);
    public static final EnumProperty<CarpetShape> WEST = EnumProperty.of("west", CarpetShape.class);

    public PaleMossCarpetBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(BOTTOM, true)
                .with(NORTH, CarpetShape.NONE)
                .with(EAST, CarpetShape.NONE)
                .with(SOUTH, CarpetShape.NONE)
                .with(WEST, CarpetShape.NONE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(BOTTOM, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return updateShape(this.getDefaultState(), ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return net.minecraft.block.Blocks.AIR.getDefaultState();
        }
        return updateShape(state, world, pos);
    }

    private BlockState updateShape(BlockState state, WorldAccess world, BlockPos pos) {
        return state
                .with(BOTTOM, !world.getBlockState(pos.down()).isAir())
                .with(NORTH, getShape(world, pos, Direction.NORTH))
                .with(EAST, getShape(world, pos, Direction.EAST))
                .with(SOUTH, getShape(world, pos, Direction.SOUTH))
                .with(WEST, getShape(world, pos, Direction.WEST));
    }

    private CarpetShape getShape(WorldAccess world, BlockPos pos, Direction dir) {
        BlockPos sidePos = pos.offset(dir);
        BlockState sideState = world.getBlockState(sidePos);

        if (world.getBlockState(pos.up()).isSideSolidFullSquare(world, pos.up(), Direction.DOWN)) {
            return CarpetShape.NONE;
        }

        boolean isSolidFace = sideState.isSideSolidFullSquare(world, sidePos, dir.getOpposite());
        boolean isSlabOrStair = sideState.getBlock() instanceof net.minecraft.block.SlabBlock ||
                sideState.getBlock() instanceof net.minecraft.block.StairsBlock;

        if (isSolidFace || isSlabOrStair) {
            return CarpetShape.LOW;
        }

        return CarpetShape.NONE;
    }
}