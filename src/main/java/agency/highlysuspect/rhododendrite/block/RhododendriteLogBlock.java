package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.computer.CorePathTracing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class RhododendriteLogBlock extends RotatedPillarBlock {
	public RhododendriteLogBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context);
		if(state == null) return null;
		else return awakenedState(context.getWorld(), context.getPos(), state).orElse(state);
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		if(awakenedState(world, pos, state).isPresent()) {
			world.getPendingBlockTicks().scheduleTick(pos, this, 1);
		}
		
		return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
	}
	
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		awakenedState(world, pos, state).ifPresent(awakeState -> world.setBlockState(pos, awakeState));
	}
	
	protected Optional<BlockState> awakenedState(IWorldReader world, BlockPos pos, BlockState state) {
		return CorePathTracing.scanForCore(world, pos, state.get(RotatedPillarBlock.AXIS)).map(r -> r.toAwakenedLogState(RhoBlocks.AWAKENED_LOG));
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
