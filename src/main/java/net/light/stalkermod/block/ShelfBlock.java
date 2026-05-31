package net.light.stalkermod.block;

import com.mojang.serialization.MapCodec;
import net.light.stalkermod.block.entity.ShelfBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class ShelfBlock extends BlockWithEntity implements Waterloggable {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final EnumProperty<SideChain> SIDE_CHAIN = EnumProperty.of("side_chain", SideChain.class);

    public ShelfBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, false)
                .with(WATERLOGGED, false)
                .with(SIDE_CHAIN, SideChain.UNCONNECTED));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() { return createCodec(ShelfBlock::new); }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShelfBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, WATERLOGGED, SIDE_CHAIN);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);

        switch (facing) {
            case NORTH:
                return Block.createCuboidShape(0.0, 0.0, 11.0, 16.0, 16.0, 16.0);
            case SOUTH:
                return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 5.0);
            case WEST:
                return Block.createCuboidShape(11.0, 0.0, 0.0, 16.0, 16.0, 16.0);
            case EAST:
                return Block.createCuboidShape(0.0, 0.0, 0.0, 5.0, 16.0, 16.0);
            default:
                return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ShelfBlockEntity shelf)) return ActionResult.CONSUME;

        double localX = hit.getPos().x - pos.getX();
        double localZ = hit.getPos().z - pos.getZ();
        Direction facing = state.get(FACING);

        double hitPos = 0;
        switch (facing) {
            case NORTH -> hitPos = 1.0 - localX;
            case SOUTH -> hitPos = localX;
            case WEST -> hitPos = localZ;
            case EAST -> hitPos = 1.0 - localZ;
        }

        int slot = MathHelper.clamp((int) (hitPos * 3), 0, 2);

        ItemStack handStack = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack shelfStack = shelf.getStack(slot);

        player.setStackInHand(Hand.MAIN_HAND, shelfStack.copy());
        shelf.setStack(slot, handStack.copy());
        shelf.markDirty();

        world.updateComparators(pos, this);

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ShelfBlockEntity) {
                ItemScatterer.spawn(world, pos, (ShelfBlockEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) { return true; }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ShelfBlockEntity shelf)) return 0;

        int signal = 0;
        if (!shelf.getStack(0).isEmpty()) signal += 1;
        if (!shelf.getStack(1).isEmpty()) signal += 2;
        if (!shelf.getStack(2).isEmpty()) signal += 4;
        return signal;
    }
}