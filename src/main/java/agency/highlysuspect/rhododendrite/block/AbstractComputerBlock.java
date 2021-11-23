package agency.highlysuspect.rhododendrite.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class AbstractComputerBlock extends Block {
	public AbstractComputerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(DirectionalBlock.FACING, Direction.UP));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(DirectionalBlock.FACING));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(DirectionalBlock.FACING, context.getNearestLookingDirection());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	private static final VoxelShape SHAPE;
	
	static {
		//shoutouts to the "VoxelShape Generator" blockbench plugin lol.
		VoxelShape shape = Shapes.empty();
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.0625, 0.0625, 0.9375, 0.25, 0.25), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.0625, 0.0625, 0.25, 0.25, 0.25), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.0625, 0.75, 0.9375, 0.25, 0.9375), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.0625, 0.75, 0.25, 0.25, 0.9375), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.75, 0.0625, 0.25, 0.9375, 0.25), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.75, 0.0625, 0.9375, 0.9375, 0.25), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.75, 0.75, 0.9375, 0.9375, 0.9375), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.75, 0.75, 0.25, 0.9375, 0.9375), BooleanOp.OR);
		//Big block in the middle because actually having a hole in the middle of the block was annoying.
		shape = Shapes.joinUnoptimized(shape, Shapes.box(3/16d, 3/16d, 3/16d, 13/16d, 13/16d, 13/16d), BooleanOp.OR);
		SHAPE = shape.optimize();
	}
}
