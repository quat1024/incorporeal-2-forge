package agency.highlysuspect.rhododendrite.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.level.IBlockReader;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public abstract class AbstractComputerBlock extends Block {
	public AbstractComputerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(DirectionalBlock.FACING, Direction.UP));
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(DirectionalBlock.FACING));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return defaultBlockState().setValue(DirectionalBlock.FACING, context.getNearestLookingDirection());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	private static final VoxelShape SHAPE;
	
	static {
		//shoutouts to the "VoxelShape Generator" blockbench plugin lol.
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.joinUnoptimized(shape, VoxelShapes.box(0.75, 0.0625, 0.0625, 0.9375, 0.25, 0.25), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, VoxelShapes.box(0.0625, 0.0625, 0.0625, 0.25, 0.25, 0.25), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, VoxelShapes.box(0.75, 0.0625, 0.75, 0.9375, 0.25, 0.9375), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, VoxelShapes.box(0.0625, 0.0625, 0.75, 0.25, 0.25, 0.9375), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, VoxelShapes.box(0.0625, 0.75, 0.0625, 0.25, 0.9375, 0.25), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, VoxelShapes.box(0.75, 0.75, 0.0625, 0.9375, 0.9375, 0.25), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, VoxelShapes.box(0.75, 0.75, 0.75, 0.9375, 0.9375, 0.9375), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, VoxelShapes.box(0.0625, 0.75, 0.75, 0.25, 0.9375, 0.9375), IBooleanFunction.OR);
		//Big block in the middle because actually having a hole in the middle of the block was annoying.
		shape = VoxelShapes.joinUnoptimized(shape, VoxelShapes.box(3/16d, 3/16d, 3/16d, 13/16d, 13/16d, 13/16d), IBooleanFunction.OR);
		SHAPE = shape.optimize();
	}
}
