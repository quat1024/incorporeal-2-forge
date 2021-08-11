package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.CoreTumblerTile;
import agency.highlysuspect.rhododendrite.block.tile.RhoTileTypes;
import agency.highlysuspect.rhododendrite.block.tile.RhodoNetworkTile;
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

public class CoreTumblerBlock extends DirectionalBlockButBetter.PlacesLikePistons implements IWandable {
	public CoreTumblerBlock(Properties builder) {
		super(builder);
		setDefaultState(getDefaultState().with(POWERED, false));
	}
	
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(POWERED));
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		boolean isPowered = state.get(POWERED);
		boolean shouldPower = world.isBlockPowered(pos);
		if(isPowered != shouldPower) {
			world.setBlockState(pos, state.with(POWERED, shouldPower));
			if(shouldPower) {
				CoreTumblerTile tile = RhoTileTypes.CORE_TUMBLER.getIfExists(world, pos);
				if(tile != null) tile.whenPowered();
			}
		}
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return RhoTileTypes.CORE_TUMBLER.create();
	}
	
	//TODO copy to other rhodo network tiles/mess about so the hierarchy is more clear
	@Override
	public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoNetworkTile) {
			((RhodoNetworkTile) tile).sparkle();
			return true;
		} else return false;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoNetworkTile) ((RhodoNetworkTile) tile).tryAutobind();
	}
}
