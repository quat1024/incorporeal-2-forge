package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.corporea.FrameReader;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.botania.common.block.BlockModWaterloggable;
import vazkii.botania.mixin.AccessorItemEntity;

import java.util.List;

public class FrameTinkererBlock extends BlockModWaterloggable {
	public FrameTinkererBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false));
	}
	
	private static final double HEIGHT = 3 / 16d;
	private static final VoxelShape BOX = Shapes.box(0, 0, 0, 1, HEIGHT, 1);
	
	//Boring stuff
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return BOX;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.POWERED));
	}
	
	//Depositing an item onto the frame tinkerer
	@Override
	public ActionResultType use(BlockState state, Level level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack held = player.getItemInHand(hand);
		if(!held.isEmpty()) {
			if(!level.isClientSide) {
				ItemStack deposit;
				if(player.isCreative()) {
					deposit = held.copy();
					deposit.setCount(1);
				} else deposit = held.split(1);
				
				spawnItem(level, pos, deposit);
			}
			
			return ActionResultType.SUCCESS;
		}
		
		return ActionResultType.PASS;
	}
	
	//Powering the frame tinkerer
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
		boolean shouldPower = level.getBestNeighborSignal(pos) > 0;
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		if(shouldPower != isPowered) {
			level.setBlockAndUpdate(pos, state.cycle(BlockStateProperties.POWERED)); //cycle
			
			if(!level.isClientSide && shouldPower) {
				//Choose a random item entity resting on the frame tinkerer.
				List<ItemEntity> itemChoices = level.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(pos), ent -> ent.isAlive() && !ent.getItem().isEmpty() && ent.getItem().getCount() == 1);
				
				//When switching an item entity with an item frame, it's okay if the frame is empty (it "places the item in the frame").
				//When I'm only taking an item out of an item frame, the frame has to be non-empty (because if not, i'd switch two empty frames with each other)
				boolean allowEmpty = !itemChoices.isEmpty();
				List<ItemFrame> frameChoices = allowEmpty ? FrameReader.near(level, pos) : FrameReader.nonEmptyNear(level, pos);
				
				if(frameChoices.isEmpty()) return; //No candidate frames are present to switch with.
				ItemFrame frame = Inc.choose(frameChoices, level.random);
				
				if(itemChoices.isEmpty()) {
					switchWithNothing(level, pos, frame);
				} else {
					switchWithEntity(level, pos, frame, Inc.choose(itemChoices, level.random));
				}
			}
		}
	}
	
	private void switchWithEntity(Level level, BlockPos pos, ItemFrame frame, ItemEntity switchWith) {
		//Take the item out of the item frame
		ItemStack formerlyInFrame = frame.getItem().copy();
		
		//Copy the item entity's item into the frame
		frame.setItem(switchWith.getItem());
		
		//Copy the old frame's item into the item entity
		switchWith.setItem(formerlyInFrame);
		switchWith.setPickUpDelay(30);
		((AccessorItemEntity) switchWith).setAge(0);
	}
	
	private void switchWithNothing(Level level, BlockPos pos, ItemFrame frame) {
		//Take the item out of the item frame
		ItemStack frameItem = frame.getItem().copy();
		frame.setItem(ItemStack.EMPTY);
		
		//(setDisplayedItem doesn't play a sound when re*moving* an item, but it does when re*placing*)
		level.playSound(null, frame.getX(), frame.getY(), frame.getZ(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1f, 1f);
		
		//Spawn that item on the plate
		spawnItem(level, pos, frameItem);
	}
	
	private static void spawnItem(Level level, BlockPos pos, ItemStack stack) {
		ItemEntity ent = new ItemEntity(level, pos.getX() + .5, pos.getY() + HEIGHT, pos.getZ() + .5, stack);
		ent.setDeltaMovement(0, 0, 0);
		ent.setPickUpDelay(30);
		level.addFreshEntity(ent);
	}
}
