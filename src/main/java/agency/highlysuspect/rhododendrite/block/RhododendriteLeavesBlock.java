package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.computer.CorePathTracing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RhododendriteLeavesBlock extends LeavesBlock {
	public RhododendriteLeavesBlock(Properties properties) {
		super(properties);
	}
	
	//Schedule ticks when adding or removing the leaves block.
	//This tells nearby (awakened)logs to check if they should (de)activate, since they might not directly receive a block update.
	//Awakened logs and regular logs both check if they should flip status in their scheduleTick method.
	private void scheduleSomeTicks(World world, BlockPos pos) {
		nextDir: for(Direction dir : Direction.values()) {
			for(int i = 1; i < CorePathTracing.MAX_RANGE; i++) {
				BlockPos offsetPos = pos.offset(dir, i);
				Block block = world.getBlockState(offsetPos).getBlock();
				if(block instanceof AwakenedLogBlock || block instanceof RhododendriteLogBlock) {
					world.getPendingBlockTicks().scheduleTick(offsetPos, block, 1);
					continue nextDir;
				}
			}
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		scheduleSomeTicks(world, pos);
	}
	
	//Is onReplaced the right place to call this
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if(newState.getBlock() != this) {
			scheduleSomeTicks(world, pos);
		}
		
		super.onReplaced(state, world, pos, newState, isMoving);
	}
}
