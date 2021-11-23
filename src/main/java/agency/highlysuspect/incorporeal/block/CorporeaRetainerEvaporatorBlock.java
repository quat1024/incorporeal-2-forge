package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class CorporeaRetainerEvaporatorBlock extends Block {
	public CorporeaRetainerEvaporatorBlock(Properties properties) {
		super(properties);
		
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		updatePoweredState(world, pos, state);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromBlock, boolean isMoving) {
		updatePoweredState(world, pos, state);
	}
	
	private void updatePoweredState(Level world, BlockPos pos, BlockState state) {
		boolean shouldPower = world.hasNeighborSignal(pos); // "strong power"; it was like this in the original, idk
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		
		if(shouldPower != isPowered) {
			world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, shouldPower));
			
			if(shouldPower) {
				for(Direction horiz : Direction.Plane.HORIZONTAL) {
					BlockEntity tile = world.getBlockEntity(pos.relative(horiz));
//					if(tile != null) {
//						tile.getCapability(SolidifiedRequest.Cap.INSTANCE).ifPresent(holder -> {
//							SolidifiedRequest solid = holder.getRequest();
//							SolidifiedRequest shrunk = solid.withCount(solid.count - 1);
//							if(shrunk.count >= 0) holder.setRequest(shrunk);
//						});
//					}
				}
			}
		}
	}
}
