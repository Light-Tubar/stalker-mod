package net.light.stalkermod.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CopperGolemStatueBlock extends BlockWithEntity implements Oxidizable, Waterloggable {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<GolemPose> POSE = EnumProperty.of("pose", GolemPose.class);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private final Oxidizable.OxidationLevel oxidationLevel;

    private static final VoxelShape STANDING_SHAPE = Block.createCuboidShape(4, 0, 4, 12, 16, 12);
    private static final VoxelShape SITTING_SHAPE = Block.createCuboidShape(3, 0, 3, 13, 12, 13);
    private static final VoxelShape RUNNING_SHAPE = Block.createCuboidShape(2, 0, 2, 14, 16, 14);
    private static final VoxelShape STAR_SHAPE = Block.createCuboidShape(2, 0, 2, 14, 16, 14);

    public CopperGolemStatueBlock(Oxidizable.OxidationLevel oxidationLevel, Settings settings) {
        super(settings);
        this.oxidationLevel = oxidationLevel;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POSE, GolemPose.STANDING)
                .with(WATERLOGGED, false));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public net.minecraft.block.entity.BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new net.light.stalkermod.block.entity.CopperGolemStatueBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POSE, WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing())
                .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(POSE)) {
            case STANDING -> STANDING_SHAPE;
            case SITTING -> SITTING_SHAPE;
            case RUNNING -> RUNNING_SHAPE;
            case STAR -> STAR_SHAPE;
        };
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (player.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
            if (!world.isClient) {
                world.setBlockState(pos, state.with(POSE, state.get(POSE).next()), Block.NOTIFY_ALL);
                world.updateComparators(pos, this);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) { return true; }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(POSE).getPower();
    }

    @Override
    public OxidationLevel getDegradationLevel() {
        return this.oxidationLevel;
    }

    @Override
    public <T extends net.minecraft.block.entity.BlockEntity> net.minecraft.block.entity.BlockEntityTicker<T> getTicker(World world, BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
        // Запускаем тикер только на стороне сервера
        return world.isClient ? null : validateTicker(type, net.light.stalkermod.StalkerMod.COPPER_GOLEM_STATUE_ENTITY, net.light.stalkermod.block.entity.CopperGolemStatueBlockEntity::serverTick);
    }
}