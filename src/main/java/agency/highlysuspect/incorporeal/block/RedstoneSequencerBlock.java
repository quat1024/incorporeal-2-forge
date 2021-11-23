package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.block.tile.RedstoneSequencerTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nullable;

public class RedstoneSequencerBlock extends Block implements IWandable, ITileEntityProvider {
	public RedstoneSequencerBlock(Properties properties) {
		super(properties);
		
		setDefaultState(getDefaultState().with(BlockStateProperties.POWER_0_15, 0));
	}
	
	@Override
	public boolean canProvidePower(BlockState state) {
		//mcp calls this "canProvidePower", but it's more like "does redstone dust point into this"
		return true;
	}
	
	@Override
	public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return state.get(BlockStateProperties.POWER_0_15);
	}
	
	@Override
	public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return state.get(BlockStateProperties.POWER_0_15);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(BlockStateProperties.POWER_0_15));
	}
	
	//Forge says this is deprecated because they have their own version for no reason.
	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new RedstoneSequencerTile();
	}
	
	//IWandable. Blank implementation is needed so the WotF can work. Hmm.
	@Override
	public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
		return true;
	}
}
