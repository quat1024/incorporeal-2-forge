package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.RhodoOpTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RhodoOpBlock extends AbstractComputerBlock {
	public RhodoOpBlock(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(BlockStateProperties.POWERED, false));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block from, BlockPos fromPos, boolean isMoving) {
		boolean shouldPower = world.getRedstonePowerFromNeighbors(pos) > 0;
		boolean isPowered = state.get(BlockStateProperties.POWERED);
		if(shouldPower != isPowered) {
			world.setBlockState(pos, state.with(BlockStateProperties.POWERED, shouldPower));
			if(shouldPower) {
				TileEntity tile = world.getTileEntity(pos);
				if(tile instanceof RhodoOpTile) {
					((RhodoOpTile) tile).onRedstonePower();
				}
			}
		}
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState blockState, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		return tile instanceof RhodoOpTile ? ((RhodoOpTile) tile).getComparatorSignal() : 0;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new RhodoOpTile();
	}
}
