package agency.highlysuspect.rhododendrite.item;

import agency.highlysuspect.rhododendrite.computer.CorePredicate;
import net.minecraft.item.Item;

public class ConditionCardItem extends Item {
	public ConditionCardItem(Properties properties, CorePredicate predicate) {
		super(properties);
		this.predicate = predicate;
	}
	
	public final CorePredicate predicate;
}
