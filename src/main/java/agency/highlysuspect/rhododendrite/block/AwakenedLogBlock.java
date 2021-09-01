package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.AwakenedLogTile;
import agency.highlysuspect.rhododendrite.computer.CorePathTracing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

//self-mixin in CoreAndAwakenedLogMixin
public class AwakenedLogBlock extends DirectionalBlock {
	public AwakenedLogBlock(Properties properties) {
		super(properties);
	}
	
	public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
	public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 1, CorePathTracing.MAX_RANGE);
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(DISTANCE, FACING));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		//This is like, not *really* an item that you're supposed to place (this block drops the non-awakened version)
		//but it's nice to be able to do it in creative mode etc
		BlockState state = super.getStateForPlacement(context);
		if(state == null) return null;
		
		final BlockState state2 = state.with(FACING, context.getFace().getOpposite());
		
		return CorePathTracing.scanForCore(context.getWorld(), context.getPos(), state2.get(FACING))
			.map(r -> r.toAwakenedLogState(this))
			.orElseGet(() -> unawakenedState(state2)); //java pls
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		if(!CorePathTracing.stillValid(world, pos, state.get(FACING), state.get(DISTANCE))) {
			world.getPendingBlockTicks().scheduleTick(pos, this, 1);
		}
		
		return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
	}
	
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		if(!CorePathTracing.stillValid(world, pos, state.get(FACING), state.get(DISTANCE))) world.setBlockState(pos, unawakenedState(state));
	}
	
	public BlockState unawakenedState(BlockState self) {
		if(self.getBlock() != this) return self;
		else return RhoBlocks.RHODODENDRITE.log.getDefaultState().with(RotatedPillarBlock.AXIS, self.get(FACING).getAxis());
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
	
}
