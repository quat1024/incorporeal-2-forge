package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.RhodoOpTile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class RhodoOpBlock extends AbstractComputerBlock {
	public RhodoOpBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block from, BlockPos fromPos, boolean isMoving) {
		boolean shouldPower = world.getBestNeighborSignal(pos) > 0;
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		if(shouldPower != isPowered) {
			world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, shouldPower));
			if(shouldPower) {
				BlockEntity tile = world.getBlockEntity(pos);
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
	public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
		BlockEntity tile = world.getBlockEntity(pos);
		return tile instanceof RhodoOpTile ? ((RhodoOpTile) tile).getComparatorSignal() : 0;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new RhodoOpTile();
	}
	
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		//The lazy way, without using a block loot table.
		//My excuse: ChestBlock does it too.
		if(!state.is(newState.getBlock())) {
			BlockEntity tile = world.getBlockEntity(pos);
			if(tile instanceof RhodoOpTile) {
				Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((RhodoOpTile) tile).getCard());
			}
		}
		
		super.onRemove(state, world, pos, newState, isMoving);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity tile = world.getBlockEntity(pos);
		if(tile instanceof RhodoOpTile) {
			RhodoOpTile op = (RhodoOpTile) tile;
			ItemStack held = player.getItemInHand(hand);
			
			//This code is cursed don't touch it
			if((hand == InteractionHand.MAIN_HAND && held.isEmpty()) || op.inventory.isItemValid(0, held)) {
				ItemStack toPut = held.isEmpty() ? ItemStack.EMPTY : held.split(1);
				
				ItemStack whatsInside = op.inventory.getStackInSlot(0);
				op.inventory.setStackInSlot(0, toPut);
				ItemHandlerHelper.giveItemToPlayer(player, whatsInside, player.inventory.selected);
				player.swing(hand);
			}
		}
		
		return InteractionResult.PASS;
	}
}
