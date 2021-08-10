package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.AwakenedLogTile;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import agency.highlysuspect.rhododendrite.block.tile.FragmentContainerTile;
import agency.highlysuspect.rhododendrite.block.tile.RhoTileTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AwakenedLogBlock extends DirectionalBlock {
	public AwakenedLogBlock(Properties properties) {
		super(properties);
	}
	
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 1, CoreTile.MAX_RANGE);
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		//yeah you have to add FACING manually because DirectionalBlock doesnt do it for you. good job Mojang
		super.fillStateContainer(builder.add(FACING, DISTANCE));
	}
	
	public boolean shouldDeactivate(World world, BlockPos pos, BlockState state) {
		return !(world.getBlockState(pos.offset(state.get(FACING), state.get(DISTANCE))).getBlock() instanceof CoreBlock);
	}
	
	public @Nullable CoreTile getCore(World world, BlockPos pos, BlockState state) {
		return RhoTileTypes.CORE.getIfExists(world, pos.offset(state.get(FACING), state.get(DISTANCE)));
	}
	
	public BlockState unawakenedState(BlockState self) {
		if(self.getBlock() != this) return self;
		else return RhoBlocks.RHODODENDRITE.log.getDefaultState().with(RotatedPillarBlock.AXIS, self.get(FACING).getAxis());
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if(shouldDeactivate(world, pos, state)) {
			world.setBlockState(pos, unawakenedState(state));
		}
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AwakenedLogTile();
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
