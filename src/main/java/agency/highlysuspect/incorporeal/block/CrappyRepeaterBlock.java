package agency.highlysuspect.incorporeal.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

//Mostly a paste of RepeaterBlock, less the stuff about changing the delay.
//Oh compared to 1.12, i also added the repeater-locking. Why not, right?
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class CrappyRepeaterBlock extends CrappyRedstoneDiodeBlock {
	public CrappyRepeaterBlock(Properties properties) {
		super(properties);
		
		registerDefaultState(defaultBlockState()
			.setValue(FACING, Direction.NORTH)
			.setValue(LOCKED, false)
			.setValue(POWERED, false));
	}
	
	public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(FACING, LOCKED, POWERED));
	}
	
	@Override
	protected int getDelay(BlockState state) {
		return 20;
	}
	
	//from RepeaterBlock, modified just to clean up decompiler cruft (boxed bool, assertion)
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = super.getStateForPlacement(context);
		assert blockstate != null;
		return blockstate.setValue(LOCKED, this.isLocked(context.getLevel(), context.getClickedPos(), blockstate));
	}
	
	//from RepeaterBlock, modified just to clean up decompiler cruft (boxed bool)
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		return !worldIn.isClientSide() && facing.getAxis() != stateIn.getValue(FACING).getAxis() ? stateIn.setValue(LOCKED, this.isLocked(worldIn, currentPos, stateIn)) : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	//from RepeaterBlock
	@Override
	public boolean isLocked(LevelReader worldIn, BlockPos pos, BlockState state) {
		return this.getAlternateSignal(worldIn, pos, state) > 0;
	}
	
	//from RepeaterBlock
	@Override
	protected boolean isAlternateInput(BlockState state) {
		return isDiode(state);
	}
	
	//from RepeaterBlock, modified to hardcode the alternate particle distance
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		if (stateIn.getValue(POWERED)) {
			Direction direction = stateIn.getValue(FACING);
			double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d1 = (double)pos.getY() + 0.4D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			float f = -5.0F;
			if (rand.nextBoolean()) {
//				f = (float)(stateIn.get(DELAY) * 2 - 1);
				f = 7;
			}
			
			f = f / 16.0F;
			double d3 = (double)(f * (float)direction.getStepX());
			double d4 = (double)(f * (float)direction.getStepZ());
			worldIn.addParticle(DustParticleOptions.REDSTONE, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
		}
	}
}
