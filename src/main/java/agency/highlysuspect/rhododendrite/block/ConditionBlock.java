package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.ConditionTile;
import agency.highlysuspect.rhododendrite.block.tile.RhoTileTypes;
import agency.highlysuspect.rhododendrite.block.tile.RhodoNetworkTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nullable;

public class ConditionBlock extends Block implements IWandable {
	public ConditionBlock(Properties properties) {
		super(properties);
	}
	
	private static final VoxelShape SHAPE = makeCuboidShape(3.0, 0, 3.0, 13.0, 16, 13.0);
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof ConditionTile) return ((ConditionTile) tile).getComparator();
		else return 0;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return RhoTileTypes.CONDITION.create();
	}
	
	@Override
	public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoNetworkTile) {
			((RhodoNetworkTile) tile).whenWanded();
			return true;
		} else return false;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoNetworkTile) ((RhodoNetworkTile) tile).whenPlaced();
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		//The lazy way, without using a block loot table.
		//My excuse: ChestBlock does it too
		if(!state.isIn(newState.getBlock())) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof ConditionTile) {
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((ConditionTile) tile).inventory.getStackInSlot(0));
			}
			
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof ConditionTile) {
			ConditionTile cond = (ConditionTile) tile;
			
			ItemStack held = player.getHeldItem(hand);
			
			if((hand == Hand.MAIN_HAND && held.isEmpty()) || cond.inventory.isItemValid(0, held)) {
				ItemStack toPut = held.isEmpty() ? ItemStack.EMPTY : held.split(1);
				
				ItemStack whatsInside = cond.inventory.getStackInSlot(0);
				cond.inventory.setStackInSlot(0, toPut);
				ItemHandlerHelper.giveItemToPlayer(player, whatsInside, player.inventory.currentItem);
				player.swingArm(hand);
			}
		}
		
		return ActionResultType.PASS;
	}
}
