package agency.highlysuspect.incorporeal.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemFrame;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.TickPriority;
import net.minecraft.level.Level;
import net.minecraft.level.server.ServerLevel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

//A lot of this is based on copypaste from ComparatorBlock, obviously.
//It's not practically possible to extend ComparatorBlock I think, things are a bit strange.
//Mainly the comparator uses a block entity to store its signal strength, but that's not
//needed here since there are only 2 strength options/no subtraction mode.
import net.minecraft.block.AbstractBlock.Properties;

public class CrappyComparatorBlock extends CrappyRedstoneDiodeBlock {
	public CrappyComparatorBlock(Properties builder) {
		super(builder);
		
		registerDefaultState(defaultBlockState()
			.setValue(FACING, Direction.NORTH)
			.setValue(POWERED, false)
			.setValue(SENSITIVE, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(FACING, POWERED, SENSITIVE));
	}
	
	//True: 0 maps to 0, everything else maps to 15
	//False: 15 maps to 15, everything else maps to 0
	public static final BooleanProperty SENSITIVE = BooleanProperty.create("sensitive");
	
	@Override
	protected int getDelay(BlockState state) {
		return 20;
	}
	
	@Override
	protected int getOutputSignal(IBlockReader levelIn, BlockPos pos, BlockState state) {
		return state.getValue(POWERED) ? 15 : 0;
	}
	
	//Not a copypaste
	private boolean calculateOutput(Level level, BlockPos pos, BlockState state) {
		int rawPower = getInputSignal(level, pos, state);
		if(state.getValue(SENSITIVE)) return rawPower != 0;
		else return rawPower == 15;
	}
	
	//Copypaste
	protected int getInputSignal(Level levelIn, BlockPos pos, BlockState state) {
		int i = super.getInputSignal(levelIn, pos, state);
		Direction direction = state.getValue(FACING);
		BlockPos blockpos = pos.relative(direction);
		BlockState blockstate = levelIn.getBlockState(blockpos);
		if (blockstate.hasAnalogOutputSignal()) {
			i = blockstate.getAnalogOutputSignal(levelIn, blockpos);
		} else if (i < 15 && blockstate.isRedstoneConductor(levelIn, blockpos)) {
			blockpos = blockpos.relative(direction);
			blockstate = levelIn.getBlockState(blockpos);
			ItemFrame ItemFrame = this.findItemFrame(levelIn, direction, blockpos);
			int j = Math.max(ItemFrame == null ? Integer.MIN_VALUE : ItemFrame.getAnalogOutput(), blockstate.hasAnalogOutputSignal() ? blockstate.getAnalogOutputSignal(levelIn, blockpos) : Integer.MIN_VALUE);
			if (j != Integer.MIN_VALUE) {
				i = j;
			}
		}
		
		return i;
	}
	
	//Copypaste
	@Nullable
	private ItemFrame findItemFrame(Level levelIn, Direction facing, BlockPos pos) {
		List<ItemFrame> list = levelIn.getEntitiesOfClass(ItemFrame.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), (frame) -> frame != null && frame.getDirection() == facing);
		return list.size() == 1 ? list.get(0) : null;
	}
	
	//Copypaste modified to change the other property
	@Override
	public ActionResultType use(BlockState state, Level level, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if(!player.abilities.mayBuild) return ActionResultType.PASS;
		else {
			state = state.cycle(SENSITIVE);
			float pitch = state.getValue(SENSITIVE) ? 0.55f : 0.5f;
			level.playSound(player, pos, SoundEvents.COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, pitch);
			level.setBlock(pos, state, 2);
			
			onStateChange(level, pos, state);
			
			return ActionResultType.sidedSuccess(level.isClientSide);
		}
	}
	
	//Based on a copypaste but modified
	//These are some very vague and confusing MCP names btw
	@Override
	protected void checkTickOnNeighbor(Level levelIn, BlockPos pos, BlockState state) {
		if(!levelIn.getBlockTicks().willTickThisTick(pos, this)) {
			//int i = this.calculateOutput(levelIn, pos, state);
			//TileEntity tileentity = levelIn.getTileEntity(pos);
			//int j = tileentity instanceof ComparatorTileEntity ? ((ComparatorTileEntity)tileentity).getOutputSignal() : 0;
			boolean i = calculateOutput(levelIn, pos, state);
			boolean j = state.getValue(POWERED);
			if (i != j /*|| state.get(POWERED) != this.shouldBePowered(levelIn, pos, state)*/) {
				TickPriority tickpriority = this.shouldPrioritize(levelIn, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
				//changed the hardcoded 2ticks to a getDelay call
				levelIn.getBlockTicks().scheduleTick(pos, this, getDelay(state), tickpriority);
			}
		}
	}
	
	//Based on a copypaste but modified a lot and its barely recognizable lol
	private void onStateChange(Level level, BlockPos pos, BlockState state) {
		boolean isPowered = state.getValue(POWERED);
		boolean shouldPower = calculateOutput(level, pos, state);
		
		if (isPowered != shouldPower) {
			level.setBlock(pos, state.setValue(POWERED, shouldPower), 2);
			this.updateNeighborsInFront(level, pos, state);
		}
	}
	
	//Copypaste
	@Override
	public void tick(BlockState state, ServerLevel levelIn, BlockPos pos, Random rand) {
		this.onStateChange(levelIn, pos, state);
	}
	
	// (no client-events stuff needed, thats for the tile entity)
	
	//Weird Forge extensions, mostly pasted as-is
	@Override
	public boolean getWeakChanges(BlockState state, net.minecraft.level.ILevelReader level, BlockPos pos) {
		//return state.isIn(Blocks.COMPARATOR);
		return true;
	}
	
	@Override
	public void onNeighborChange(BlockState state, net.minecraft.level.ILevelReader level, BlockPos pos, BlockPos neighbor) {
		if (pos.getY() == neighbor.getY() && level instanceof Level && !((Level)level).isClientSide()) {
			state.neighborChanged((Level)level, pos, level.getBlockState(neighbor).getBlock(), neighbor, false);
		}
	}
}
