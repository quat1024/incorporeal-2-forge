package agency.highlysuspect.incorporeal.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.Level;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import vazkii.botania.common.item.ModItems;

import net.minecraft.block.AbstractBlock.Properties;

public class RedstoneRootCropBlock extends CropsBlock implements IPlantable {
	public RedstoneRootCropBlock(Properties builder) {
		super(builder);
	}
	
	public static final int AGE_MAX = 6;
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, AGE_MAX);
	
	public static final VoxelShape[] SHAPES = Util.make(new VoxelShape[AGE_MAX + 1], arr -> {
		for(int i = 0; i <= AGE_MAX; i++) {
			double yea = (AGE_MAX - i) / 32d;
			arr[i] = VoxelShapes.box(yea, 0, yea, 1 - yea, 3/16d, 1 - yea);
		}
	});
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
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
	protected IItemProvider getBaseSeedId() {
		return ModItems.redstoneRoot;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context) {
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
	public PlantType getPlantType(IBlockReader level, BlockPos pos) {
		return PlantType.CROP;
	}
	
	public static void interactEvent(PlayerInteractEvent.RightClickBlock e) {
		ItemStack stack = e.getItemStack();
		BlockRayTraceResult hit = e.getHitVec();
		PlayerEntity player = e.getPlayer();
		Level level = e.getLevel();
		
		if(level != null && stack.getItem() == ModItems.redstoneRoot && hit != null && hit.getType() == RayTraceResult.Type.BLOCK) {
			//see BlockItemUseContext#offsetPos
			BlockPos targetPos = hit.getBlockPos().relative(hit.getDirection());
			
			BlockItemUseContext haha = new BlockItemUseContext(e.getPlayer(), e.getHand(), stack, hit);
			
			//this particular arrangement of targetPos and getFace is correct, see BucketItem
			if(e.getPlayer().mayUseItemAt(targetPos, hit.getDirection(), stack) &&
				level.getBlockState(targetPos).canBeReplaced(haha) &&
				//weird forge extension
				level.getBlockState(targetPos.below()).canSustainPlant(level, targetPos.below(), Direction.UP, IncBlocks.REDSTONE_ROOT_CROP)) {
				
				level.setBlockAndUpdate(targetPos, IncBlocks.REDSTONE_ROOT_CROP.defaultBlockState());
				
				SoundType type = SoundType.GRASS;
				SoundEvent sound = type.getPlaceSound(); 
				level.playSound(player, targetPos, sound, SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
				
				if(!player.isCreative()) stack.shrink(1);
				if(player instanceof ServerPlayerEntity) {
					CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, targetPos, stack);
				}
				
				e.setCanceled(true);
				e.setCancellationResult(ActionResultType.SUCCESS);
			}
		}
	}
}
