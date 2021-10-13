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
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public abstract class AbstractComputerBlock extends Block {
	public AbstractComputerBlock(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(DirectionalBlock.FACING, Direction.UP));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(DirectionalBlock.FACING));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(DirectionalBlock.FACING, context.getNearestLookingDirection());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SORTA_DETAILED_SHAPE;
	}
	
	//A bounding box.
	//private static final VoxelShape VERY_ROUGH_SHAPE = VoxelShapes.create(1 / 16d, 1 / 16d, 1 / 16d, 15 / 16d, 15 / 16d, 15 / 16d);
	//A solid box decorated with 8 corners.
	private static final VoxelShape SORTA_DETAILED_SHAPE;
	
	static {
		//shoutouts to the "VoxelShape Generator" blockbench plugin lol.
		VoxelShape shape = VoxelShapes.empty();
		//Eight corners
		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.75, 0.0625, 0.0625, 0.9375, 0.25, 0.25), IBooleanFunction.OR);
		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.0625, 0.0625, 0.0625, 0.25, 0.25, 0.25), IBooleanFunction.OR);
		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.75, 0.0625, 0.75, 0.9375, 0.25, 0.9375), IBooleanFunction.OR);
		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.0625, 0.0625, 0.75, 0.25, 0.25, 0.9375), IBooleanFunction.OR);
		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.0625, 0.75, 0.0625, 0.25, 0.9375, 0.25), IBooleanFunction.OR);
		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.75, 0.75, 0.0625, 0.9375, 0.9375, 0.25), IBooleanFunction.OR);
		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.75, 0.75, 0.75, 0.9375, 0.9375, 0.9375), IBooleanFunction.OR);
		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.0625, 0.75, 0.75, 0.25, 0.9375, 0.9375), IBooleanFunction.OR);
		
		//Big block in the middle because actually having a hole in the middle of the block was annoying.
		VoxelShape sortaDetailedUnsimplified = VoxelShapes.combine(shape, VoxelShapes.create(3/16d, 3/16d, 3/16d, 13/16d, 13/16d, 13/16d), IBooleanFunction.OR);
		SORTA_DETAILED_SHAPE = sortaDetailedUnsimplified.simplify();
		
		//Twelve edges.
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.25, 0.125, 0.125, 0.75, 0.25, 0.25), IBooleanFunction.OR);
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.25, 0.125, 0.75, 0.75, 0.25, 0.875), IBooleanFunction.OR);
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.125, 0.125, 0.25, 0.25, 0.25, 0.75), IBooleanFunction.OR);
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.125, 0.25, 0.125, 0.25, 0.75, 0.25), IBooleanFunction.OR);
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.75, 0.25, 0.125, 0.875, 0.75, 0.25), IBooleanFunction.OR);
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.75, 0.25, 0.75, 0.875, 0.75, 0.875), IBooleanFunction.OR);
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.125, 0.25, 0.75, 0.25, 0.75, 0.875), IBooleanFunction.OR);
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.75, 0.125, 0.25, 0.875, 0.25, 0.75), IBooleanFunction.OR);
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.25, 0.75, 0.125, 0.75, 0.875, 0.25), IBooleanFunction.OR);
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.125, 0.75, 0.25, 0.25, 0.875, 0.75), IBooleanFunction.OR);
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.25, 0.75, 0.75, 0.75, 0.875, 0.875), IBooleanFunction.OR);
//		shape = VoxelShapes.combine(shape, VoxelShapes.create(0.75, 0.75, 0.25, 0.875, 0.875, 0.75), IBooleanFunction.OR);
//		
//		VERY_DETAILED_SHAPE = shape.simplify();
	}
}
