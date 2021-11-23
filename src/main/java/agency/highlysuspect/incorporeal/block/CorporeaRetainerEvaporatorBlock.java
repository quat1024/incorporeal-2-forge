package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.corporea.RetainerDuck;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class CorporeaRetainerEvaporatorBlock extends Block {
	public CorporeaRetainerEvaporatorBlock(Properties properties) {
		super(properties);
		
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		updatePoweredState(level, pos, state);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromBlock, boolean isMoving) {
		updatePoweredState(level, pos, state);
	}
	
	private void updatePoweredState(Level level, BlockPos pos, BlockState state) {
		boolean shouldPower = level.hasNeighborSignal(pos); // "strong power"; it was like this in the original, idk
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		
		if(shouldPower != isPowered) {
			level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, shouldPower));
			
			if(shouldPower) {
				for(Direction horiz : Direction.Plane.HORIZONTAL) {
					TileEntity tile = level.getBlockEntity(pos.relative(horiz));
					if(tile != null) {
						tile.getCapability(SolidifiedRequest.Cap.INSTANCE).ifPresent(holder -> {
							SolidifiedRequest solid = holder.getRequest();
							SolidifiedRequest shrunk = solid.withCount(solid.count - 1);
							if(shrunk.count >= 0) holder.setRequest(shrunk);
						});
					}
				}
			}
		}
	}
}
