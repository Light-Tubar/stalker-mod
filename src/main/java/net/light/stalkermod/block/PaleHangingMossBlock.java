package net.light.stalkermod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HangingRootsBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class PaleHangingMossBlock extends HangingRootsBlock {
    public static final BooleanProperty TIP = BooleanProperty.of("tip");

    public PaleHangingMossBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(TIP, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(TIP);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState stateBelow = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
        boolean isTip = !(stateBelow.getBlock() instanceof PaleHangingMossBlock);

        BlockState baseState = super.getPlacementState(ctx);
        if (baseState == null) baseState = this.getDefaultState();

        return baseState.with(TIP, isTip);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            boolean isTip = !(neighborState.getBlock() instanceof PaleHangingMossBlock);
            return state.with(TIP, isTip);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
}