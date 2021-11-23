package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.RhodoOpTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class RhodoOpBlock extends AbstractComputerBlock {
	public RhodoOpBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block from, BlockPos fromPos, boolean isMoving) {
		boolean shouldPower = world.getBestNeighborSignal(pos) > 0;
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		if(shouldPower != isPowered) {
			world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, shouldPower));
			if(shouldPower) {
				TileEntity tile = world.getBlockEntity(pos);
				if(tile instanceof RhodoOpTile) {
					((RhodoOpTile) tile).onRedstonePower();
				}
			}
		}
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState blockState, World world, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		return tile instanceof RhodoOpTile ? ((RhodoOpTile) tile).getComparatorSignal() : 0;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new RhodoOpTile();
	}
	
	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		//The lazy way, without using a block loot table.
		//My excuse: ChestBlock does it too.
		if(!state.is(newState.getBlock())) {
			TileEntity tile = world.getBlockEntity(pos);
			if(tile instanceof RhodoOpTile) {
				InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((RhodoOpTile) tile).getCard());
			}
		}
		
		super.onRemove(state, world, pos, newState, isMoving);
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity tile = world.getBlockEntity(pos);
		if(tile instanceof RhodoOpTile) {
			RhodoOpTile op = (RhodoOpTile) tile;
			ItemStack held = player.getItemInHand(hand);
			
			//This code is cursed don't touch it
			if((hand == Hand.MAIN_HAND && held.isEmpty()) || op.inventory.isItemValid(0, held)) {
				ItemStack toPut = held.isEmpty() ? ItemStack.EMPTY : held.split(1);
				
				ItemStack whatsInside = op.inventory.getStackInSlot(0);
				op.inventory.setStackInSlot(0, toPut);
				ItemHandlerHelper.giveItemToPlayer(player, whatsInside, player.inventory.selected);
				player.swing(hand);
			}
		}
		
		return ActionResultType.PASS;
	}
}
