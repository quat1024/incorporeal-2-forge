package agency.highlysuspect.incorporeal.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import vazkii.botania.common.item.ModItems;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.ItemLike;

public class RedstoneRootCropBlock extends CropBlock implements IPlantable {
	public RedstoneRootCropBlock(Properties builder) {
		super(builder);
	}
	
	public static final int AGE_MAX = 6;
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, AGE_MAX);
	
	public static final VoxelShape[] SHAPES = Util.make(new VoxelShape[AGE_MAX + 1], arr -> {
		for(int i = 0; i <= AGE_MAX; i++) {
			double yea = (AGE_MAX - i) / 32d;
			arr[i] = Shapes.box(yea, 0, yea, 1 - yea, 3/16d, 1 - yea);
		}
	});
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		//No call to super() since that adds the cropsblock default age property.
		builder.add(AGE);
	}
	
	@Override
	public IntegerProperty getAgeProperty() {
		return AGE;
	}
	
	@Override
	public int getMaxAge() {
		return AGE_MAX;
	}
	
	@Override
	protected ItemLike getBaseSeedId() {
		return ModItems.redstoneRoot;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPES[state.getValue(AGE)];
	}
	
	//the typical block -> item transformation doesn't take seeds into account, i don't think
	//needed for isReplaceable I think, it checks this.asItem
	@Override
	public Item asItem() {
		return ModItems.redstoneRoot;
	}
	
	//Weird forge extension thing
	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.CROP;
	}
	
	public static void interactEvent(PlayerInteractEvent.RightClickBlock e) {
		ItemStack stack = e.getItemStack();
		BlockHitResult hit = e.getHitVec();
		Player player = e.getPlayer();
		Level world = e.getWorld();
		
		if(world != null && stack.getItem() == ModItems.redstoneRoot && hit != null && hit.getType() == HitResult.Type.BLOCK) {
			//see BlockItemUseContext#offsetPos
			BlockPos targetPos = hit.getBlockPos().relative(hit.getDirection());
			
			BlockPlaceContext haha = new BlockPlaceContext(e.getPlayer(), e.getHand(), stack, hit);
			
			//this particular arrangement of targetPos and getFace is correct, see BucketItem
			if(e.getPlayer().mayUseItemAt(targetPos, hit.getDirection(), stack) &&
				world.getBlockState(targetPos).canBeReplaced(haha) &&
				//weird forge extension
				world.getBlockState(targetPos.below()).canSustainPlant(world, targetPos.below(), Direction.UP, IncBlocks.REDSTONE_ROOT_CROP)) {
				
				world.setBlockAndUpdate(targetPos, IncBlocks.REDSTONE_ROOT_CROP.defaultBlockState());
				
				SoundType type = SoundType.GRASS;
				SoundEvent sound = type.getPlaceSound(); 
				world.playSound(player, targetPos, sound, SoundSource.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
				
				if(!player.isCreative()) stack.shrink(1);
				if(player instanceof ServerPlayer) {
					CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, targetPos, stack);
				}
				
				e.setCanceled(true);
				e.setCancellationResult(InteractionResult.SUCCESS);
			}
		}
	}
}
