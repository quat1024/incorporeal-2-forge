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

public class RhodoOpBlock extends AbstractComputerBlock {
	public RhodoOpBlock(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(BlockStateProperties.POWERED, false));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block from, BlockPos fromPos, boolean isMoving) {
		boolean shouldPower = world.getRedstonePowerFromNeighbors(pos) > 0;
		boolean isPowered = state.get(BlockStateProperties.POWERED);
		if(shouldPower != isPowered) {
			world.setBlockState(pos, state.with(BlockStateProperties.POWERED, shouldPower));
			if(shouldPower) {
				TileEntity tile = world.getTileEntity(pos);
				if(tile instanceof RhodoOpTile) {
					((RhodoOpTile) tile).onRedstonePower();
				}
			}
		}
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState blockState, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
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
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		//The lazy way, without using a block loot table.
		//My excuse: ChestBlock does it too.
		if(!state.isIn(newState.getBlock())) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof RhodoOpTile) {
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((RhodoOpTile) tile).getCard());
			}
		}
		
		super.onReplaced(state, world, pos, newState, isMoving);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoOpTile) {
			RhodoOpTile op = (RhodoOpTile) tile;
			ItemStack held = player.getHeldItem(hand);
			
			//This code is cursed don't touch it
			if((hand == Hand.MAIN_HAND && held.isEmpty()) || op.inventory.isItemValid(0, held)) {
				ItemStack toPut = held.isEmpty() ? ItemStack.EMPTY : held.split(1);
				
				ItemStack whatsInside = op.inventory.getStackInSlot(0);
				op.inventory.setStackInSlot(0, toPut);
				ItemHandlerHelper.giveItemToPlayer(player, whatsInside, player.inventory.currentItem);
				player.swingArm(hand);
			}
		}
		
		return ActionResultType.PASS;
	}
}
