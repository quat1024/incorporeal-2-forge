package agency.highlysuspect.incorporeal.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import javax.annotation.Nullable;

public class CorporeaRetainerEvaporatorBlock extends Block {
	public CorporeaRetainerEvaporatorBlock(Properties properties) {
		super(properties);
		
		setDefaultState(getDefaultState().with(BlockStateProperties.POWERED, false));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		updatePoweredState(world, pos, state);
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromBlock, boolean isMoving) {
		updatePoweredState(world, pos, state);
	}
	
	private void updatePoweredState(World world, BlockPos pos, BlockState state) {
		boolean shouldPower = world.isBlockPowered(pos); // "strong power"; it was like this in the original, idk
		boolean isPowered = state.get(BlockStateProperties.POWERED);
		
		if(shouldPower != isPowered) {
			world.setBlockState(pos, state.with(BlockStateProperties.POWERED, shouldPower));
			
			if(shouldPower) {
				for(Direction horiz : Direction.Plane.HORIZONTAL) {
					TileEntity tile = world.getTileEntity(pos.offset(horiz));
					if(tile instanceof TileCorporeaRetainer) {
						TileCorporeaRetainer retainer = (TileCorporeaRetainer) tile;
						if(!retainer.hasPendingRequest()) continue;
						
						Duck duck = (Duck) tile;
						
						//reduce the request size by 1, or clear it if that would make the request empty
						int newCount = duck.inc$getRequestCount() - 1;
						if(newCount > 0) duck.inc$setRequestCount(newCount);
						else duck.inc$clearRequest();
						
						//if the comparator signal is different, send comparator updates
						if(duck.inc$updateComparator()) world.updateComparatorOutputLevel(pos.offset(horiz), ModBlocks.corporeaRetainer);
					}
				}
			}
		}
	}
	
	//Quack.
	public interface Duck {
		int inc$getRequestCount();
		void inc$setRequestCount(int newCount);
		void inc$clearRequest();
		
		boolean inc$updateComparator();
	}
}
