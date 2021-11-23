package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.core.helper.InventoryHelper;

import net.minecraft.block.AbstractBlock.Properties;

public class CorporeaSolidifierBlock extends Block {
	public CorporeaSolidifierBlock(Properties properties) {
		super(properties);
	}
	
	public void receiveRequest(Level level, BlockPos pos, BlockState state, ICorporeaRequestMatcher request, int count) {
		if(level == null || level.isClientSide) return;
		
		ItemStack ticket = IncItems.CORPOREA_TICKET.produceForRequest(request, count);
		IItemHandler dest = getInv(level, pos);
		
		if(dest != null && ItemHandlerHelper.insertItemStacked(dest, ticket, false).isEmpty()) {
			ItemHandlerHelper.insertItemStacked(dest, ticket, true);
		} else {
			level.addFreshEntity(new ItemEntity(level,
				pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,
				ticket
			));
		}
	}
	
	//Modified from TileCorporeaFunnel.
	private IItemHandler getInv(Level level, BlockPos pos) {
		//Try 1 block below
		IItemHandler ret = InventoryHelper.getInventory(level, pos.below(), Direction.UP);
		if (ret == null) ret = InventoryHelper.getInventory(level, pos.below(), null);
		if (ret != null) return ret;
		
		//Try 2 blocks below
		ret = InventoryHelper.getInventory(level, pos.below(2), Direction.UP);
		if (ret == null) ret = InventoryHelper.getInventory(level, pos.below(2), null);
		
		return ret;
	}
}
