package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import agency.highlysuspect.rhododendrite.block.tile.FragmentContainerTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CoreBlock extends DirectionalBlock {
	public CoreBlock(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(FACING, Direction.UP));
	}
	
	public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(FACING));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getFace().getOpposite());
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new CoreTile();
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof FragmentContainerTile) return ((FragmentContainerTile) tile).signalStrength();
		else return 0;
	}
}
