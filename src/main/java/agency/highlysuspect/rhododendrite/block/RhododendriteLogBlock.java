package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class RhododendriteLogBlock extends RotatedPillarBlock {
	public RhododendriteLogBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
		BlockState awakenedState = awakenedState(world, pos, state);
		if(awakenedState != null) world.setBlockState(pos, awakenedState);
	}
	
	//Returns `null` if it's not appropriate for this log to become activated right now.
	protected @Nullable BlockState awakenedState(World world, BlockPos pos, BlockState state) {
		Direction.Axis axis = state.get(RotatedPillarBlock.AXIS);
		BlockState awakenedState = scan(world, pos, state, Rho.positive(axis));
		return awakenedState != null ? awakenedState : scan(world, pos, state, Rho.negative(axis));
	}
	
	protected @Nullable BlockState scan(World world, BlockPos pos, BlockState state, Direction scanDir) {
		//search "out" from myself, in the direction of scanDir, for a core
		BlockPos.Mutable scanPos = pos.toMutable();
		for(int distance = 1; distance < CoreTile.MAX_RANGE; distance++) {
			scanPos.move(scanDir);
			BlockState stateThere = world.getBlockState(scanPos);
			
			//skip over blocks that are okay to have in the path of a rhododendrite stack - (TODO this could be a tag)
			if(stateThere.getBlock() == RhoBlocks.RHODODENDRITE.leaves || stateThere.getBlock() instanceof AwakenedLogBlock) continue;
			
			if(stateThere.getBlock() instanceof CoreBlock) {
				return RhoBlocks.AWAKENED_LOG.getDefaultState()
					.with(AwakenedLogBlock.DISTANCE, distance)
					.with(AwakenedLogBlock.FACING, scanDir);
			} else return null;
		}
		
		return null;
	}
	
	//In lieu of letting you just add things to the stinkin axe-item stripping map, Forge provides... whatever this is!
	//Yeah man, I dunno.
	@Nullable
	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		if(toolType == ToolType.AXE) return RhoBlocks.RHODODENDRITE.strippedLog.getDefaultState().with(RotatedPillarBlock.AXIS, state.get(RotatedPillarBlock.AXIS));
		return super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}
}
