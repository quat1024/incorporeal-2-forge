package agency.highlysuspect.incorporeal.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.util.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

//A lot of this is based on copypaste from ComparatorBlock, obviously.
//It's not practically possible to extend ComparatorBlock I think, things are a bit strange.
//Mainly the comparator uses a block entity to store its signal strength, but that's not
//needed here since there are only 2 strength options/no subtraction mode.
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

public class CrappyComparatorBlock extends CrappyRedstoneDiodeBlock {
	public CrappyComparatorBlock(Properties builder) {
		super(builder);
		
		registerDefaultState(defaultBlockState()
			.setValue(FACING, Direction.NORTH)
			.setValue(POWERED, false)
			.setValue(SENSITIVE, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
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
	protected int getOutputSignal(BlockGetter worldIn, BlockPos pos, BlockState state) {
		return state.getValue(POWERED) ? 15 : 0;
	}
	
	//Not a copypaste
	private boolean calculateOutput(Level world, BlockPos pos, BlockState state) {
		int rawPower = getInputSignal(world, pos, state);
		if(state.getValue(SENSITIVE)) return rawPower != 0;
		else return rawPower == 15;
	}
	
	//Copypaste
	protected int getInputSignal(Level worldIn, BlockPos pos, BlockState state) {
		int i = super.getInputSignal(worldIn, pos, state);
		Direction direction = state.getValue(FACING);
		BlockPos blockpos = pos.relative(direction);
		BlockState blockstate = worldIn.getBlockState(blockpos);
		if (blockstate.hasAnalogOutputSignal()) {
			i = blockstate.getAnalogOutputSignal(worldIn, blockpos);
		} else if (i < 15 && blockstate.isRedstoneConductor(worldIn, blockpos)) {
			blockpos = blockpos.relative(direction);
			blockstate = worldIn.getBlockState(blockpos);
			ItemFrame itemframeentity = this.findItemFrame(worldIn, direction, blockpos);
			int j = Math.max(itemframeentity == null ? Integer.MIN_VALUE : itemframeentity.getAnalogOutput(), blockstate.hasAnalogOutputSignal() ? blockstate.getAnalogOutputSignal(worldIn, blockpos) : Integer.MIN_VALUE);
			if (j != Integer.MIN_VALUE) {
				i = j;
			}
		}
		
		return i;
	}
	
	//Copypaste
	@Nullable
	private ItemFrame findItemFrame(Level worldIn, Direction facing, BlockPos pos) {
		List<ItemFrame> list = worldIn.getEntitiesOfClass(ItemFrame.class, new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), (frame) -> frame != null && frame.getDirection() == facing);
		return list.size() == 1 ? list.get(0) : null;
	}
	
	//Copypaste modified to change the other property
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if(!player.abilities.mayBuild) return InteractionResult.PASS;
		else {
			state = state.cycle(SENSITIVE);
			float pitch = state.getValue(SENSITIVE) ? 0.55f : 0.5f;
			world.playSound(player, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3f, pitch);
			world.setBlock(pos, state, 2);
			
			onStateChange(world, pos, state);
			
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
	}
	
	//Based on a copypaste but modified
	//These are some very vague and confusing MCP names btw
	@Override
	protected void checkTickOnNeighbor(Level worldIn, BlockPos pos, BlockState state) {
		if(!worldIn.getBlockTicks().willTickThisTick(pos, this)) {
			//int i = this.calculateOutput(worldIn, pos, state);
			//TileEntity tileentity = worldIn.getTileEntity(pos);
			//int j = tileentity instanceof ComparatorTileEntity ? ((ComparatorTileEntity)tileentity).getOutputSignal() : 0;
			boolean i = calculateOutput(worldIn, pos, state);
			boolean j = state.getValue(POWERED);
			if (i != j /*|| state.get(POWERED) != this.shouldBePowered(worldIn, pos, state)*/) {
				TickPriority tickpriority = this.shouldPrioritize(worldIn, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
				//changed the hardcoded 2ticks to a getDelay call
				worldIn.getBlockTicks().scheduleTick(pos, this, getDelay(state), tickpriority);
			}
		}
	}
	
	//Based on a copypaste but modified a lot and its barely recognizable lol
	private void onStateChange(Level world, BlockPos pos, BlockState state) {
		boolean isPowered = state.getValue(POWERED);
		boolean shouldPower = calculateOutput(world, pos, state);
		
		if (isPowered != shouldPower) {
			world.setBlock(pos, state.setValue(POWERED, shouldPower), 2);
			this.updateNeighborsInFront(world, pos, state);
		}
	}
	
	//Copypaste
	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		this.onStateChange(worldIn, pos, state);
	}
	
	// (no client-events stuff needed, thats for the tile entity)
	
	//Weird Forge extensions, mostly pasted as-is
	@Override
	public boolean getWeakChanges(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos) {
		//return state.isIn(Blocks.COMPARATOR);
		return true;
	}
	
	@Override
	public void onNeighborChange(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos, BlockPos neighbor) {
		if (pos.getY() == neighbor.getY() && world instanceof Level && !((Level)world).isClientSide()) {
			state.neighborChanged((Level)world, pos, world.getBlockState(neighbor).getBlock(), neighbor, false);
		}
	}
}
