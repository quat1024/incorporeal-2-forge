package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.OpcodeTile;
import agency.highlysuspect.rhododendrite.block.tile.RhoTileTypes;
import agency.highlysuspect.rhododendrite.block.tile.RhodoNetworkTile;
import agency.highlysuspect.rhododendrite.computer.CoreAction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nullable;

public class OpcodeBlock extends Block implements IWandable {
	public OpcodeBlock(Properties properties) {
		super(properties);
		
		setDefaultState(getDefaultState()
			.with(BlockStateProperties.POWERED, false)
			.with(FAILED, false));
	}
	
	public static final BooleanProperty FAILED = BooleanProperty.create("failed");
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(BlockStateProperties.POWERED, FAILED));
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		setDefaultState(getDefaultState().with(BlockStateProperties.POWERED, false));
		
		boolean shouldPower = world.getRedstonePowerFromNeighbors(pos) > 0;
		boolean isPowered = state.get(BlockStateProperties.POWERED);
		if(isPowered != shouldPower) {
			state = state.with(BlockStateProperties.POWERED, shouldPower);
			world.setBlockState(pos, state);
			
			if(!world.isRemote && shouldPower) {
				OpcodeTile tile = RhoTileTypes.OPCODE.getIfExists(world, pos);
				if(tile != null) {
					CoreAction.Result result = tile.runAction();
					state = state.with(FAILED, result == CoreAction.Result.FAILURE);
					world.setBlockState(pos, state);
				}
			}
		}
	}
	
	//TODO copy to other rhodo network tiles/mess about so the hierarchy is more clear
	@Override
	public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoNetworkTile) {
			((RhodoNetworkTile) tile).whenWanded();
			return true;
		} else return false;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoNetworkTile) ((RhodoNetworkTile) tile).whenPlaced();
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		return state.get(FAILED) ? 15 : 0;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return RhoTileTypes.OPCODE.create();
	}
}
