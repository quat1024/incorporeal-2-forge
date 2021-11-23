package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.RhodoFunnelTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.Level;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class RhodoFunnelBlock extends AbstractComputerBlock {
	public RhodoFunnelBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block from, BlockPos fromPos, boolean isMoving) {
		boolean shouldPower = level.getBestNeighborSignal(pos) > 0;
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		if(shouldPower != isPowered) {
			level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, shouldPower));
			if(shouldPower) {
				TileEntity tile = level.getBlockEntity(pos);
				if(tile instanceof RhodoFunnelTile) {
					((RhodoFunnelTile) tile).onRedstonePower();
				}
			}
		}
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new RhodoFunnelTile();
	}
}
