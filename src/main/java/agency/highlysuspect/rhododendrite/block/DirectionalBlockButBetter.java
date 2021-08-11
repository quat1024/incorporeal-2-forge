package agency.highlysuspect.rhododendrite.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public class DirectionalBlockButBetter extends DirectionalBlock {
	public DirectionalBlockButBetter(Properties builder) {
		super(builder);
	}
	
	public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		//RotatedPillarBlock does this for you, but DirectionalBlock doesn't...
		super.fillStateContainer(builder.add(FACING));
	}
	
	public static class PlacesLikeLogs extends DirectionalBlockButBetter {
		public PlacesLikeLogs(Properties builder) {
			super(builder);
		}
		
		@Nullable
		@Override
		public BlockState getStateForPlacement(BlockItemUseContext context) {
			return getDefaultState().with(FACING, context.getFace().getOpposite());
		}
	}
	
	public static class PlacesLikePistons extends DirectionalBlockButBetter {
		public PlacesLikePistons(Properties builder) {
			super(builder);
		}
		
		@Nullable
		@Override
		public BlockState getStateForPlacement(BlockItemUseContext context) {
			return getDefaultState().with(FACING, context.getNearestLookingDirection());
		}
	}
}
