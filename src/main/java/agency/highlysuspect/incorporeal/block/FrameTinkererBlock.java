package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.corporea.FrameReader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import vazkii.botania.common.block.BlockModWaterloggable;
import vazkii.botania.mixin.AccessorItemEntity;

import java.util.List;

public class FrameTinkererBlock extends BlockModWaterloggable {
	public FrameTinkererBlock(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(BlockStateProperties.POWERED, false));
	}
	
	private static final double HEIGHT = 3 / 16d;
	private static final VoxelShape BOX = VoxelShapes.create(0, 0, 0, 1, HEIGHT, 1);
	
	//Boring stuff
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BOX;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(BlockStateProperties.POWERED));
	}
	
	//Depositing an item onto the frame tinkerer
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack held = player.getHeldItem(hand);
		if(!held.isEmpty()) {
			if(!world.isRemote) {
				ItemStack deposit;
				if(player.isCreative()) {
					deposit = held.copy();
					deposit.setCount(1);
				} else deposit = held.split(1);
				
				spawnItem(world, pos, deposit);
			}
			
			return ActionResultType.SUCCESS;
		}
		
		return ActionResultType.PASS;
	}
	
	//Powering the frame tinkerer
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
		boolean shouldPower = world.getRedstonePowerFromNeighbors(pos) > 0;
		boolean isPowered = state.get(BlockStateProperties.POWERED);
		if(shouldPower != isPowered) {
			world.setBlockState(pos, state.func_235896_a_(BlockStateProperties.POWERED)); //cycle
			
			if(!world.isRemote && shouldPower) {
				//Choose a random item entity resting on the frame tinkerer.
				List<ItemEntity> itemChoices = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos), ent -> ent.isAlive() && !ent.getItem().isEmpty() && ent.getItem().getCount() == 1);
				
				//When switching an item entity with an item frame, it's okay if the frame is empty (it "places the item in the frame").
				//When I'm only taking an item out of an item frame, the frame has to be non-empty (because if not, i'd switch two empty frames with each other)
				boolean allowEmpty = !itemChoices.isEmpty();
				List<ItemFrameEntity> frameChoices = allowEmpty ? FrameReader.near(world, pos) : FrameReader.nonEmptyNear(world, pos);
				
				if(frameChoices.isEmpty()) return; //No candidate frames are present to switch with.
				ItemFrameEntity frame = Inc.choose(frameChoices, world.rand);
				
				if(itemChoices.isEmpty()) {
					switchWithNothing(world, pos, frame);
				} else {
					switchWithEntity(world, pos, frame, Inc.choose(itemChoices, world.rand));
				}
			}
		}
	}
	
	private void switchWithEntity(World world, BlockPos pos, ItemFrameEntity frame, ItemEntity switchWith) {
		//Take the item out of the item frame
		ItemStack formerlyInFrame = frame.getDisplayedItem().copy();
		
		//Copy the item entity's item into the frame
		frame.setDisplayedItem(switchWith.getItem());
		
		//Copy the old frame's item into the item entity
		switchWith.setItem(formerlyInFrame);
		switchWith.setPickupDelay(30);
		((AccessorItemEntity) switchWith).setAge(0);
	}
	
	private void switchWithNothing(World world, BlockPos pos, ItemFrameEntity frame) {
		//Take the item out of the item frame
		ItemStack frameItem = frame.getDisplayedItem().copy();
		frame.setDisplayedItem(ItemStack.EMPTY);
		
		//(setDisplayedItem doesn't play a sound when re*moving* an item, but it does when re*placing*)
		world.playSound(null, frame.getPosX(), frame.getPosY(), frame.getPosZ(), SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1f, 1f);
		
		//Spawn that item on the plate
		spawnItem(world, pos, frameItem);
	}
	
	private static void spawnItem(World world, BlockPos pos, ItemStack stack) {
		ItemEntity ent = new ItemEntity(world, pos.getX() + .5, pos.getY() + HEIGHT, pos.getZ() + .5, stack);
		ent.setMotion(0, 0, 0);
		ent.setPickupDelay(30);
		world.addEntity(ent);
	}
}
