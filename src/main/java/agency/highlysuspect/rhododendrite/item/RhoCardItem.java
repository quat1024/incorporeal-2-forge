package agency.highlysuspect.rhododendrite.item;

import agency.highlysuspect.rhododendrite.block.tile.RhodoCellTile;
import agency.highlysuspect.rhododendrite.block.tile.RhodoOpTile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class RhoCardItem extends Item {
	public RhoCardItem(Properties properties, CellAction action, boolean isCondition) {
		super(properties);
		this.action = action;
		this.isCondition = isCondition;
	}
	
	public static RhoCardItem op(Properties properties, CellAction action) {
		return new RhoCardItem(properties, action, false);
	}
	
	public static RhoCardItem cond(Properties properties, CellPredicate pred) {
		return new RhoCardItem(properties, (cell, op) -> op.setComparatorSignal(pred.test(cell, op) ? 15 : 0), true);
	}
	
	//Convenience
	public static @Nullable
	RhoCardItem extract(ItemStack stack) {
		if(stack.getItem() instanceof RhoCardItem) return (RhoCardItem) stack.getItem();
		else return null;
	}
	
	public final CellAction action;
	public final boolean isCondition;
	
	public interface CellAction {
		void run(RhodoCellTile cell, RhodoOpTile op);
	}
	
	public interface CellPredicate {
		boolean test(RhodoCellTile cell, RhodoOpTile op);
	}
}
