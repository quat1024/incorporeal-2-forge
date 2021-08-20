package agency.highlysuspect.incorporeal.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

//Mostly a paste of RepeaterBlock, less the stuff about changing the delay.
//Oh compared to 1.12, i also added the repeater-locking. Why not, right?
public class CrappyRepeaterBlock extends CrappyRedstoneDiodeBlock {
	public CrappyRepeaterBlock(Properties properties) {
		super(properties);
		
		setDefaultState(getDefaultState()
			.with(HORIZONTAL_FACING, Direction.NORTH)
			.with(LOCKED, false)
			.with(POWERED, false));
	}
	
	public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(HORIZONTAL_FACING, LOCKED, POWERED));
	}
	
	@Override
	protected int getDelay(BlockState state) {
		return 20;
	}
	
	//from RepeaterBlock, modified just to clean up decompiler cruft (boxed bool, assertion)
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState blockstate = super.getStateForPlacement(context);
		assert blockstate != null;
		return blockstate.with(LOCKED, this.isLocked(context.getWorld(), context.getPos(), blockstate));
	}
	
	//from RepeaterBlock, modified just to clean up decompiler cruft (boxed bool)
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return !worldIn.isRemote() && facing.getAxis() != stateIn.get(HORIZONTAL_FACING).getAxis() ? stateIn.with(LOCKED, this.isLocked(worldIn, currentPos, stateIn)) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	//from RepeaterBlock
	@Override
	public boolean isLocked(IWorldReader worldIn, BlockPos pos, BlockState state) {
		return this.getPowerOnSides(worldIn, pos, state) > 0;
	}
	
	//from RepeaterBlock
	@Override
	protected boolean isAlternateInput(BlockState state) {
		return isDiode(state);
	}
	
	//from RepeaterBlock, modified to hardcode the alternate particle distance
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.get(POWERED)) {
			Direction direction = stateIn.get(HORIZONTAL_FACING);
			double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d1 = (double)pos.getY() + 0.4D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			float f = -5.0F;
			if (rand.nextBoolean()) {
//				f = (float)(stateIn.get(DELAY) * 2 - 1);
				f = 7;
			}
			
			f = f / 16.0F;
			double d3 = (double)(f * (float)direction.getXOffset());
			double d4 = (double)(f * (float)direction.getZOffset());
			worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
		}
	}
}
