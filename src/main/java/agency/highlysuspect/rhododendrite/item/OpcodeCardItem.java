package agency.highlysuspect.rhododendrite.item;

import agency.highlysuspect.rhododendrite.computer.CoreAction;
import net.minecraft.item.Item;

public class OpcodeCardItem extends Item {
	public OpcodeCardItem(Properties properties, CoreAction action) {
		super(properties);
		this.action = action;
	}
	
	public final CoreAction action;
}
