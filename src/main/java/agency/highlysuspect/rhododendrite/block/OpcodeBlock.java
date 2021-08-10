package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import agency.highlysuspect.rhododendrite.block.tile.OpcodeTile;
import agency.highlysuspect.rhododendrite.block.tile.RhoTileTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class OpcodeBlock extends Block {
	public OpcodeBlock(Properties properties, CoreAction action) {
		super(properties);
		this.action = action;
		
		setDefaultState(getDefaultState().with(BlockStateProperties.POWERED, false));
	}
	
	public final CoreAction action;
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		setDefaultState(getDefaultState().with(BlockStateProperties.POWERED, false));
		
		boolean shouldPower = world.getRedstonePowerFromNeighbors(pos) > 0;
		boolean isPowered = state.get(BlockStateProperties.POWERED);
		if(isPowered != shouldPower) {
			world.setBlockState(pos, state.with(BlockStateProperties.POWERED, shouldPower));
			
			if(!world.isRemote && shouldPower) {
				OpcodeTile tile = RhoTileTypes.OPCODE.getIfExists(world, pos);
				if(tile != null) {
					CoreTile core = tile.findCore();
					if(core != null) {
						action.apply(world, pos, state, core);
					}
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
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return RhoTileTypes.OPCODE.create();
	}
	
	public interface CoreAction {
		void apply(World world, BlockPos pos, BlockState state, CoreTile core);
	}
}
