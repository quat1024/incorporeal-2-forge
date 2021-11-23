package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.core.helper.InventoryHelper;

import net.minecraft.block.AbstractBlock.Properties;

public class CorporeaSolidifierBlock extends Block {
	public CorporeaSolidifierBlock(Properties properties) {
		super(properties);
	}
	
	public void receiveRequest(World world, BlockPos pos, BlockState state, ICorporeaRequestMatcher request, int count) {
		if(world == null || world.isClientSide) return;
		
		ItemStack ticket = IncItems.CORPOREA_TICKET.produceForRequest(request, count);
		IItemHandler dest = getInv(world, pos);
		
		if(dest != null && ItemHandlerHelper.insertItemStacked(dest, ticket, false).isEmpty()) {
			ItemHandlerHelper.insertItemStacked(dest, ticket, true);
		} else {
			world.addFreshEntity(new ItemEntity(world,
				pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,
				ticket
			));
		}
	}
	
	//Modified from TileCorporeaFunnel.
	private IItemHandler getInv(World world, BlockPos pos) {
		//Try 1 block below
		IItemHandler ret = InventoryHelper.getInventory(world, pos.below(), Direction.UP);
		if (ret == null) ret = InventoryHelper.getInventory(world, pos.below(), null);
		if (ret != null) return ret;
		
		//Try 2 blocks below
		ret = InventoryHelper.getInventory(world, pos.below(2), Direction.UP);
		if (ret == null) ret = InventoryHelper.getInventory(world, pos.below(2), null);
		
		return ret;
	}
}
